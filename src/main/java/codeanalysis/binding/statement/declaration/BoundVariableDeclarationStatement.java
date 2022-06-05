package codeanalysis.binding.statement.declaration;

import codeanalysis.binding.BoundNode;
import codeanalysis.binding.BoundNodeKind;
import codeanalysis.binding.expression.BoundExpression;
import codeanalysis.binding.statement.BoundStatement;
import codeanalysis.symbol.variable.VariableSymbol;

import java.util.List;

public class BoundVariableDeclarationStatement extends BoundStatement {
    private final VariableSymbol variable;
    private final BoundExpression initializer;
    private final BoundNodeKind kind;

    private final List<BoundNode> children;

    public BoundVariableDeclarationStatement(VariableSymbol variable, BoundExpression initializer) {
        this.variable = variable;
        this.initializer = initializer;
        this.kind = BoundNodeKind.VARIABLE_DECLARATION_STATEMENT;
        this.children = List.of(initializer);
    }

    @Override
    public List<BoundNode> getChildren() {
        return children;
    }

    public VariableSymbol getVariable() {
        return variable;
    }

    public BoundExpression getInitializer() {
        return initializer;
    }

    @Override
    public BoundNodeKind getKind() {
        return kind;
    }
}
