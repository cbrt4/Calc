import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionCalculator {

    private final List<String> functions = Stream.of("sin", "cos", "tan", "cot", "!", "^", "%", "log", "ln", "lg", "exp", "abs", "sqrt", "cbrt", "root")
            .collect(Collectors.toList());

    private final List<String> operators = Stream.of("-", "+", "/", "*", UNARY)
            .collect(Collectors.toList());

    private final List<String> brackets = Stream.of("(", ")")
            .collect(Collectors.toList());

    private final HashMap<String, Double> constants;

    private Stack<String> stack, out;

    private static final String UNARY = "un", SEPARATOR = "|";

    public ExpressionCalculator() {
        constants = new HashMap<>();
        constants.put("pi", Math.PI);
        constants.put("e", Math.E);
    }

    private List<String> getTokens(String expression) {

        expression = expression
                .toLowerCase()
                .replace(",", ".")
                .replace(SEPARATOR, " " + SEPARATOR + " ")
                .replaceAll("['`]", "");

        for (String function : functions) {
            expression = expression.replace(function, " " + function + " ");
        }

        for (String operator : operators) {
            expression = expression.replace(operator, " " + operator + " ");
        }

        for (String bracket : brackets) {
            expression = expression.replace(bracket, " " + bracket + " ");
        }

        for (Map.Entry<String, Double> entry : constants.entrySet()) {
            expression = expression.replace(entry.getKey(), " " + entry.getValue() + " ");
        }

        List<String> tokens = new ArrayList<>();
        String previous = " ";

        for (String token : expression.split("[ ]+")) {
            if (isNumber(previous) && (isOpenBracket(token) || isNumber(token) || isFunction(token) && !token.matches("[!%^]")) ||
                    isCloseBracket(previous) && (isOpenBracket(token) || isNumber(token)  || isFunction(token) && !token.matches("[!%^]"))) {
                tokens.add("*");
                tokens.add(token);
            } else if (token.equals("-") && !isNumber(previous) && !isCloseBracket(previous)) {
                tokens.add("-1");
                tokens.add(UNARY);
            } else if (token.equals("+") && !isNumber(previous) && !isCloseBracket(previous)) {
                tokens.add("1");
                tokens.add(UNARY);
            }
            else tokens.add(token);
            previous = token;
        }
        System.out.println(tokens);
        return tokens;
    }

    /**
     * Алгоритм toRPN:
     * <p>
     * Пока есть ещё символы для чтения:
     * <p>
     * Читаем очередной символ.
     * <p>
     * Если символ является числом, добавляем его к выходной строке.
     * <p>
     * Если символ является символом функции, помещаем его в стек.
     * <p>
     * Если символ является открывающей скобкой, помещаем его в стек.
     * <p>
     * Если символ является закрывающей скобкой:
     * До тех пор, пока верхним элементом стека не станет открывающая скобка,
     * выталкиваем элементы из стека в выходную строку. При этом открывающая
     * скобка удаляется из стека, но в выходную строку не добавляется. Если
     * стек закончился раньше, чем мы встретили открывающую скобку, это означает,
     * что в выражении либо неверно поставлен разделитель, либо не согласованы скобки.
     * <p>
     * Если символ является оператором о1, тогда:
     * 1) пока…
     * … (если оператор o1 право-ассоциированный) приоритет o1 меньше приоритета
     * оператора, находящегося на вершине стека…
     * … (если оператор o1 ассоциированный, либо лево-ассоциированный) приоритет
     * o1 меньше либо равен приоритету оператора, находящегося на вершине стека…
     * … выталкиваем верхний элемент стека в выходную строку;
     * 2) помещаем оператор o1 в стек.
     * <p>
     * Когда входная строка закончилась, выталкиваем все символы из стека в
     * выходную строку. В стеке должны были остаться только символы операторов;
     * если это не так, значит в выражении не согласованы скобки.
     */
    private void toRPN(String expression) {

        for (String token : getTokens(expression)) {
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
     * <p>
     * Алгоритм вычисления для стековой машины:
     * <p>
     * 1.Обработка входного символа
     * * Если на вход подан операнд, он помещается на вершину стека.
     * * Если на вход подан знак операции, то соответствующая операция
     * выполняется над требуемым количеством значений, извлечённых из
     * стека, взятых в порядке добавления. Результат выполненной операции
     * кладётся на вершину стека.
     * <p>
     * 2.Если входной набор символов обработан не полностью, перейти к шагу 1.
     * <p>
     * 3.После полной обработки входного набора символов результат вычисления
     * выражения лежит на вершине стека.
     */
    public double calculate(String expression) {
        stack = new Stack<>();
        out = new Stack<>();
        toRPN(expression);

        for (String token : out) {
            if (isNumber(token)) stack.push(token);
            if (isOperator(token)) calculateOperation(token);
            if (isFunction(token)) calculateFunction(token);
        }
        return stack.size() == 1 && isNumber(stack.peek()) ?
                Double.parseDouble(stack.pop()) : .0 / .0;
    }

    private void calculateOperation(String token) {
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
                case UNARY:
                    stack.push(String.valueOf(arg1 * arg2));
                    break;
            }
        } catch (EmptyStackException e) {
            stack.push(String.valueOf(.0 / .0));
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
                    stack.push(String.valueOf(Math.sin(arg2) / Math.cos(arg2)));
                    break;
                case "cot":
                    stack.push(String.valueOf(Math.cos(arg2) / Math.sin(arg2)));
                    break;
                case "!":
                    stack.push(String.valueOf(factorial(arg2)));
                    break;
                case "^":
                    arg1 = Double.parseDouble(stack.pop());
                    stack.push(String.valueOf(Math.pow(arg1, arg2)));
                    break;
                case "%":
                    arg1 = Double.parseDouble(stack.pop());
                    stack.push(String.valueOf(arg1 % arg2));
                    break;
                case "log":
                    arg1 = Double.parseDouble(stack.pop());
                    stack.push(String.valueOf(Math.log(arg2) / Math.log(arg1)));
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
                    stack.push(String.valueOf(Math.pow(arg2, 1 / arg1)));
                    break;
            }
        } catch (EmptyStackException e) {
            stack.push(String.valueOf(.0 / .0));
        }
    }

    private double factorial(double arg) {
        if (arg % 1 != 0) return .0 / .0;
        double result = 1;
        for (int i = (int) arg; i > 1; i--) result *= i;
        return result;
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
        if (token.matches("[*/]")) priority = 1;
        if (isFunction(token)) priority = 2;
        if (token.equals(UNARY)) priority = 3;
        return priority;
    }

    public String getHelp() {
        String help = "\n Calculator supports following functions:\n ";
        int count = 0;
        for (String operator : operators) {
            help += operator + ", ";
            count++;
        }
        help = help.substring(0, help.length() - 3);
        for (String function : functions) {
            help += count % 10 == 0 ? function + ",\n " : function + ", ";
            count++;
        }
        help = help.substring(0, help.length() - 2) +
                ".\n\n To separate arguments in two-argument functions " +
                "\n (e.g. 'root' and 'log')  use '" + SEPARATOR + "'. For example:" +
                "\n root10" + SEPARATOR + "1024 = 2\n log2" + SEPARATOR + "1024 = 10\n";
        return help;
    }
}