import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ExpressionCalculator {

    private String[] functionArray = {"sin", "cos", "tg", "ctg", "!", "%", "^"};
    private String[] operatorArray = {"-", "+", "/", "*", "(", ")"};
    private final HashMap<String, Double> variables = new HashMap<String, Double>() {
        {
            put("pi", Math.PI);
            put("e", Math.E);
        }
    };

    private double result;

    public double calculate(String expression) {
        String[] tokenArray = getTokens(expression);
        List<Double> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokenArray) {
            if (isVariable(token)) {
                if (variables.containsKey(token)) output.add(variables.get(token));
                else stack.push(token);
            }
            if (isNumber(token)) output.add(Double.parseDouble(token));
            if (isFunction(token) || isOperator(token)) stack.push(token);

        }

        System.out.println("Out: " + output + " size: " + output.size() +
                "\nStack: " + stack + " size: " + stack.size());


        /// calculating here:

        return result;
    }

    private String[] getTokens(String expression) {

        expression = expression.toLowerCase();

        if (expression.charAt(0) == '-' || expression.charAt(0) == '+')
            expression = "0" + expression;

        expression = expression
                .replace(",", ".")
                .replaceAll("['`]|[ ]+", "")
                .replace("(-", "(0-")
                .replace("(+", "(0+");

        for (String function : functionArray) {
            expression = expression.replace(function, " " + function + " ");
        }

        for (String operator : operatorArray) {
            expression = expression.replace(operator, " " + operator + " ");
        }

        return expression.split("[ ]+");
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        boolean result = false;
        for (String operator : operatorArray) {
            if (operator.equals(token)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isFunction(String token) {
        boolean result = false;
        for (String function : functionArray) {
            if (function.equals(token)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isVariable(String token) {
        return !isFunction(token) && token.matches("[a-z]+");
    }
}