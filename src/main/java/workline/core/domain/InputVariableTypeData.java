package workline.core.domain;

public class InputVariableTypeData {

    private EInputVariableType inputVariableType;
    private String repoName;

    public EInputVariableType getInputVariableType() {
        return inputVariableType;
    }

    public void setInputVariableType(EInputVariableType inputVariableType) {
        this.inputVariableType = inputVariableType;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    @Override
    public String toString() {
        return "InputVariableTypeData [inputVariableType=" + inputVariableType + ", repoName=" + repoName + "]";
    }

}
