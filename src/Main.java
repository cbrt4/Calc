import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        ExpressionCalculator calculator = new ExpressionCalculator();
        String consoleIn;

        System.out.println("To exit type 'exit' and press Enter\nType expression here:\n");
        while (!(consoleIn = scanner.readLine()).equals("exit")) {
            if (consoleIn.equals(""))
                System.out.println("Can't calculate empty expression\nType expression here:\n");
            else
                System.out.println("\n" +
                        consoleIn + " = " + calculator.calculate(consoleIn) +
                        "\n");
        }
    }
}