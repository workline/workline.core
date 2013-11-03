package workflow.model;

import vrds.model.EAttributeType;

public enum EInputVariableType {
    REPO(EAttributeType.REPO_ITEM);

    private final EAttributeType attributeType;

    private EInputVariableType(EAttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public EAttributeType getAttributeType() {
        return attributeType;
    }
}
