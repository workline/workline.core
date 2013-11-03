package workflow.model;

import java.io.Serializable;

public class ProcessVariableDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private InputVariableTypeData type;
    private EInputVariableScope inputVariableScope;
    private InputVariableSelectionData inputVariableSelectionData;

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

    public InputVariableSelectionData getInputVariableSelectionData() {
        return inputVariableSelectionData;
    }

    public void setInputVariableSelectionData(InputVariableSelectionData inputVariableSelectionData) {
        this.inputVariableSelectionData = inputVariableSelectionData;
    }

}
