package workline.core.domain;

import java.io.Serializable;

public class ProcessElementVariableMappingDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String expression;
    private InputVariableTypeData type;
    private EInputVariableScope inputVariableScope;
    private String inputVariableSelectionQuery;
    private MappingExpression mappingExpression;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String nameExpression) {
        this.expression = nameExpression;
    }

    public InputVariableTypeData getType() {
        return type;
    }

    public void setType(InputVariableTypeData type) {
        this.type = type;
    }

    public EInputVariableScope getInputVariableScope() {
        return inputVariableScope;
    }

    public void setInputVariableScope(EInputVariableScope eInputVariableScope) {
        this.inputVariableScope = eInputVariableScope;
    }

    public String getInputVariableSelectionQuery() {
        return inputVariableSelectionQuery;
    }

    public void setInputVariableSelectionQuery(String inputVariableSelectionQuery) {
        this.inputVariableSelectionQuery = inputVariableSelectionQuery;
    }

    public MappingExpression getMappingExpression() {
        return mappingExpression;
    }

    public void setMappingExpression(MappingExpression mappedToExpression) {
        this.mappingExpression = mappedToExpression;
    }

    @Override
    public String toString() {
        return "ProcessElementVariableMappingDefinition [expression=" + expression + ", type=" + type + ", inputVariableScope=" + inputVariableScope
                + ", inputVariableSelectionQuery=" + inputVariableSelectionQuery + ", mappingExpression=" + mappingExpression + "]";
    }

}
