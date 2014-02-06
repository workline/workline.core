package workline.core.domain;

import vrds.model.EAttributeType;
import vrds.model.meta.Coupling;
import vrds.model.meta.CouplingTag;

@Coupling(tags = { CouplingTag.ATTRIBUTE_TYPE })
public enum EInputVariableType {
    REPO(EAttributeType.REPO_ITEM), INTEGER(EAttributeType.INTEGER);

    private final EAttributeType attributeType;

    private EInputVariableType(EAttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public EAttributeType getAttributeType() {
        return attributeType;
    }
}
