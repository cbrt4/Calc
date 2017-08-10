import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionCalculator {

    private final List<String> functions = Stream.of("sin", "cos", "tan", "!")
            .collect(Collectors.toList());

    private final List<String> operators = Stream.of("-", "+", "/", "*", "%", "^")
            .collect(Collectors.toList());

    private final List<String> brackets = Stream.of("(", ")")
            .collect(Collectors.toList());

    private final HashMap<String, Double> constants;

    private Stack<String> stack;
    private Stack<String> out;

    private double result;

    public ExpressionCalculator() {
        constants = new HashMap<>();
        constants.put("pi", Math.PI);
        constants.put("e", Math.E);
    }

    public double calculate(String expression) {
        stack = new Stack<>();
        out = new Stack<>();
        toRPN(expression);

        //  !!! >>> CALCULATING MAGIC HERE: <<< !!! \\\

        for (String token : out) {
            if (isNumber(token)) stack.push(token);
            if (isOperator(token)) calculateAction(token);
            if (isFunction(token)) calculateFunction(token);
        }

        result = Double.parseDouble(stack.peek());

        System.out.println("\nExpression: " + expression);
        System.out.println("\nOut:        " + out);
        System.out.println("\nStack:      " + stack);
        System.out.println("\nResult:     " + result);

        return result;
    }

    private void calculateAction(String token) {
        double arg2 = Double.parseDouble(stack.pop());
        double arg1 = Double.parseDouble(stack.pop());
        switch (token) {
            case "-":
                stack.push(String.valueOf(arg1 - arg2));
                break;
            case "+":
                stack.push(String.valueOf(arg1 + arg2));
                break;
            case "/":
                stack.push(String.valueOf(arg1 / arg2));
                break;
            case "*":
                stack.push(String.valueOf(arg1 * arg2));
                break;
            case "%":
                stack.push(String.valueOf(arg1 % arg2));
                break;
            case "^":
                stack.push(String.valueOf(Math.pow(arg1, arg2)));
                break;
        }
    }

    private void calculateFunction(String token) {
        double arg = Double.parseDouble(stack.pop());
        switch (token) {
            case "sin":
                stack.push(String.valueOf(Math.sin(arg)));
                break;
            case "cos":
                stack.push(String.valueOf(Math.cos(arg)));
                break;
            case "tan":
                stack.push(String.valueOf(Math.tan(arg)));
                break;
        }
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

        for (String function : functions) {
            expression = expression.replace(function, " " + function + " ");
        }

        for (String operator : operators) {
            expression = expression.replace(operator, " " + operator + " ");
        }

        for (String bracket : brackets) {
            expression = expression.replace(bracket, " " + bracket + " ");
        }

        return Arrays.stream(expression.split("[ ]+")).collect(Collectors.toList());
    }

    private void toRPN(String expression) {

        //  !!! >>> SORTING MAGIC HERE: <<< !!! \\\

        for (String token : getTokens(expression)) {
            if (isConstant(token)) token = String.valueOf(constants.get(token));
            if (isNumber(token)) out.push(token);
            if (isFunction(token)) stack.push(token);
            if (isOpenBracket(token)) stack.push(token);
            if (isCloseBracket(token)) {
                while (!stack.empty() && !isOpenBracket(stack.peek())) {
                    out.push(stack.pop());
                }
                stack.pop();
            }
            if (isOperator(token)) {
                while (!stack.empty() && operationPriority(token) <= operationPriority(stack.peek())) {
                    out.push(stack.pop());
                }
                stack.push(token);
            }
        }
        while (!stack.empty()) out.push(stack.pop());
    }

    private boolean isConstant(String token) {
        return constants.containsKey(token);
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFunction(String token) {
        return functions.contains(token);
    }

    private boolean isOpenBracket(String token) {
        return token.equals("(");
    }

    private boolean isCloseBracket(String token) {
        return token.equals(")");
    }

    private boolean isOperator(String token) {
        return operators.contains(token);
    }

    private int operationPriority(String token) {
        int priority = -1;
        if (token.equals("+") || token.equals("-")) priority = 0;
        if (token.equals("*") || token.equals("/") || token.equals("%")) priority = 1;
        if (isFunction(token) || token.equals("^")) priority = 2;
        return priority;
    }
}