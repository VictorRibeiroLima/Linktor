package repl;

import util.ConsoleColors;

import java.util.Scanner;

public abstract class Repl {

    public void run() {
        Scanner console = new Scanner(System.in);
        try {
            while (true) {
                String submission = submission(console);
                if (submission == null)
                    break;
                evaluate(submission);
                System.out.println(ConsoleColors.RESET);
                System.out.println("-------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\033[0;31m" + e.getMessage());
        }
    }

    private String submission(Scanner console) {
        StringBuilder input = new StringBuilder();
        while (true) {
            if (input.isEmpty()) {
                System.out.print(">");
            } else {
                System.out.print(".");
            }
            String inLineInput = console.nextLine();
            if (input.isEmpty() && inLineInput.isEmpty())
                return null;
            if (evaluateMetaCommand(inLineInput)) continue;

            input.append(inLineInput);
            if (isCompleteSubmission(inLineInput, input)) {
                input.append("\n");
                continue;
            }
            return input.toString();
        }
    }

    protected abstract boolean evaluateMetaCommand(String inLineInput);

    protected abstract void evaluate(String input) throws Exception;

    protected abstract boolean isCompleteSubmission(String text, StringBuilder input);
}
