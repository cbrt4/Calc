import java.util.*;
import java.util.stream.Collectors;

public class ExpressionCalculator {

    private String[] functionArray = {"sin", "cos", "tg", "ctg", "!", "%", "^"};
    private String[] operatorArray = {"-", "+", "/", "*"};
    private String[] brackets = {"(", ")"};

    private HashMap<String, Double> variables;

    private double result;

    public ExpressionCalculator() {
        variables = new HashMap<>();
        variables.put("pi", Math.PI);
        variables.put("e", Math.E);
    }


    public double calculate(String expression) {
        List<String> tokens = getTokens(expression);

        System.out.println(tokens);

        return result;
    }

    private void addSub(List<String> tokens) {
    }

    private void mulDiv(List<String> tokens) {
    }

    private void resolveFunction(List<String> tokens) {
    }

    private List<String> getTokens(String expression) {

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

        for (String bracket : brackets) {
            expression = expression.replace(bracket, " " + bracket + " ");
        }

        return Arrays.stream(expression.split("[ ]+")).collect(Collectors.toList());
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isBracket(String token) {
        boolean result = false;
        for (String bracket : brackets) {
            if (bracket.equals(token)) {
                result = true;
                break;
            }
        }
        return result;
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