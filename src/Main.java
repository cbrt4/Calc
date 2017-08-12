import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        ExpressionCalculator calculator = new ExpressionCalculator();
        String consoleIn;

        System.out.println("To exit type 'exit' and press Enter" +
                "\nTo see help type 'help' and press Enter" +
                "\nType expression here:\n");
        while (!(consoleIn = scanner.readLine()).equalsIgnoreCase("exit")) {
            if (consoleIn.equals(""))
                System.out.println("Can't calculate empty expression" +
                        "\nType expression here:\n");
            if (consoleIn.equalsIgnoreCase("help"))
                System.out.println(calculator.getHelp());
            else
                System.out.println("\n" +
                        consoleIn + " = " + calculator.calculate(consoleIn) +
                        "\n");
        }
    }
}