import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionCalculator {

    private final List<String> functions = Stream.of("sin", "cos", "tan", "cot", "!")
            .collect(Collectors.toList());

    private final List<String> operators = Stream.of("-", "+", "/", "*", "^")
            .collect(Collectors.toList());

    private final List<String> brackets = Stream.of("(", ")")
            .collect(Collectors.toList());

    private final HashMap<String, Double> constants;

    private Stack<String> stack;
    private Stack<String> out;

    public ExpressionCalculator() {
        constants = new HashMap<>();
        constants.put("pi", Math.PI);
        constants.put("e", Math.E);
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

    /**
     * Алгоритм toRPN:
     *
     * Пока есть ещё символы для чтения:
     *
     * Читаем очередной символ.
     *
     * Если символ является числом, добавляем его к выходной строке.
     *
     * Если символ является символом функции, помещаем его в стек.
     *
     * Если символ является открывающей скобкой, помещаем его в стек.
     *
     * Если символ является закрывающей скобкой:
     * До тех пор, пока верхним элементом стека не станет открывающая скобка,
     * выталкиваем элементы из стека в выходную строку. При этом открывающая
     * скобка удаляется из стека, но в выходную строку не добавляется. Если
     * стек закончился раньше, чем мы встретили открывающую скобку, это означает,
     * что в выражении либо неверно поставлен разделитель, либо не согласованы скобки.
     *
     * Если символ является оператором о1, тогда:
     * 1) пока…
     * … (если оператор o1 право-ассоциированный) приоритет o1 меньше приоритета
     * оператора, находящегося на вершине стека…
     * … (если оператор o1 ассоциированный, либо лево-ассоциированный) приоритет
     * o1 меньше либо равен приоритету оператора, находящегося на вершине стека…
     * … выталкиваем верхний элемент стека в выходную строку;
     * 2) помещаем оператор o1 в стек.
     *
     * Когда входная строка закончилась, выталкиваем все символы из стека в
     * выходную строку. В стеке должны были остаться только символы операторов;
     * если это не так, значит в выражении не согласованы скобки.
     * */

    private void toRPN(String expression) {

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

    /**
     * Автоматизация вычисления выражений в обратной польской нотации
     * основана на использовании стека.
     * Алгоритм вычисления для стековой машины элементарен:
     * 1.Обработка входного символа
     * * Если на вход подан операнд, он помещается на вершину стека.
     * * Если на вход подан знак операции, то соответствующая операция
     * выполняется над требуемым количеством значений, извлечённых из
     * стека, взятых в порядке добавления. Результат выполненной операции
     * кладётся на вершину стека.
     * 2.Если входной набор символов обработан не полностью, перейти к шагу 1.
     * 3.После полной обработки входного набора символов результат вычисления
     * выражения лежит на вершине стека.
     */

    public double calculate(String expression) {
        stack = new Stack<>();
        out = new Stack<>();
        toRPN(expression);

        for (String token : out) {
            if (isNumber(token)) stack.push(token);
            if (isOperator(token)) calculateAction(token);
            if (isFunction(token)) calculateFunction(token);
        }
        try {
            return Double.parseDouble(stack.pop());
        } catch (EmptyStackException e) {
            return .0/.0;
        }
    }

    private void calculateAction(String token) {
        try {
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
        } catch (EmptyStackException e) {
            stack.push(String.valueOf(.0/.0));
        }
    }

    private void calculateFunction(String token) {
        try {
            double arg = Double.parseDouble(stack.pop());
            switch (token) {
                case "sin":
                    stack.push(String.valueOf(Math.sin(arg)));
                    break;
                case "cos":
                    stack.push(String.valueOf(Math.cos(arg)));
                    break;
                case "tan":
                    stack.push(String.valueOf(Math.sin(arg)/Math.cos(arg)));
                    break;
                case "cot":
                    stack.push(String.valueOf(Math.cos(arg)/Math.sin(arg)));
                    break;
                case "!":
                    stack.push(String.valueOf(factorial(arg)));
                    break;
            }
        } catch (EmptyStackException e) {
            stack.push(String.valueOf(.0/.0));
        }
    }

    private double factorial(double arg) {
        double result = 1;
        for (int i = 1; i <= arg; i++) result *= i;
        return result;
    }

    private boolean isConstant(String token) {
        return constants.containsKey(token);
    }

    private boolean isNumber(String token) {
        try {
            token = String.valueOf(Double.parseDouble(token));
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

    private byte operationPriority(String token) {
        byte priority = -1;
        if (token.equals("+") || token.equals("-")) priority = 0;
        if (token.equals("*") || token.equals("/")) priority = 1;
        if (isFunction(token) || token.equals("^")) priority = 2;
        return priority;
    }
}