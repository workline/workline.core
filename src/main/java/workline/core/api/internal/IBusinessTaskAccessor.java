package workline.core.api.internal;

import vrds.model.IValueWrapper;
import vrds.model.attributetype.AttributeValueHandler;

public interface IBusinessTaskAccessor {
    Object readVariable(Long businessTaskId, String variableName);

    <T, W extends IValueWrapper<T>> T readVariable(Long processElementId, String variableName, AttributeValueHandler<T, W> attributeValueHandler);

    void writeVariable(Long businessTaskId, String variableName, Long repoItemId);

    void writeVariable(Long businessTaskId, String variableName, Object value);

    void modifyVariable(Long businessTaskId, String variableName, String attributeName, Long repoItemId);

    void modifyVariable(Long businessTaskId, String variableName, String attributeName, Object value);
}
