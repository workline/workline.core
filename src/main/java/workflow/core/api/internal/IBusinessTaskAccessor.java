package workflow.core.api.internal;

public interface IBusinessTaskAccessor {
    Object readVariable(Long businessTaskId, String variableName);

    void writeVariable(Long businessTaskId, String variableName, Object value);
}
