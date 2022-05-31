package compilation;

import codeanalysis.diagnostics.Diagnostic;

import java.util.List;

public record EvaluationResult(List<Diagnostic> diagnostics, Object result) {
}
