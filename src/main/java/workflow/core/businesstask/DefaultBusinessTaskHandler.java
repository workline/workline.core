package workflow.core.businesstask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import workflow.core.api.internal.IBusinessTaskHandler;
import workflow.core.domain.EInputVariableScope;
import workflow.core.domain.EInputVariableSelectionType;
import workflow.core.domain.EInputVariableType;
import workflow.core.domain.InputBehaviourLogicURI;
import workflow.core.domain.InputVariableSelectionData;
import workflow.core.domain.InputVariableTypeData;
import workflow.core.domain.ProcessVariableDefinition;
import workflow.core.engine.constants.WorklineEngineConstants;
import workflow.core.meta.SPECIFICATION_REQUIRED;
import workflow.core.repo.IRepoHandler;

public class DefaultBusinessTaskHandler implements IBusinessTaskHandler {
    @Inject
    private IRepoHandler repoHandler;

    @Override
    public void initTask(RepoItem businessTask) {
        preProcess(businessTask);
        initialIO(businessTask);
        // TODO LATER notify actors
    }

    @Override
    public void readVariable(RepoItem businessTask, String variableName) {
        // TODO LATER Auto-generated method stub

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

        String taskSpecificProcessVariableDefinitionData = businessTaskDefinition.getValue(WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION,
                StringAttributeValueHandler.getInstance());
        List<ProcessVariableDefinition> taskSpecificProcessVariableDefinitionList = parseIoVariableSourceData(taskSpecificProcessVariableDefinitionData);

        for (ProcessVariableDefinition taskSpecificProcessVariableDefinition : taskSpecificProcessVariableDefinitionList) {
            addAttributeToBusinessTask(businessTask, taskSpecificProcessVariableDefinition);
        }

        setContextDependentIO(businessTask);
    }

    private void postProcess(RepoItem businessTask) {
        // TODO LATER Auto-generated method stub

    }

    private void close(RepoItem businessTask) {
        // TODO LATER Auto-generated method stub

    }

    private void callInputBehaviourService(Long taskId, InputBehaviourLogicURI inputBehaviourLogicURI) {
        // FIXME LATER Auto-generated method stub

    }

    private void setContextDependentIO(RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);

        String ioVariableSourceExpression = businessTaskDefinition.getValue(WorklineEngineConstants.IO_VARIABLE_SOURCE,
                StringAttributeValueHandler.getInstance());
        List<ProcessVariableDefinition> contextDependentProcessVariableDefinitionList = getIoVariableSourceByExpression(businessTask,
                ioVariableSourceExpression);

        // TODO LATER Remove previous, not used attributes
        for (ProcessVariableDefinition contextDependentProcessVariableDefinition : contextDependentProcessVariableDefinitionList) {
            RepoItemAttribute ioAttribute = addAttributeToBusinessTask(businessTask, contextDependentProcessVariableDefinition);

            MetaAttribute dynamicTag = new MetaAttribute();
            dynamicTag.setOwnerAttribute(ioAttribute);
            dynamicTag.setNameAndType(WorklineEngineConstants.DYNAMIC_TAG, EAttributeType.STRING);
        }
    }

    private RepoItemAttribute addAttributeToBusinessTask(RepoItem businessTask, ProcessVariableDefinition variableDefinition) {
        RepoItemAttribute ioAttribute = new RepoItemAttribute();
        ioAttribute.setRepoItem(businessTask);
        ioAttribute.setNameAndType(variableDefinition.getName(), variableDefinition.getType().getInputVariableType().getAttributeType());

        persist(ioAttribute);

        return ioAttribute;
    }

    private List<ProcessVariableDefinition> getIoVariableSourceByExpression(RepoItem businessTask, String expression) {
        String flatData = getFlatDataByExpression(businessTask, expression);

        return parseIoVariableSourceData(flatData);
    }

    @SPECIFICATION_REQUIRED("How should accessRight RepoItems look like? What to do with the parsed data (inputVariableScope, inputVariableSelectionData)?")
    private List<ProcessVariableDefinition> parseIoVariableSourceData(String ioFlatData) {
        List<ProcessVariableDefinition> processVariableDefinitions = new ArrayList<>();

        /*
         * accessRight;REPO::AccessRight;PROCESS;ALL::REPO::AccessRight
         */
        // FIXME LATER Auto-generated method stub

        String[] ioFlatDataTokens = ioFlatData.split(";");
        String variableName = ioFlatDataTokens[0];
        String typeDataAsString = ioFlatDataTokens[1];
        String scopeAsString = ioFlatDataTokens[2];
        String selectionDataAsString = ioFlatDataTokens[3];

        String[] typeDataTokens = typeDataAsString.split("::");
        String typeAsString = typeDataTokens[0];
        String typeRepoName = typeDataTokens[1];

        EInputVariableScope inputVariableScope = EInputVariableScope.valueOf(scopeAsString);
        EInputVariableType inputVariableType = EInputVariableType.valueOf(typeAsString);
        List<?> inputVariableSelectionList = getInputVariableSelectionList(selectionDataAsString);

        ProcessVariableDefinition processVariableDefinition = new ProcessVariableDefinition();

        InputVariableSelectionData inputVariableSelectionData = new InputVariableSelectionData();
        inputVariableSelectionData.setInputVariableSelectionType(EInputVariableSelectionType.LIST);
        inputVariableSelectionData.setInputVariableSelectionList(inputVariableSelectionList);

        InputVariableTypeData type = new InputVariableTypeData();
        type.setInputVariableType(inputVariableType);
        type.setRepoName(typeRepoName);

        processVariableDefinition.setName(variableName);
        processVariableDefinition.setType(type);
        processVariableDefinition.setInputVariableScope(inputVariableScope);
        processVariableDefinition.setInputVariableSelectionData(inputVariableSelectionData);

        processVariableDefinitions.add(processVariableDefinition);

        return processVariableDefinitions;
    }

    private List<?> getInputVariableSelectionList(String selectionFlatData) {
        // FIXME Auto-generated method stub
        return new ArrayList<>();
    }

    private RepoItem getBusinessTaskDefinition(RepoItem businessTask) {
        RepoItem businessTaskDefinition = businessTask.getValue(WorklineEngineConstants.BUSINESS_TASK_DEFINITION, RepoItemAttributeValueHandler.getInstance());

        return businessTaskDefinition;
    }

    private void runInputBehaviourLogic(RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);
        String inputBehaviourSourceExpression = businessTaskDefinition.getValue(WorklineEngineConstants.INPUT_BEHAVIOUR_SOURCE,
                StringAttributeValueHandler.getInstance());

        InputBehaviourLogicURI inputBehaviourLogicURI = getInputBehaviourLogicURIyExpression(businessTask, inputBehaviourSourceExpression);

        callInputBehaviourService(businessTask.getId(), inputBehaviourLogicURI);
    }

    private InputBehaviourLogicURI getInputBehaviourLogicURIyExpression(RepoItem businessTask, String expression) {
        String flatData = getFlatDataByExpression(businessTask, expression);

        InputBehaviourLogicURI inputBehaviourLogicURI = parseInputBehaviourLogicURI(flatData);

        return inputBehaviourLogicURI;
    }

    private InputBehaviourLogicURI parseInputBehaviourLogicURI(String flatData) {
        // FIXME LATER Auto-generated method stub
        return null;
    }

    private String getFlatDataByExpression(RepoItem businessTask, String expression) {
        String[] expressionTokens = expression.split(".");
        String attributeNameReferencingRepoItem = expressionTokens[0];
        String attributeNameOfReferencedRepoItem = expressionTokens[1];

        RepoItem repoItemContainingFlatData = businessTask.getValue(attributeNameReferencingRepoItem, RepoItemAttributeValueHandler.getInstance());

        String flatData = repoItemContainingFlatData.getValue(attributeNameOfReferencedRepoItem, StringAttributeValueHandler.getInstance());

        return flatData;
    }

    private void persist(RepoItemAttribute ioAttribute) {
        repoHandler.persistRepoItemAttribute(ioAttribute);
    }

}
