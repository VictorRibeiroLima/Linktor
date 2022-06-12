import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import compilation.Compilation;
import compilation.EvaluationResult;
import io.DiagnosticsWriter;
import repl.LinktorRepl;
import repl.Repl;
import util.ConsoleColors;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Linktor {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {

                fromFile(args[0]);

            } else {
                Repl repl = new LinktorRepl();
                repl.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fromFile(String filePath) throws Exception {
        final Map<VariableSymbol, Object> variables = new HashMap<>();
        SyntaxTree tree = SyntaxTree.load(filePath);
        if (tree != null) {
            Compilation compilation = new Compilation(tree);
            EvaluationResult evaluationResult = compilation.evaluate(variables);
            List<Diagnostic> diagnostics = evaluationResult.diagnostics();
            DiagnosticsWriter.writeTo(new PrintWriter(System.out), diagnostics, tree, true);
        } else {
            System.out.print(ConsoleColors.RED_BRIGHT + "Error: " + filePath + " doesn't exists");
        }
    }
}
