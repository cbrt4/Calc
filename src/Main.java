import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static final String ANSI_RESET = "\u001B[0m";
    //private static final String ANSI_BLACK = "\u001B[0;90m";
    //private static final String ANSI_RED = "\u001B[0;91m";
    //private static final String ANSI_GREEN = "\u001B[0;92m";
    //private static final String ANSI_YELLOW = "\u001B[0;93m";
    //private static final String ANSI_BLUE = "\u001B[0;94m";
    //private static final String ANSI_PURPLE = "\u001B[0;95m";
    private static final String ANSI_CYAN = "\u001B[0;96m";
    //private static final String ANSI_WHITE = "\u001B[0;97m";

    public static void main(String[] args) throws IOException {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        ExpressionCalculator calculator = new ExpressionCalculator();
        String consoleIn;

        System.out.println(ANSI_CYAN + "\n  * * * / / / ConsoleCalc - - - + + + \n" +
                "\n To see help type 'help' and press Enter" +
                "\n To exit type 'exit' and press Enter" +
                "\n Type expression here:\n "+ ANSI_RESET);
        while (!(consoleIn = scanner.readLine()).equalsIgnoreCase("exit")) {
            System.out.println(consoleIn.equalsIgnoreCase("help") ?
                    ANSI_CYAN + calculator.getHelp() + ANSI_RESET :
                    ANSI_CYAN + " = " + calculator.calculate(consoleIn) + ANSI_RESET +
                    "\n");
        }
    }
}