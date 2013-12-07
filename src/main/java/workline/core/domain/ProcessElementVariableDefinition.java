package workline.core.domain;

import java.io.Serializable;

public class ProcessElementVariableDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private InputVariableTypeData type;
    private EInputVariableScope inputVariableScope;
    private String inputVariableSelectionQuery;
    private String mappedToExpression;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMappedToExpression() {
        return mappedToExpression;
    }

    public void setMappedToExpression(String mappedToExpression) {
        this.mappedToExpression = mappedToExpression;
    }

    @Override
    public String toString() {
        return "ProcessElementVariableDefinition [name=" + name + ", type=" + type + ", inputVariableScope=" + inputVariableScope
                + ", inputVariableSelectionQuery=" + inputVariableSelectionQuery + ", mappedToExpression=" + mappedToExpression + "]";
    }

}
