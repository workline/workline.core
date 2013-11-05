package workflow.core.api.internal;

import org.drools.runtime.process.WorkItemHandler;

public interface IBasicWorkItemHandler extends WorkItemHandler {
    Long getWorkItemId();
}
