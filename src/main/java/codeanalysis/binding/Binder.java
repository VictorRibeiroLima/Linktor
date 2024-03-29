package codeanalysis.binding;

import codeanalysis.binding.conversion.BoundConversionExpression;
import codeanalysis.binding.conversion.Conversion;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.expression.assignment.BoundAssignmentExpression;
import codeanalysis.binding.expression.assignment.BoundCompoundAssignmentExpression;
import codeanalysis.binding.expression.binary.BoundBinaryExpression;
import codeanalysis.binding.expression.binary.BoundBinaryOperator;
import codeanalysis.binding.expression.call.BoundCallExpression;
import codeanalysis.binding.expression.error.BoundErrorExpression;
import codeanalysis.binding.expression.literal.BoundLiteralExpression;
import codeanalysis.binding.expression.sufixpreffix.BoundPrefixExpression;
import codeanalysis.binding.expression.sufixpreffix.BoundPrefixSuffixOperator;
import codeanalysis.binding.expression.sufixpreffix.BoundSuffixExpression;
import codeanalysis.binding.expression.unary.BoundUnaryExpression;
import codeanalysis.binding.expression.unary.BoundUnaryOperator;
import codeanalysis.binding.expression.variable.BoundVariableExpression;
import codeanalysis.binding.scopes.BoundGlobalScope;
import codeanalysis.binding.scopes.BoundScope;
import codeanalysis.binding.scopes.identifier.FunctionIdentifier;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.binding.statement.block.BoundBlockStatement;
import codeanalysis.binding.statement.conditional.BoundElseClause;
import codeanalysis.binding.statement.conditional.BoundIfStatement;
import codeanalysis.binding.statement.declaration.BoundVariableDeclarationStatement;
import codeanalysis.binding.statement.expression.BoundExpressionStatement;
import codeanalysis.binding.statement.expression.BoundReturnStatement;
import codeanalysis.binding.statement.jumpto.BoundJumpToStatement;
import codeanalysis.binding.statement.jumpto.BoundLabel;
import codeanalysis.binding.statement.loop.BoundForConditionClause;
import codeanalysis.binding.statement.loop.BoundForStatement;
import codeanalysis.binding.statement.loop.BoundWhileStatement;
import codeanalysis.controlflow.ControlFlowGraph;
import codeanalysis.diagnostics.DiagnosticBag;
import codeanalysis.lowering.Lowerer;
import codeanalysis.source.TextLocation;
import codeanalysis.symbol.BuildInFunctions;
import codeanalysis.symbol.FunctionSymbol;
import codeanalysis.symbol.ParameterSymbol;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxFacts;
import codeanalysis.syntax.SyntaxKind;
import codeanalysis.syntax.SyntaxToken;
import codeanalysis.syntax.SyntaxTree;
import codeanalysis.syntax.clause.ElseClauseSyntax;
import codeanalysis.syntax.clause.ForConditionClauseSyntax;
import codeanalysis.syntax.clause.ParameterClauseSyntax;
import codeanalysis.syntax.clause.TypeClauseSyntax;
import codeanalysis.syntax.expression.*;
import codeanalysis.syntax.member.FunctionMemberSyntax;
import codeanalysis.syntax.member.GlobalMemberSyntax;
import codeanalysis.syntax.member.MemberSyntax;
import codeanalysis.syntax.statements.*;

import java.util.*;

public class Binder {
    private record LoopStack(BoundLabel breakLabel, BoundLabel continueLabel) {
    }

    private final Stack<LoopStack> loopStack = new Stack<>();
    private int labelCount = 0;
    private final DiagnosticBag diagnostics = new DiagnosticBag();
    private final FunctionSymbol function;

    private BoundScope scope;


    private Binder(BoundScope parent) {
        this(parent, null);
    }

    private Binder(BoundScope parent, FunctionSymbol function) {
        this.function = function;
        scope = new BoundScope(parent);
        if (function != null) {
            for (ParameterSymbol p : function.getParameters())
                scope.declareVariable(p);
        }
    }

    public DiagnosticBag getDiagnostics() {
        return diagnostics;
    }

    public static BoundGlobalScope bindGlobalScope(List<SyntaxTree> trees, BoundGlobalScope previous) throws Exception {
        BoundScope parent = createParentScope(previous);
        Binder binder = new Binder(parent);
        List<FunctionSymbol> functionSymbols = getBoundFunctions(trees, binder);
        List<BoundStatement> statements = getBoundStatements(trees, binder);
        List<BoundStatement> statement = List.copyOf(statements);
        List<VariableSymbol> variables = binder.scope.getDeclaredVariables();
        var diagnostics = binder.getDiagnostics();
        var mainFunction = getMainFunction(trees, functionSymbols, statements, diagnostics);


        return new BoundGlobalScope(previous, List.copyOf(diagnostics.getDiagnostics()), variables, functionSymbols, statement, mainFunction);
    }

    public static BoundProgram bindProgram(BoundProgram previous, BoundGlobalScope global) throws Exception {
        BoundScope parent = createParentScope(global);
        Map<FunctionSymbol, BoundBlockStatement> functionsBodies = new HashMap<>();
        DiagnosticBag diagnostics = new DiagnosticBag();

        for (FunctionSymbol function : global.getFunctions()) {
            Binder binder = new Binder(parent, function);
            BoundBlockStatement body = binder.bindBlockStatement(function.getDeclaration().getBody());
            BoundBlockStatement loweredBody = Lowerer.lower(function, body);
            if (function.getType() != TypeSymbol.VOID && !ControlFlowGraph.allPathsReturn(loweredBody))
                diagnostics.reportAllPathMustReturn(function.getDeclaration().getIdentifier().getLocation());

            functionsBodies.put(function, loweredBody);
            diagnostics.addAll(binder.getDiagnostics());
        }

        var mainFunction = global.getMainFunction();
        if (mainFunction != null && !global.getStatements().isEmpty()) {
            BoundBlockStatement statement = Lowerer.lower(mainFunction, new BoundBlockStatement(global.getStatements()));
            functionsBodies.put(mainFunction, statement);
        }
        return new BoundProgram(previous, diagnostics, functionsBodies, mainFunction);
    }

    private static List<BoundStatement> getBoundStatements(List<SyntaxTree> trees, Binder binder) throws Exception {
        List<GlobalMemberSyntax> globalMembers = new ArrayList<>();
        List<BoundStatement> statements = new ArrayList<>();
        trees.forEach(tree -> tree.getRoot().getMembers().forEach(memberSyntax -> {
            if (memberSyntax instanceof GlobalMemberSyntax g)
                globalMembers.add(g);
        }));

        for (GlobalMemberSyntax g : globalMembers) {
            BoundStatement s = binder.bindStatement(g.getStatement());
            statements.add(s);
        }
        return statements;
    }

    private static BoundScope createParentScope(BoundGlobalScope previous) {
        Stack<BoundGlobalScope> stack = new Stack<>();
        while (previous != null) {
            stack.add(previous);
            previous = previous.getPrevious();
        }

        BoundScope parentScope = genRootScope();

        while (!stack.empty()) {
            previous = stack.pop();
            BoundScope scope = new BoundScope(parentScope);
            for (VariableSymbol variable : previous.getVariables()) {
                scope.declareVariable(variable);
            }
            for (FunctionSymbol function : previous.getFunctions()) {
                scope.declareFunction(function);
            }
            parentScope = scope;
        }
        return parentScope;
    }

    private static BoundScope genRootScope() {
        BoundScope root = new BoundScope(null);
        for (FunctionSymbol f : BuildInFunctions.getAll())
            root.declareFunction(f);
        return root;
    }

    private void bindFunctionDeclaration(FunctionMemberSyntax f) {
        List<ParameterSymbol> params = new ArrayList<>();
        Set<String> seeParamNames = new HashSet<>();
        List<TypeSymbol> paramsTypes = new ArrayList<>();
        for (ParameterClauseSyntax param : f.getParams()) {
            String name = param.getIdentifier().getText();
            TypeSymbol type = bindTypeClause(param.getType());
            if (!seeParamNames.add(name)) {
                diagnostics.reportDuplicatedParam(param.getLocation(), name);
            } else {
                ParameterSymbol parameter = new ParameterSymbol(name, type);
                paramsTypes.add(type);
                params.add(parameter);
            }
        }
        TypeSymbol funcType = bindTypeClause(f.getType());
        if (funcType == null)
            funcType = TypeSymbol.VOID;
        FunctionIdentifier identifier = new FunctionIdentifier(f.getIdentifier().getText(), paramsTypes);
        FunctionSymbol function = new FunctionSymbol(f.getIdentifier().getText(), params, funcType, f);
        if (scope.isFunctionPresent(identifier)) {
            diagnostics.reportFunctionAlreadyDeclared(f.getIdentifier().getLocation(), f.getIdentifier().getText(), paramsTypes);
        } else {
            scope.declareFunction(function);
        }

    }

    private BoundStatement bindErrorStatement() {
        return new BoundExpressionStatement(new BoundErrorExpression());
    }

    private BoundStatement bindStatement(StatementSyntax syntax) throws Exception {
        var result = switch (syntax.getKind()) {
            case BLOCK_STATEMENT -> bindBlockStatement((BlockStatementSyntax) syntax);
            case EXPRESSION_STATEMENT -> bindExpressionStatement((ExpressionStatementSyntax) syntax);
            case VARIABLE_DECLARATION_STATEMENT ->
                    bindVariableDeclarationStatement((VariableDeclarationStatementSyntax) syntax);
            case IF_STATEMENT -> bindIfStatement((IfStatementSyntax) syntax);
            case WHILE_STATEMENT -> bindWhileStatement((WhileStatementSyntax) syntax);
            case FOR_STATEMENT -> bindForStatement((ForStatementSyntax) syntax);
            case CONTINUE_STATEMENT -> bindContinueStatement((ContinueStatementSyntax) syntax);
            case BREAK_STATEMENT -> bindBreakStatement((BreakStatementSyntax) syntax);
            case RETURN_STATEMENT -> bindReturnStatement((ReturnStatementSyntax) syntax);
            default -> throw new Exception("ERROR: unexpected syntax: " + syntax.getKind());
        };
        if (result instanceof BoundExpressionStatement b) {
            switch (b.getExpression().getKind()) {
                case ERROR_EXPRESSION,
                        ASSIGNMENT_EXPRESSION,
                        VARIABLE_DECLARATION_STATEMENT,
                        CALL_EXPRESSION,
                        PREFIX_EXPRESSION,
                        SUFFIX_EXPRESSION,
                        COMPOUND_ASSIGNMENT_EXPRESSION -> {
                }
                default -> diagnostics.reportInvalidExpressionStatement(syntax.getLocation());
            }
        }

        return result;
    }

    private BoundStatement bindReturnStatement(ReturnStatementSyntax syntax) throws Exception {
        var hasExpression = syntax.getExpression() != null;
        var expression = hasExpression ? bindExpression(syntax.getExpression()) : null;
        if (this.function == null) {
            diagnostics.reportReturnOutsideFunction(syntax.getKeyword().getLocation());
        } else {
            if (function.getType() == TypeSymbol.VOID) {
                if (hasExpression)
                    diagnostics.reportReturnOnVoid(syntax.getExpression().getLocation());
            } else {
                if (!hasExpression)
                    diagnostics.reportMissingReturnExpression(syntax.getExpression().getLocation(), function.getType());
                else
                    expression = bindConversion(function.getType(), expression, syntax.getExpression().getLocation());
            }
        }
        return new BoundReturnStatement(expression);
    }

    private BoundStatement bindForStatement(ForStatementSyntax syntax) throws Exception {
        this.scope = new BoundScope(scope);
        BoundForConditionClause clause = bindForConditionClause(syntax.getCondition());
        BoundLabel[] labels = genLoopLabels();
        BoundLabel breakLabel = labels[0];
        BoundLabel continueLabel = labels[1];
        BoundStatement body = bindLoopBody(syntax.getThenStatement(), breakLabel, continueLabel);
        scope = scope.getParent();
        return new BoundForStatement(clause, body, breakLabel, continueLabel);
    }

    private BoundForConditionClause bindForConditionClause(ForConditionClauseSyntax condition) throws Exception {
        BoundNode variable;
        if (condition.getVariableNode().getKind() == SyntaxKind.VARIABLE_DECLARATION_STATEMENT)
            variable = bindVariableDeclarationStatement((VariableDeclarationStatementSyntax) condition.getVariableNode());
        else
            variable = bindNameExpression((NameExpressionSyntax) condition.getVariableNode());

        BoundExpression conditionExpression = bindExpression(condition.getConditionExpression(), TypeSymbol.BOOLEAN);
        BoundExpression incrementExpression = bindExpression(condition.getIncrementExpression(), TypeSymbol.INTEGER);
        return new BoundForConditionClause(variable, conditionExpression, incrementExpression);
    }

    private BoundStatement bindWhileStatement(WhileStatementSyntax syntax) throws Exception {
        BoundExpression condition = bindExpression(syntax.getCondition(), TypeSymbol.BOOLEAN);
        BoundLabel[] labels = genLoopLabels();
        BoundLabel breakLabel = labels[0];
        BoundLabel continueLabel = labels[1];
        BoundStatement body = bindLoopBody(syntax.getThenStatement(), breakLabel, continueLabel);
        return new BoundWhileStatement(condition, body, breakLabel, continueLabel);
    }

    private BoundLabel[] genLoopLabels() {
        labelCount++;
        BoundLabel breakLabel = new BoundLabel("break" + labelCount);
        BoundLabel continueLabel = new BoundLabel("continue" + labelCount);
        return new BoundLabel[]{breakLabel, continueLabel};
    }

    private BoundStatement bindLoopBody(StatementSyntax syntax, BoundLabel breakLabel, BoundLabel continueLabel) throws Exception {
        loopStack.push(new LoopStack(breakLabel, continueLabel));
        BoundStatement body = bindStatement(syntax);
        loopStack.pop();
        return body;
    }

    private BoundStatement bindBreakStatement(BreakStatementSyntax syntax) {
        if (loopStack.isEmpty()) {
            diagnostics.reportInvalidBreakOrContinue(syntax.getKeyword().getLocation(), syntax.getKeyword().getText());
            return bindErrorStatement();
        }
        return new BoundJumpToStatement(loopStack.peek().breakLabel());
    }

    private BoundStatement bindContinueStatement(ContinueStatementSyntax syntax) {
        if (loopStack.isEmpty()) {
            diagnostics.reportInvalidBreakOrContinue(syntax.getKeyword().getLocation(), syntax.getKeyword().getText());
            return bindErrorStatement();
        }
        return new BoundJumpToStatement(loopStack.peek().continueLabel());
    }


    private BoundStatement bindIfStatement(IfStatementSyntax syntax) throws Exception {
        BoundExpression condition = bindExpression(syntax.getCondition(), TypeSymbol.BOOLEAN);
        BoundStatement thenStatement = bindStatement(syntax.getThenStatement());
        BoundElseClause elseClause = bindElseClause(syntax.getElseClause());
        return new BoundIfStatement(condition, thenStatement, elseClause);
    }

    private BoundElseClause bindElseClause(ElseClauseSyntax elseClause) throws Exception {
        if (elseClause != null) {
            BoundStatement thenStatement = bindStatement(elseClause.getThenStatement());
            return new BoundElseClause(thenStatement);
        }
        return null;
    }

    private BoundStatement bindVariableDeclarationStatement(VariableDeclarationStatementSyntax syntax) throws Exception {
        BoundExpression initializer = bindInitializer(syntax);
        VariableSymbol variableSymbol = bindVariable(syntax, initializer);

        return new BoundVariableDeclarationStatement(variableSymbol, initializer);
    }

    private VariableSymbol bindVariable(VariableDeclarationStatementSyntax syntax, BoundExpression initializer) {
        boolean isReadOnly = syntax.getKeyword().getKind() == SyntaxKind.LET_KEYWORD;
        boolean declare = !syntax.getIdentifier().isMissing();
        String name = syntax.getIdentifier().getText() == null ? "?" : syntax.getIdentifier().getText();
        VariableSymbol variableSymbol = new VariableSymbol(name, initializer.getType(), isReadOnly);
        if (declare && !scope.declareVariable(variableSymbol))
            diagnostics.reportVariableAlreadyDeclared(name, syntax.getIdentifier().getLocation());
        return variableSymbol;
    }

    private BoundExpression bindInitializer(VariableDeclarationStatementSyntax syntax) throws Exception {
        TypeSymbol type = bindTypeClause(syntax.getType());
        BoundExpression initializer;
        if (type != null)
            initializer = bindExpression(syntax.getInitializer(), type);
        else
            initializer = bindExpression(syntax.getInitializer());
        return initializer;
    }

    private TypeSymbol bindTypeClause(TypeClauseSyntax clause) {
        if (clause == null)
            return null;
        TypeSymbol type = lookupType(clause.getIdentifierToken().getText());
        if (type == TypeSymbol.ERROR)
            diagnostics.reportUndefinedType(clause.getLocation(), clause.getIdentifierToken().getText());

        return type;
    }

    private BoundBlockStatement bindBlockStatement(BlockStatementSyntax syntax) throws Exception {
        List<BoundStatement> statements = new ArrayList<>();
        scope = new BoundScope(scope);
        for (StatementSyntax e : syntax.getStatements()
        ) {
            BoundStatement statement = bindStatement(e);
            statements.add(statement);
        }
        scope = scope.getParent();
        return new BoundBlockStatement(statements);
    }

    private BoundExpressionStatement bindExpressionStatement(ExpressionStatementSyntax syntax) throws Exception {
        BoundExpression expression = bindExpression(syntax.getExpression(), true);
        return new BoundExpressionStatement(expression);
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax, TypeSymbol expectedType) throws Exception {
        BoundExpression result = bindExpression(syntax);
        Conversion conversion = Conversion.classify(result.getType(), expectedType);
        if (conversion.isIdentity()) {
            return result;
        }
        if (conversion.isImplicit())
            return new BoundConversionExpression(expectedType, result);
        if (
                !result.getType().equals(expectedType) &&
                        !result.getType().equals(TypeSymbol.ERROR) &&
                        !expectedType.equals(TypeSymbol.ERROR)
        ) {
            diagnostics.reportCannotConvert(syntax.getLocation(), expectedType, result.getType());
        }

        return result;
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax) throws Exception {
        return bindExpression(syntax, false);
    }

    private BoundExpression bindExpression(ExpressionSyntax syntax, boolean canBeVoid) throws Exception {
        BoundExpression result = switch (syntax.getKind()) {
            case PARENTHESIZED_EXPRESSION -> bindParenthesizedExpression((ParenthesizedExpressionSyntax) syntax);
            case LITERAL_EXPRESSION -> bindLiteralExpression((LiteralExpressionSyntax) syntax);
            case NAME_EXPRESSION -> bindNameExpression((NameExpressionSyntax) syntax);
            case ASSIGNMENT_EXPRESSION -> bindAssignmentExpression((AssignmentExpressionSyntax) syntax);
            case UNARY_EXPRESSION -> bindUnaryExpression((UnaryExpressionSyntax) syntax);
            case BINARY_EXPRESSION -> bindBinaryExpression((BinaryExpressionSyntax) syntax);
            case CALL_EXPRESSION -> bindCallExpression((CallExpressionSyntax) syntax);
            case PREFIX_EXPRESSION -> bindPrefixExpression((PrefixExpressionSyntax) syntax);
            case SUFFIX_EXPRESSION -> bindSuffixExpression((SuffixExpressionSyntax) syntax);
            default -> throw new Exception("ERROR: unexpected syntax: " + syntax.getKind());
        };
        if (!canBeVoid && result.getType() == TypeSymbol.VOID) {
            diagnostics.reportExpressionMustHaveValue(syntax.getLocation());
            return new BoundErrorExpression();
        }
        return result;
    }

    private BoundExpression bindCallExpression(CallExpressionSyntax syntax) throws Exception {

        //this generates the conversion methods string() int() boolean() etc..
        TypeSymbol type = lookupType(syntax.getIdentifier().getText());
        if (syntax.getArgs().getCount() == 1 && type != TypeSymbol.ERROR)
            return bindConversion(type, syntax.getArgs().get(0));


        List<BoundExpression> boundArgs = new ArrayList<>();
        List<TypeSymbol> usedTypes = new ArrayList<>();
        List<TypeSymbol> anyTypes = new ArrayList<>();

        for (ExpressionSyntax arg : syntax.getArgs()) {
            BoundExpression expression = bindExpression(arg);
            if (expression.getType() == TypeSymbol.ERROR)
                return new BoundErrorExpression();
            boundArgs.add(expression);
            usedTypes.add(expression.getType());
            anyTypes.add(TypeSymbol.ANY);
        }
        FunctionIdentifier identifier = new FunctionIdentifier(syntax.getIdentifier().getText(), usedTypes);
        FunctionIdentifier anyIdentifier = new FunctionIdentifier(syntax.getIdentifier().getText(), anyTypes);
        if (!scope.isFunctionPresent(identifier)) {
            if (!scope.isFunctionPresent(anyIdentifier)) {
                diagnostics.reportUndefinedFunction(syntax.getLocation(), syntax.getIdentifier().getText(), usedTypes);
                return new BoundErrorExpression();
            } else {
                FunctionSymbol function = scope.getFunctionsByIdentifier(anyIdentifier);
                return new BoundCallExpression(function, boundArgs);
            }
        }
        FunctionSymbol function = scope.getFunctionsByIdentifier(identifier);
        return new BoundCallExpression(function, boundArgs);
    }

    private BoundExpression bindConversion(TypeSymbol type, ExpressionSyntax syntax) throws Exception {
        BoundExpression expression = bindExpression(syntax);
        Conversion conversion = Conversion.classify(expression.getType(), type);
        if (!conversion.isExists()) {
            if (expression.getType() != TypeSymbol.ERROR && type != TypeSymbol.ERROR)
                diagnostics.reportCannotConvert(syntax.getLocation(), expression.getType(), type);
            return new BoundErrorExpression();
        }
        if (conversion.isIdentity())
            return expression;
        return new BoundConversionExpression(type, expression);
    }

    private BoundExpression bindConversion(TypeSymbol type, BoundExpression expression, TextLocation location) {
        Conversion conversion = Conversion.classify(expression.getType(), type);
        if (!conversion.isExists()) {
            if (expression.getType() == TypeSymbol.ERROR && type == TypeSymbol.ERROR)
                diagnostics.reportCannotConvert(location, expression.getType(), type);
            return new BoundErrorExpression();
        }
        if (conversion.isExplicit()) {
            diagnostics.reportCannotConvert(location, expression.getType(), type);
        }
        if (conversion.isIdentity())
            return expression;
        return new BoundConversionExpression(type, expression);
    }


    private BoundExpression bindParenthesizedExpression(ParenthesizedExpressionSyntax syntax) throws Exception {
        return bindExpression(syntax.getExpression());
    }

    private BoundExpression bindNameExpression(NameExpressionSyntax syntax) {
        if (syntax.getIdentifierToken().isMissing())
            return new BoundErrorExpression();
        VariableSymbol variable = getVariable(syntax.getIdentifierToken());
        if (variable == null)
            return new BoundErrorExpression();
        return new BoundVariableExpression(variable);
    }

    private BoundExpression bindPrefixExpression(PrefixExpressionSyntax syntax) {
        var variable = getVariable(syntax.getIdentifier());
        var token = syntax.getToken();
        if (variable == null)
            return new BoundErrorExpression();
        var operator = BoundPrefixSuffixOperator.bind(token.getKind(), variable.getType());
        if (variable.isReadOnly()) {
            diagnostics.reportReadOnly(token.getLocation(), syntax.getIdentifier().getText());
        }
        if (operator == null) {
            diagnostics.reportUndefinedOperator(syntax.getToken().getLocation(),
                    syntax.getToken().getText(), variable.getType());
            return new BoundErrorExpression();
        }
        return new BoundPrefixExpression(operator, variable);
    }

    private BoundExpression bindSuffixExpression(SuffixExpressionSyntax syntax) {
        var variable = getVariable(syntax.getIdentifier());
        var token = syntax.getToken();
        if (variable == null)
            return new BoundErrorExpression();
        var operator = BoundPrefixSuffixOperator.bind(token.getKind(), variable.getType());
        if (variable.isReadOnly()) {
            diagnostics.reportReadOnly(token.getLocation(), syntax.getIdentifier().getText());
        }
        if (operator == null) {
            diagnostics.reportUndefinedOperator(syntax.getToken().getLocation(),
                    syntax.getToken().getText(), variable.getType());
            return new BoundErrorExpression();
        }
        return new BoundSuffixExpression(variable, operator);
    }

    private BoundExpression bindAssignmentExpression(AssignmentExpressionSyntax syntax) throws Exception {
        String name = syntax.getIdentifierToken().getText();
        VariableSymbol variable = getVariable(syntax.getIdentifierToken());
        if (variable == null)
            return new BoundErrorExpression();
        if (variable.isReadOnly()) {
            diagnostics.reportReadOnly(syntax.getOperatorToken().getLocation(), name);
        }
        var boundExpression = bindExpression(syntax.getExpression(), variable.getType());
        if (boundExpression.getKind() == BoundNodeKind.ERROR_EXPRESSION) {
            return new BoundErrorExpression();
        }
        if (syntax.getOperatorToken().getKind() != SyntaxKind.EQUAL_TOKEN) {
            var equivalentOperatorTokenKind = SyntaxFacts.getBinaryOperatorOfAssignmentOperator(syntax.getOperatorToken().getKind());
            var operation = BoundBinaryOperator.bind(equivalentOperatorTokenKind, variable.getType(), boundExpression.getType());
            if (operation == null) {
                diagnostics.reportUndefinedOperator(syntax.getOperatorToken().getLocation(),
                        syntax.getOperatorToken().getText(), boundExpression.getType());
                return new BoundErrorExpression();
            }
            return new BoundCompoundAssignmentExpression(variable, operation, boundExpression);
        }
        return new BoundAssignmentExpression(variable, boundExpression);
    }

    private VariableSymbol getVariable(SyntaxToken identifier) {
        var name = identifier.getText();
        if (!scope.isVariablePresent(name)) {
            diagnostics.reportUndefinedNameExpression(identifier.getLocation(), name);
            return null;
        }
        return scope.getVariableByIdentifier(name);
    }


    private BoundExpression bindLiteralExpression(LiteralExpressionSyntax syntax) {
        Object value = syntax.getValue() != null ? syntax.getValue() : 0;
        return new BoundLiteralExpression(value);
    }

    private BoundExpression bindUnaryExpression(UnaryExpressionSyntax syntax) throws Exception {
        BoundExpression right = bindExpression(syntax.getRight());
        if (right.getType().equals(TypeSymbol.ERROR))
            return new BoundErrorExpression();
        BoundUnaryOperator operator = BoundUnaryOperator.bind(syntax.getOperatorToken().getKind(), right.getType());
        if (operator == null) {
            diagnostics.reportUndefinedUnaryOperator(syntax.getOperatorToken().getLocation(),
                    syntax.getOperatorToken().getText(), right.getType());
            return new BoundErrorExpression();
        }
        return new BoundUnaryExpression(operator, right);
    }

    private BoundExpression bindBinaryExpression(BinaryExpressionSyntax syntax) throws Exception {
        BoundExpression left = bindExpression(syntax.getLeft());
        BoundExpression right = bindExpression(syntax.getRight());
        if (left.getType().equals(TypeSymbol.ERROR) || right.getType().equals(TypeSymbol.ERROR))
            return new BoundErrorExpression();
        BoundBinaryOperator operator = BoundBinaryOperator.bind(syntax.getOperatorToken().getKind(), left.getType(), right.getType());
        if (operator == null) {
            diagnostics.reportUndefinedBinaryOperator(syntax.getOperatorToken().getLocation(),
                    syntax.getOperatorToken().getText(), left.getType(), right.getType());

            return new BoundErrorExpression();
        }
        return new BoundBinaryExpression(left, operator, right);
    }

    private TypeSymbol lookupType(String name) {
        if (name == null)
            return TypeSymbol.ERROR;
        return switch (name) {
            case "boolean" -> TypeSymbol.BOOLEAN;
            case "int" -> TypeSymbol.INTEGER;
            case "string" -> TypeSymbol.STRING;
            case "any" -> TypeSymbol.ANY;
            default -> TypeSymbol.ERROR;
        };
    }


    private static FunctionSymbol getMainFunction(List<SyntaxTree> trees, List<FunctionSymbol> functionSymbols, List<BoundStatement> statements, DiagnosticBag diagnostics) {
        var firstGSPerTree = getFirstStatementPerTree(trees);
        var main = functionSymbols.stream().filter(f -> f.getName().equals("main")).findFirst();
        if (firstGSPerTree.size() > 1) {
            for (var gS : firstGSPerTree)
                diagnostics.reportGlobalStatementInMultipleFiles(gS.getLocation());
        }

        //TODO: Check this latter
        if (!statements.isEmpty()) {
            if (main.isPresent()) {
                checkMainFunction(firstGSPerTree, main.get(), diagnostics);
                return main.get();
            }
            return new FunctionSymbol("main", List.of(), TypeSymbol.VOID, null);
        } else if (main.isPresent()) {
            checkMainFunction(firstGSPerTree, main.get(), diagnostics);
            return main.get();
        }
        return null;
    }

    private static void checkMainFunction(List<MemberSyntax> globalStatements, FunctionSymbol main, DiagnosticBag diagnostics) {
        if (main.getType() != TypeSymbol.VOID || !main.getParameters().isEmpty())
            diagnostics.reportWrongMainSignature(main.getDeclaration().getIdentifier().getLocation());

        if (!globalStatements.isEmpty()) {
            diagnostics.reportMixMainGlobalStatement(main.getDeclaration().getIdentifier().getLocation());
            for (var gS : globalStatements) {
                diagnostics.reportMixMainGlobalStatement(gS.getLocation());
            }
        }
        diagnostics.toUnmodifiableList();
    }

    private static List<MemberSyntax> getFirstStatementPerTree(List<SyntaxTree> trees) {
        return trees.stream().map(
                tree -> tree
                        .getRoot()
                        .getMembers()
                        .stream()
                        .filter(memberSyntax ->
                                memberSyntax instanceof GlobalMemberSyntax
                        ).findFirst()
        ).flatMap(Optional::stream).toList();
    }

    private static List<FunctionSymbol> getBoundFunctions(List<SyntaxTree> trees, Binder binder) {
        List<FunctionMemberSyntax> functionMembers = new ArrayList<>();
        trees.forEach(tree -> tree.getRoot().getMembers().forEach(memberSyntax -> {
            if (memberSyntax instanceof FunctionMemberSyntax f)
                functionMembers.add(f);
        }));
        for (FunctionMemberSyntax f : functionMembers)
            binder.bindFunctionDeclaration(f);

        return binder.scope.getDeclaredFunctions();

    }
}
