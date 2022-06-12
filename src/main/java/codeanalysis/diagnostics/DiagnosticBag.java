package codeanalysis.diagnostics;

import codeanalysis.source.TextLocation;
import codeanalysis.symbol.TypeSymbol;
import codeanalysis.syntax.SyntaxKind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiagnosticBag implements Iterable<Diagnostic> {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    @Override
    public Iterator<Diagnostic> iterator() {
        return diagnostics.iterator();
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }


    public boolean isEmpty() {
        return diagnostics.isEmpty();
    }

    public void addAll(DiagnosticBag bag) {
        this.diagnostics.addAll(bag.diagnostics);
    }

    public DiagnosticBag concat(DiagnosticBag bag) {
        DiagnosticBag concatenatedBag = new DiagnosticBag();
        concatenatedBag.addAll(this);
        concatenatedBag.addAll(bag);
        return concatenatedBag;
    }


    public void reportInvalidType(TextLocation location, String text, TypeSymbol type) {
        String message = "ERROR: The number " + text + "is not a valid representation of " + type + ".";
        report(location, message);
    }

    private void report(TextLocation location, String message) {
        Diagnostic diagnostic = new Diagnostic(location, message);
        diagnostics.add(diagnostic);
    }

    public void reportBadChar(TextLocation location, char character) {
        String message = "ERROR: Bad character input " + character + ".";
        report(location, message);
    }

    public void reportUnexpectedToken(TextLocation location, SyntaxKind actualKind, SyntaxKind expectedKind) {
        String message = "ERROR: Unexpected token '" + actualKind + "' expected '" + expectedKind + "'.";
        report(location, message);
    }

    public void reportUndefinedUnaryOperator(TextLocation location, String text, TypeSymbol type) {
        String message = "ERROR: Unary operator " + text + " is not defined for type " + type + ".";
        report(location, message);
    }

    public void reportUndefinedBinaryOperator(TextLocation location, String text, TypeSymbol leftType, TypeSymbol rightType) {
        String message = "ERROR: Binary operator " + text +
                " is not defined for type " + leftType + " and " + rightType + ".";

        report(location, message);
    }

    public void reportUndefinedNameExpression(TextLocation location, String name) {
        String message = "ERROR: Undefined variable '" + name + "'.";
        report(location, message);
    }

    public void reportVariableAlreadyDeclared(String name, TextLocation location) {
        String message = "ERROR: variable '" + name + "' is already declared.";
        report(location, message);
    }

    public List<Diagnostic> toUnmodifiableList() {
        return List.copyOf(diagnostics);
    }

    public void reportCannotConvert(TextLocation location, TypeSymbol expectedType, TypeSymbol actualType) {
        String message = "ERROR: Cannot convert '" + actualType + "' into '" + expectedType + "'.";
        report(location, message);
    }

    public void reportReadOnly(TextLocation location, String name) {
        String message = "ERROR: Variable '" + name + "' is read only and cannot be assigned.";
        report(location, message);
    }

    public void reportUnterminatedString(TextLocation location) {
        String message = "ERROR: Unterminated string literal.";
        report(location, message);
    }

    public void reportUndefinedFunction(TextLocation location, String name, List<TypeSymbol> types) {
        String usedTypes = parseTypesList(types);
        String message = "ERROR: Undefined function '" + name + "' with parameters" + usedTypes + ".";
        report(location, message);
    }

    public void reportUndefinedOperator(TextLocation location, String name, TypeSymbol type) {
        String message = "ERROR: Undefined operator '" + name + "' for type '" + type + "'.";
        report(location, message);
    }

    public void reportExpressionMustHaveValue(TextLocation location) {
        String message = "ERROR: Expression must have a value.";
        report(location, message);
    }

    public void reportUndefinedType(TextLocation location, String text) {
        String message = "ERROR: Undefined type '" + text + "'.";
        report(location, message);
    }

    public void reportDuplicatedParam(TextLocation location, String name) {
        String message = "ERROR: duplicated param name '" + name + "'.";
        report(location, message);
    }

    public void reportFunctionAlreadyDeclared(TextLocation location, String name, List<TypeSymbol> paramTypes) {
        String usedTypes = parseTypesList(paramTypes);

        String message = "ERROR: Function '" + name + "' with parameters" + usedTypes + " already declared.";
        report(location, message);
    }

    public void reportInvalidBreakOrContinue(TextLocation location, String text) {
        String message = "ERROR: Keyword '" + text + "' outside loop.";
        report(location, message);
    }

    public void reportReturnOutsideFunction(TextLocation location) {
        String message = "ERROR: Return statement outside function.";
        report(location, message);
    }

    public void reportReturnOnVoid(TextLocation location) {
        String message = "ERROR: Return statement on void function.";
        report(location, message);
    }

    public void reportMissingReturnExpression(TextLocation location, TypeSymbol type) {
        String message = "ERROR: Return statement is returning void on function of type '" + type.getName() + "'.";
        report(location, message);
    }

    public void reportAllPathMustReturn(TextLocation location) {
        String message = "ERROR: All paths must return a value on non void functions .";
        report(location, message);
    }

    private String parseTypesList(List<TypeSymbol> paramTypes) {
        StringBuilder usedTypes = new StringBuilder("[");
        for (int i = 0; i < paramTypes.size(); i++) {
            if (i > 0)
                usedTypes.append(",");
            usedTypes.append(paramTypes.get(i));
        }
        usedTypes.append("]");
        return usedTypes.toString();
    }
}
