import codeanalysis.diagnostics.Diagnostic;
import codeanalysis.diagnostics.text.SourceText;
import codeanalysis.diagnostics.text.TextLine;
import codeanalysis.symbol.variable.VariableSymbol;
import codeanalysis.syntax.SyntaxTree;
import compilation.Compilation;
import compilation.EvaluationResult;
import repl.LinktorRepl;
import repl.Repl;
import util.ConsoleColors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        String input = br.lines().collect(Collectors.joining());


        final Map<VariableSymbol, Object> variables = new HashMap<>();
        SyntaxTree tree = SyntaxTree.parse(input);
        Compilation compilation = new Compilation(tree);
        EvaluationResult evaluationResult = compilation.evaluate(variables);
        List<Diagnostic> diagnostics = evaluationResult.diagnostics();
        Object result = evaluationResult.result();
        if (!diagnostics.isEmpty()) {
            SourceText text = tree.getText();
            for (Diagnostic diagnostic : diagnostics) {
                int lineIndex = text.getLineIndex(diagnostic.span().start());
                TextLine line = text.getLines().get(lineIndex);
                int lineNumber = lineIndex + 1;
                int character = diagnostic.span().start() - line.getStart() + 1;

                System.out.println(ConsoleColors.RED);
                System.out.println(diagnostic);

                String prefix = input.substring(line.getStart(), diagnostic.span().start());
                String error = tree.getText().toString(diagnostic.span());
                String suffix = input.substring(diagnostic.span().end());

                System.out.print("At Line(" + lineNumber + "," + character + "): ");
                System.out.println(ConsoleColors.WHITE + prefix + ConsoleColors.RED + error + ConsoleColors.WHITE + suffix);
            }
        }
    }
}
