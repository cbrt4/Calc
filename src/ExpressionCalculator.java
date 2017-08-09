public class ExpressionCalculator {

    private String[] functionArray = {"sin", "cos", "tg", "ctg", "!", "%", "^"};
    private String[] operatorArray = {"-", "+", "/", "*", "(", ")"};

    private double result;

    private String[] getTokens(String expression) {

        expression = expression.toLowerCase();

        if (expression.charAt(0) == '-' || expression.charAt(0) == '+')
            expression = "0" + expression;

        expression = expression
                .replace(",", ".")
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
        return token.matches("[-+/*]");
    }

    private boolean isFunction(String token) {
        return token.equals("sin") ||
                token.equals("cos") ||
                token.equals("tg") ||
                token.equals("ctg") ||
                token.equals("!") ||
                token.equals("%") ||
                token.equals("^");
    }

    private boolean isVariable(String token) {
        return !isFunction(token) && token.matches("[a-z]+");
    }
}
