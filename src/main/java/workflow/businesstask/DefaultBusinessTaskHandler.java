package workflow.businesstask;

import java.util.List;

import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.StringAttributeValueHandler;
import workflow.model.InputBehaviourLogicURI;
import workflow.model.ProcessVariableDefinition;

public class DefaultBusinessTaskHandler implements IBusinessTaskHandler {
    /*
     * BusinessTask process (process instance) task (workflow task)
     */

    private static final String DYNAMIC_TAG = "dynamicTag";
    private static final String PROCESS = "process";
    private static final String TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION = "taskSpecificProcessVariablesDefinition";
    private static final String IO_VARIABLE_SOURCE = "ioVariableSource";
    private static final String INPUT_BEHAVIOUR_SOURCE = "inputBehaviourSource";

    @Override
    public void initTask(RepoItem businessTask) {
        preProcess(businessTask);
        initialIO(businessTask);
        // TODO notify actors
    }

    @Override
    public void readVariable(RepoItem businessTask, String variableName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeVariable(RepoItem businessTask, String variableName, Object value) {
        businessTask.setValue(variableName, value);

        runInputBehaviourLogic(businessTask);

        setContextDependentIO(businessTask);
    }

    public void set(String taskVariableName, Object value) {

    }

    @Override
    public void finishTask(RepoItem businessTask) {
        postProcess(businessTask);
        close(businessTask);
    }

    private void preProcess(RepoItem businessTask) {

    }

    private void initialIO(RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);

        String taskSpecificProcessVariableDefinitionData = businessTaskDefinition.getValue(TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION,
                StringAttributeValueHandler.getInstance());
        List<ProcessVariableDefinition> taskSpecificProcessVariableDefinitionList = parseIoVariableSourceData(taskSpecificProcessVariableDefinitionData);

        for (ProcessVariableDefinition taskSpecificProcessVariableDefinition : taskSpecificProcessVariableDefinitionList) {
            addAttributeToBusinessTask(businessTask, taskSpecificProcessVariableDefinition);
        }

        setContextDependentIO(businessTask);
    }

    private void postProcess(RepoItem businessTask) {
        // TODO Auto-generated method stub

    }

    private void close(RepoItem businessTask) {
        // TODO Auto-generated method stub

    }

    private void callInputBehaviourService(Long taskId, InputBehaviourLogicURI inputBehaviourLogicURI) {
        // TODO Auto-generated method stub

    }

    private void setContextDependentIO(RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);

        String ioVariableSourceExpression = businessTaskDefinition.getValue(IO_VARIABLE_SOURCE, StringAttributeValueHandler.getInstance());
        List<ProcessVariableDefinition> contextDependentProcessVariableDefinitionList = getIoVariableSourceByExpression(businessTask, ioVariableSourceExpression);

        // TODO Remove previous, not used attributes
        for (ProcessVariableDefinition contextDependentProcessVariableDefinition : contextDependentProcessVariableDefinitionList) {
            RepoItemAttribute ioAttribute = addAttributeToBusinessTask(businessTask, contextDependentProcessVariableDefinition);

            MetaAttribute dynamicTag = new MetaAttribute();
            dynamicTag.setOwnerAttribute(ioAttribute);
            dynamicTag.setNameAndType(DYNAMIC_TAG, EAttributeType.STRING);
        }
    }

    private RepoItemAttribute addAttributeToBusinessTask(RepoItem businessTask, ProcessVariableDefinition variableDefinition) {
        RepoItemAttribute ioAttribute = new RepoItemAttribute();
        ioAttribute.setRepoItem(businessTask);
        ioAttribute.setNameAndType(variableDefinition.getName(), variableDefinition.getType().getInputVariableType().getAttributeType());

        persist(ioAttribute);

        return ioAttribute;
    }

    private List<ProcessVariableDefinition> parseIoVariableSourceData(String staticIO) {
        /*
         * accessRight;REPO::AccessRight;PROCESS;ALL::REPO::AccessRight
         */

        // TODO Auto-generated method stub
        return null;
    }

    private List<ProcessVariableDefinition> getIoVariableSourceByExpression(RepoItem businessTask, String expression) {
        /*
         * accessRight.(Additional input for Access right request Define request)
         */

        // TODO Auto-generated method stub
        return null;
    }

    private RepoItem getBusinessTaskDefinition(RepoItem businessTask) {
        // TODO Auto-generated method stub
        return null;
    }

    private void persist(RepoItemAttribute ioAttribute) {
        // TODO Auto-generated method stub

    }

    private void runInputBehaviourLogic(RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);
        String inputBehaviourSourceExpression = businessTaskDefinition.getValue(INPUT_BEHAVIOUR_SOURCE, StringAttributeValueHandler.getInstance());

        InputBehaviourLogicURI inputBehaviourLogicURI = getInputBehaviourLogicURIyExpression(inputBehaviourSourceExpression);

        callInputBehaviourService(businessTask.getId(), inputBehaviourLogicURI);
    }

    private InputBehaviourLogicURI getInputBehaviourLogicURIyExpression(String inputBehaviourSourceExpression) {
        // TODO Auto-generated method stub
        return null;
    }

}
