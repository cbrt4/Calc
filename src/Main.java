import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        ExpressionCalculator calculator = new ExpressionCalculator();
        String consoleIn;

        System.out.println("\n  * * * / / / ConsoleCalc - - - + + + \n" +
                "\n To see help type 'help' and press Enter" +
                "\n To exit type 'exit' and press Enter" +
                "\n Type expression here:\n");
        while (!(consoleIn = scanner.readLine()).equalsIgnoreCase("exit")) {
            System.out.println(consoleIn.equalsIgnoreCase("help") ?
                    calculator.getHelp() : " = " + calculator.calculate(consoleIn) +
                    "\n");
        }
    }
}