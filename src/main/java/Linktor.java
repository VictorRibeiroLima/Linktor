import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import compilation.Compilation;
import compilation.EvaluationResult;
import io.DiagnosticsWriter;
import repl.LinktorRepl;
import repl.Repl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        File file = new File(filePath);
        BufferedReader br
                = new BufferedReader(new FileReader(file));

        var builder = new StringBuilder();
        br.lines().forEach(line -> {
            builder.append(line);
            builder.append("\n");
        });

        String input = builder.toString();

        final Map<VariableSymbol, Object> variables = new HashMap<>();
        SyntaxTree tree = SyntaxTree.parse(input);
        Compilation compilation = new Compilation(tree);
        EvaluationResult evaluationResult = compilation.evaluate(variables);
        List<Diagnostic> diagnostics = evaluationResult.diagnostics();
        DiagnosticsWriter.writeTo(new PrintWriter(System.out), diagnostics, tree, true);
    }
}
