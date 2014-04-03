package workline.core.businesstask.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableExpressionParser {
    private static final VariableExpressionParser INSTANCE = new VariableExpressionParser();

    private VariableExpressionParser() {
    }

    public List<String> getTokens(String variableExpression) {
        List<String> tokens;

        if (variableExpression == null) {
            return new ArrayList<>();
        }

        tokens = new ArrayList<>(Arrays.asList(variableExpression.split("\\.")));

        return tokens;
    }

    public static VariableExpressionParser getInstance() {
        return INSTANCE;
    }
}
