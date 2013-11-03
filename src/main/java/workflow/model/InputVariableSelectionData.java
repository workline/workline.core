package workflow.model;

import java.util.List;

public class InputVariableSelectionData {
    private EInputVariableSelectionType inputVariableSelectionType;
    private List<?> inputVariableSelectionList;

    public EInputVariableSelectionType getInputVariableSelectionType() {
        return inputVariableSelectionType;
    }

    public void setInputVariableSelectionType(EInputVariableSelectionType inputVariableSelectionType) {
        this.inputVariableSelectionType = inputVariableSelectionType;
    }

    public List<?> getInputVariableSelectionList() {
        return inputVariableSelectionList;
    }

    public void setInputVariableSelectionList(List<?> inputVariableSelectionList) {
        this.inputVariableSelectionList = inputVariableSelectionList;
    }

}
