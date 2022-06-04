package codeanalysis.diagnostics;

import codeanalysis.diagnostics.text.TextSpan;
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


    public void reportInvalidType(TextSpan span, String text, TypeSymbol type) {
        String message = "ERROR: The number " + text + "is not a valid representation of " + type + ".";
        report(span, message);
    }

    private void report(TextSpan span, String message) {
        Diagnostic diagnostic = new Diagnostic(span, message);
        diagnostics.add(diagnostic);
    }

    public void reportBadChar(int position, char character) {
        TextSpan span = new TextSpan(position, 1);
        String message = "ERROR: Bad character input " + character + ".";
        report(span, message);
    }

    public void reportUnexpectedToken(TextSpan span, SyntaxKind actualKind, SyntaxKind expectedKind) {
        String message = "ERROR: Unexpected token '" + actualKind + "' expected '" + expectedKind + "'.";
        report(span, message);
    }

    public void reportUndefinedUnaryOperator(TextSpan span, String text, TypeSymbol type) {
        String message = "ERROR: Unary operator " + text + " is not defined for type " + type + ".";
        report(span, message);
    }

    public void reportUndefinedBinaryOperator(TextSpan span, String text, TypeSymbol leftType, TypeSymbol rightType) {
        String message = "ERROR: Binary operator " + text +
                " is not defined for type " + leftType + " and " + rightType + ".";

        report(span, message);
    }

    public void reportUndefinedNameExpression(TextSpan span, String name) {
        String message = "ERROR: Undefined variable '" + name + "'.";
        report(span, message);
    }

    public void reportVariableAlreadyDeclared(String name, TextSpan span) {
        String message = "ERROR: variable '" + name + "' is already declared.";
        report(span, message);
    }

    public List<Diagnostic> toUnmodifiableList() {
        return List.copyOf(diagnostics);
    }

    public void reportCannotConvert(TextSpan span, TypeSymbol expectedType, TypeSymbol actualType) {
        String message = "ERROR: Cannot convert '" + expectedType + "' into '" + actualType + "'.";
        report(span, message);
    }

    public void reportReadOnly(TextSpan span, String name) {
        String message = "ERROR: Variable '" + name + "' is read only and cannot be assigned.";
        report(span, message);
    }

    public void reportUnterminatedString(TextSpan span) {
        String message = "ERROR: Unterminated string literal.";
        report(span, message);
    }

    public void reportUndefinedFunction(TextSpan span, String name) {
        String message = "ERROR: Undefined function '" + name + "' .";
        report(span, message);
    }

    public void reportWrongArgumentCount(TextSpan span, String name, int expectedSize, int actualSize) {
        String message = "ERROR: Wrong number of arguments for function '" + name + "' expected:" + expectedSize + ",received:" + actualSize + " .";
        report(span, message);
    }

    public void reportWrongArgumentType(TextSpan span, String name, String argName, TypeSymbol expectedType, TypeSymbol actualType) {
        String message = "ERROR: Wrong type of arguments for function '" + name + "' param'" + argName +
                "' expected type:" + expectedType + "received:" + actualType + " .";
        report(span, message);
    }

    public void reportExpressionMustHaveValue(TextSpan span) {
        String message = "ERROR: Expression must have a value.";
        report(span, message);
    }
}
