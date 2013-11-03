package workflow.workitem;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

import vrds.model.RepoItem;
import vrds.model.attributetype.StringAttributeValueHandler;

public class TempWorkItemHandler implements WorkItemHandler {
    private static final String INPUT_VARIABLE_SOURCE = "inputVariableSource";
    private static final String INPUT_BEHAVIOUR_SOURCE = "inputBehaviourSource";

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // TODO Auto-generated method stub

    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // TODO Auto-generated method stub

    }

    public void tmp(RepoItem task, RepoItem process, WorkItem workItem, WorkItemManager manager) {
        String inputVariableSource = task.getValue(INPUT_VARIABLE_SOURCE, StringAttributeValueHandler.getInstance());
        String inputVariableDescription = getStringValue(inputVariableSource, workItem);

        String inputBehaviourSource = task.getValue(INPUT_VARIABLE_SOURCE, StringAttributeValueHandler.getInstance());
        String inputBehaviourDescription = getStringValue(inputVariableSource, workItem);
    }

    public String getStringValue(String expression, WorkItem workItem) {
        int indexOfDelimiter = expression.indexOf(".");
        String processParameterName = expression.substring(0, indexOfDelimiter);
        String attributeName = expression.substring(indexOfDelimiter);

        RepoItem repoItem = (RepoItem) workItem.getParameter(processParameterName);

        String value = repoItem.getValue(attributeName, StringAttributeValueHandler.getInstance());

        return value;
    }
}
