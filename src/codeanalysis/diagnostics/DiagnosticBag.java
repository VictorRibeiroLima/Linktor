package src.codeanalysis.diagnostics;

import src.codeanalysis.syntax.SyntaxKind;

import java.lang.reflect.Type;
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


    private void report(TextSpan span, String message) {
        Diagnostic diagnostic = new Diagnostic(span, message);
        diagnostics.add(diagnostic);
    }

    public void reportInvalidType(TextSpan span, String text, Type type) {
        String message = "ERROR: The number " + text + "is not a valid representation of " + type + ".";
        report(span, message);
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

    public void reportUndefinedUnaryOperator(TextSpan span, String text, Type type) {
        String message = "ERROR: Unary operator " + text + " is not defined for type " + type + ".";
        report(span, message);
    }

    public void reportUndefinedBinaryOperator(TextSpan span, String text, Type leftType, Type rightType) {
        String message = "ERROR: Binary operator " + text +
                " is not defined for type " + leftType + " and " + rightType + ".";

        report(span, message);
    }

    public void reportUndefinedNameExpression(TextSpan span, String name) {
        String message = "ERROR: Undefined variable '" + name + "'";
        report(span, message);
    }
}
