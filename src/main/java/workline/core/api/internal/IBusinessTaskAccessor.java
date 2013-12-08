package workline.core.api.internal;

public interface IBusinessTaskAccessor {
    Object readVariable(Long businessTaskId, String variableName);

    void writeVariable(Long businessTaskId, String variableName, Long repoItemId);

    void writeVariable(Long businessTaskId, String variableName, Object value);
}
