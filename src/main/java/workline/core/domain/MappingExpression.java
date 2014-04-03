package workline.core.domain;

public class MappingExpression {
    private MappingDirection mappingDirection;
    private String variableExpression;

    public MappingDirection getMappingDirection() {
        return mappingDirection;
    }

    public void setMappingDirection(MappingDirection mappingDirection) {
        this.mappingDirection = mappingDirection;
    }

    public String getVariableExpression() {
        return variableExpression;
    }

    public void setVariableExpression(String variableExpression) {
        this.variableExpression = variableExpression;
    }

    @Override
    public String toString() {
        return "MappingExpression [mappingDirection=" + mappingDirection + ", variableExpression=" + variableExpression + "]";
    }

}
