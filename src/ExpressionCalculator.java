import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionCalculator {

    private final List<String> functions = Stream.of("sin", "cos", "tan", "cot", "!", "^", "log", "ln", "lg", "exp", "abs", "sqrt", "cbrt", "root")
            .collect(Collectors.toList());

    private final List<String> operators = Stream.of("-", "+", "/", "*", "%")
            .collect(Collectors.toList());

    private final List<String> brackets = Stream.of("(", ")")
            .collect(Collectors.toList());

    private final HashMap<String, Double> constants;

    private Stack<String> stack, out;

    public ExpressionCalculator() {
        constants = new HashMap<>();
        constants.put("pi", Math.PI);
        constants.put("e", Math.E);
    }

    private List<String> getTokens(String expression) {

        expression = expression
                .toLowerCase()
                .replace(",", ".")
                .replaceAll("['`]|[ ]+", "")
                .replace("(-", "(0-")
                .replace("(+", "(0+");

        if (expression.startsWith("-") || expression.startsWith("+"))
            expression = "0" + expression;

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
     */
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
                if (!stack.empty() && isOpenBracket(stack.peek())) stack.pop();
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
     *
     * Алгоритм вычисления для стековой машины элементарен:
     *
     * 1.Обработка входного символа
     * * Если на вход подан операнд, он помещается на вершину стека.
     * * Если на вход подан знак операции, то соответствующая операция
     * выполняется над требуемым количеством значений, извлечённых из
     * стека, взятых в порядке добавления. Результат выполненной операции
     * кладётся на вершину стека.
     *
     * 2.Если входной набор символов обработан не полностью, перейти к шагу 1.
     *
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
            double arg2 = Double.parseDouble(stack.pop()),
                    arg1 = Double.parseDouble(stack.pop());
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
            }
        } catch (EmptyStackException e) {
            stack.push(String.valueOf(.0/.0));
        }
    }

    private void calculateFunction(String token) {
        try {
            double arg1, arg2 = Double.parseDouble(stack.pop());
            switch (token) {
                case "sin":
                    stack.push(String.valueOf(Math.sin(arg2)));
                    break;
                case "cos":
                    stack.push(String.valueOf(Math.cos(arg2)));
                    break;
                case "tan":
                    stack.push(String.valueOf(Math.sin(arg2)/Math.cos(arg2)));
                    break;
                case "cot":
                    stack.push(String.valueOf(Math.cos(arg2)/Math.sin(arg2)));
                    break;
                case "!":
                    stack.push(String.valueOf(factorial(arg2)));
                    break;
                case "^":
                    arg1 = Double.parseDouble(stack.pop());
                    stack.push(String.valueOf(Math.pow(arg1, arg2)));
                    break;
                case "log":
                    arg1 = Double.parseDouble(stack.pop());
                    stack.push(String.valueOf(Math.log(arg2)/Math.log(arg1)));
                    break;
                case "ln":
                    stack.push(String.valueOf(Math.log(arg2)));
                    break;
                case "lg":
                    stack.push(String.valueOf(Math.log10(arg2)));
                    break;
                case "exp":
                    stack.push(String.valueOf(Math.exp(arg2)));
                    break;
                case "abs":
                    stack.push(String.valueOf(Math.abs(arg2)));
                    break;
                case "sqrt":
                    stack.push(String.valueOf(Math.sqrt(arg2)));
                    break;
                case "cbrt":
                    stack.push(String.valueOf(Math.cbrt(arg2)));
                    break;
                case "root":
                    arg1 = Double.parseDouble(stack.pop());
                    stack.push(String.valueOf(Math.pow(arg2, 1/arg1)));
                    break;
            }
        } catch (EmptyStackException e) {
            stack.push(String.valueOf(.0/.0));
        }
    }

    private double factorial(double arg) {
        if (arg%1 != 0) return .0/.0;
        double result = 1;
        for (int i = (int) arg; i > 0; i--) result *= i;
        return result;
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

    private byte operationPriority(String token) {
        byte priority = -1;
        if (token.matches("[-+]")) priority = 0;
        if (token.matches("[*/%]")) priority = 1;
        if (isFunction(token)) priority = 2;
        return priority;
    }

    public String getHelp() {
        String help = "\n Calculator supports following functions:\n ";
        int count = 0;
        for (String operator : operators) {
            help += operator + ", ";
            count++;
        }
        for (String function : functions) {
            help += count % 10 == 0 ? function + ",\n " : function + ", ";
            count++;
        }
        help = help.substring(0, help.length()-2) + ".\n";
        return help;
    }
}