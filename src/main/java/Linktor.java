import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import compilation.Compilation;
import repl.LinktorRepl;
import repl.Repl;
import util.ConsoleColors;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Linktor {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {

                fromFile(args);

            } else {
                Repl repl = new LinktorRepl();
                repl.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fromFile(String[] args) throws Exception {
        final Map<VariableSymbol, Object> variables = new HashMap<>();
        final List<SyntaxTree> trees = new ArrayList<>();
        var debug = false;
        var paths = getFilePath(args);
        for (var path : paths) {
            if (path.equals("_debug")) {
                debug = true;
                continue;
            }
            SyntaxTree tree = SyntaxTree.load(path);
            if (tree != null) {
                trees.add(tree);
            } else {
                System.out.println(ConsoleColors.RED_BRIGHT + "Error: " + path + " doesn't exists");
                System.out.println(ConsoleColors.RESET);
            }
        }
        Compilation compilation = Compilation.create(trees.toArray(new SyntaxTree[]{}));
        if (debug) {
            compilation.emitTree(new PrintWriter(System.out, true));
            compilation.writeFlowGraph();
        }
        compilation.emmit();
        /*EvaluationResult evaluationResult = compilation.evaluate(variables);
        List<Diagnostic> diagnostics = evaluationResult.diagnostics();
        DiagnosticsWriter.write(diagnostics);*/

    }

    private static String[] getFilePath(String[] paths) {
        SortedSet<String> result = new TreeSet<>();
        for (var path : paths) {
            var file = new File(path);
            if (file.isDirectory()) {
                for (File f : Objects.requireNonNull(file.listFiles())) {
                    String name = f.getName();
                    if (name.endsWith("lk")) {
                        result.add(path + "/" + name);
                    }
                }
            } else {
                result.add(path);
            }
        }
        return result.toArray(new String[]{});
    }
}
