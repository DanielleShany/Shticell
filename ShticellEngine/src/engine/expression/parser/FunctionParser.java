package engine.expression.parser;

import engine.cell.api.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.*;
import engine.expression.impl.numeric.*;
import engine.expression.impl.ref.*;
import engine.expression.impl.string.*;
import engine.sheet.api.Sheet;
import engine.cell.impl.CellType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public enum FunctionParser {
    IDENTITY {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "IDENTITY");

            String actualValue = arguments.get(0).trim();
            if (isBoolean(actualValue)) {
                return new IdentityExpression(Boolean.parseBoolean(actualValue), CellType.BOOLEAN);
            } else if (isNumeric(actualValue)) {
                return new IdentityExpression(Double.parseDouble(actualValue), CellType.NUMERIC);
            } else {
                return new IdentityExpression(actualValue, CellType.STRING);
            }
        }
    },
    PLUS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "PLUS");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            validateArgumentTypes(List.of(left, right), CellType.NUMERIC, "PLUS");

            return new PlusExpression(left, right);
        }
    },
    MINUS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "MINUS");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            validateArgumentTypes(List.of(left, right), CellType.NUMERIC, "MINUS");

            return new MinusExpression(left, right);
        }
    },
    TIMES {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "TIMES");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            validateArgumentTypes(List.of(left, right), CellType.NUMERIC, "TIMES");

            return new TimesExpression(left, right);
        }
    },
    DIVIDE {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "DIVIDE");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            validateArgumentTypes(List.of(left, right), CellType.NUMERIC, "DIVIDE");

            return new DivideExpression(left, right);
        }
    },
    POW {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "POW");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            validateArgumentTypes(List.of(left, right), CellType.NUMERIC, "POW");

            return new PowExpression(left, right);
        }
    },
    ABS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "ABS");

            Expression arg = parseExpression(arguments.get(0).trim(), sheet);

            validateArgumentType(arg, CellType.NUMERIC, "ABS");

            return new AbsExpression(arg);
        }
    },
    MOD {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "MOD");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            validateArgumentTypes(List.of(left, right), CellType.NUMERIC, "MOD");

            return new ModuloExpression(left, right);
        }
    },
    REF {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "REF");

            return new RefExpression(arguments.get(0), sheet);
        }
    },
    CONCAT {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "CONCAT");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            validateArgumentTypes(List.of(left, right), CellType.STRING, "CONCAT");

            return new ConcatExpression(left, right);
        }
    },
    SUB {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 3, "SUB");

            Expression source = parseExpression(arguments.get(0).trim(), sheet);
            Expression start = parseExpression(arguments.get(1).trim(), sheet);
            Expression end = parseExpression(arguments.get(2).trim(), sheet);

            if (!source.getFunctionResultType().equals(CellType.STRING) || !start.getFunctionResultType().equals(CellType.NUMERIC) || !end.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for SUB function. Expected STRING, NUMERIC, NUMERIC but got " + source.getFunctionResultType() + ", " + start.getFunctionResultType() + ", " + end.getFunctionResultType());
            }

            return new SubExpression(source, start, end);
        }
    };

    // Abstract parse method for all function types
    abstract public Expression parse(List<String> arguments, Sheet sheet);

    // Utility method to parse a string expression into an Expression object
    public static Expression parseExpression(String input, Sheet sheet) {
        if (input.startsWith("{") && input.endsWith("}")) {
                String functionContent = input.substring(1, input.length() - 1);
                List<String> topLevelParts = parseMainParts(functionContent);
                String functionName = topLevelParts.get(0).trim().toUpperCase();
                topLevelParts.remove(0); // Remove function name from the list of arguments
            try {
                return FunctionParser.valueOf(functionName).parse(topLevelParts, sheet);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("\nfunction " + topLevelParts.get(0).trim() + " does'nt exists in shticell.");
            }
        }

        // Handle identity expression
        return FunctionParser.IDENTITY.parse(List.of(input.trim()), sheet);
    }

    // Utility method to split the main parts of a function expression
    private static List<String> parseMainParts(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : input.toCharArray()) {
            if (c == '{') {
                stack.push(c);
            } else if (c == '}') {
                stack.pop();
            }

            if (c == ',' && stack.isEmpty()) {
                parts.add(buffer.toString().trim());
                buffer.setLength(0); // Clear the buffer for the next part
            } else {
                buffer.append(c);
            }
        }

        if (buffer.length() > 0) {
            parts.add(buffer.toString().trim());
        }

        return parts;
    }

    // Utility method to validate the number of arguments
    private static void validateArgumentCount(List<String> arguments, int expected, String functionName) {
        if (arguments.size() != expected) {
            throw new IllegalArgumentException("Invalid number of arguments for " + functionName + " function. Expected " + expected + ", but got " + arguments.size());
        }
    }

    // Utility method to validate the type of a single argument
    private static void validateArgumentType(Expression expression, CellType expectedType, String functionName) {
        if (!expression.getFunctionResultType().equals(expectedType) && !expression.getFunctionResultType().equals(CellType.UNKNOWN)) {
            throw new IllegalArgumentException("Invalid argument type for " + functionName + " function. Expected " + expectedType + ", but got " + expression.getFunctionResultType());
        }
    }

    // Utility method to validate the types of multiple arguments
    private static void validateArgumentTypes(List<Expression> expressions, CellType expectedType, String functionName) {
        for (Expression expression : expressions) {
            validateArgumentType(expression, expectedType, functionName);
        }
    }

    private static boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }


    private static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        // Test cases for parsing expressions
        String input = "{MOD, 8, 4}";
        Expression expression = parseExpression(input, null);
        EffectiveValue result = expression.eval();
        System.out.println("Result: " + result.getValue() + " of type " + result.getCellType());
    }
}
