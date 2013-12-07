package workline.core.domain;

public class MappedToData {
    private String variableName;
    private String variableNameOfVariable;

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableNameOfVariable() {
        return variableNameOfVariable;
    }

    public void setVariableNameOfVariable(String variableNameOfVariable) {
        this.variableNameOfVariable = variableNameOfVariable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((variableName == null) ? 0 : variableName.hashCode());
        result = prime * result + ((variableNameOfVariable == null) ? 0 : variableNameOfVariable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MappedToData other = (MappedToData) obj;
        if (variableName == null) {
            if (other.variableName != null)
                return false;
        } else if (!variableName.equals(other.variableName))
            return false;
        if (variableNameOfVariable == null) {
            if (other.variableNameOfVariable != null)
                return false;
        } else if (!variableNameOfVariable.equals(other.variableNameOfVariable))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MappedToData [variableName=" + variableName + ", variableNameOfVariable=" + variableNameOfVariable + "]";
    }

}
