package workflow.core.businesstask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import workflow.core.api.internal.IBusinessTaskHandler;
import workflow.core.api.internal.IProcessElementService;
import workflow.core.api.internal.IRepoHandler;
import workflow.core.domain.EInputVariableScope;
import workflow.core.domain.EInputVariableSelectionType;
import workflow.core.domain.EInputVariableType;
import workflow.core.domain.InputBehaviourLogicURI;
import workflow.core.domain.InputVariableSelectionData;
import workflow.core.domain.InputVariableTypeData;
import workflow.core.domain.ProcessElementVariableDefinition;
import workflow.core.engine.constants.WorklineEngineConstants;
import workflow.core.meta.SPECIFICATION_REQUIRED;

@Logged
public class DefaultBusinessTaskHandler implements IBusinessTaskHandler {
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IProcessElementService processElementService;

    @Override
    public void initTask(RepoItem process, RepoItem businessTask) {
        preProcess(businessTask);
        initialIO(process, businessTask);
        // TODO LATER notify actors
    }

    @Override
    public Object readVariable(Long businessTaskId, String variableName) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        return businessTask.getValue(variableName);
    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Object value) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        businessTask.setValue(variableName, value);

        runInputBehaviourLogic(businessTask);

        RepoItem process = businessTask.getValue(WorklineEngineConstants.PROCESS, RepoItemAttributeValueHandler.getInstance());
        setContextDependentIO(process, businessTask);
    }

    public void set(String taskVariableName, Object value) {

    }

    @Override
    public void finishTask(RepoItem businessTask) {
        postProcess(businessTask);
        close(businessTask);
    }

    @SPECIFICATION_REQUIRED("How should accessRight RepoItems look like? What to do with the parsed data (inputVariableScope, inputVariableSelectionData)?")
    public List<ProcessElementVariableDefinition> parseIoVariableSourceData(String ioFlatData) {
        List<ProcessElementVariableDefinition> processElementVariableDefinitions = new ArrayList<>();

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

        ProcessElementVariableDefinition processElementVariableDefinition = new ProcessElementVariableDefinition();

        InputVariableSelectionData inputVariableSelectionData = new InputVariableSelectionData();
        inputVariableSelectionData.setInputVariableSelectionType(EInputVariableSelectionType.LIST);
        inputVariableSelectionData.setInputVariableSelectionList(inputVariableSelectionList);

        InputVariableTypeData type = new InputVariableTypeData();
        type.setInputVariableType(inputVariableType);
        type.setRepoName(typeRepoName);

        processElementVariableDefinition.setName(variableName);
        processElementVariableDefinition.setType(type);
        processElementVariableDefinition.setInputVariableScope(inputVariableScope);
        processElementVariableDefinition.setInputVariableSelectionData(inputVariableSelectionData);

        processElementVariableDefinitions.add(processElementVariableDefinition);

        return processElementVariableDefinitions;
    }

    private void preProcess(RepoItem businessTask) {
        // TODO
    }

    private void initialIO(RepoItem process, RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);

        String taskSpecificProcessVariableDefinitionData = businessTaskDefinition.getValue(WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION,
                StringAttributeValueHandler.getInstance());
        List<ProcessElementVariableDefinition> taskSpecificProcessVariableDefinitionList = parseIoVariableSourceData(taskSpecificProcessVariableDefinitionData);

        for (ProcessElementVariableDefinition taskSpecificProcessVariableDefinition : taskSpecificProcessVariableDefinitionList) {
            processElementService.addVariableToProcessElement(businessTask, taskSpecificProcessVariableDefinition);

            if (taskSpecificProcessVariableDefinition.getInputVariableScope() == EInputVariableScope.PROCESS) {
                processElementService.copyProcessElementVariable(process, businessTask, taskSpecificProcessVariableDefinition);
            }
        }

        setContextDependentIO(process, businessTask);
    }

    private void postProcess(RepoItem businessTask) {
        // FIXME Auto-generated method stub
        // TODO Probably should add task specific process variables to process at this point
    }

    private void close(RepoItem businessTask) {
        // TODO LATER Auto-generated method stub

    }

    private void callInputBehaviourService(Long taskId, InputBehaviourLogicURI inputBehaviourLogicURI) {
        // FIXME LATER Auto-generated method stub

    }

    private void setContextDependentIO(RepoItem process, RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);

        String ioVariableSourceExpression = businessTaskDefinition.getValue(WorklineEngineConstants.IO_VARIABLE_SOURCE,
                StringAttributeValueHandler.getInstance());
        List<ProcessElementVariableDefinition> contextDependentProcessVariableDefinitionList = getIoVariableSourceByExpression(businessTask,
                ioVariableSourceExpression);

        // TODO LATER Remove previous, not used attributes
        for (ProcessElementVariableDefinition contextDependentProcessVariableDefinition : contextDependentProcessVariableDefinitionList) {
            MetaAttribute dynamicTag = new MetaAttribute();
            dynamicTag.setNameAndType(WorklineEngineConstants.DYNAMIC, EAttributeType.STRING);
            dynamicTag.setValue(ioVariableSourceExpression);

            processElementService.addVariableToProcessElement(businessTask, contextDependentProcessVariableDefinition);

            if (contextDependentProcessVariableDefinition.getInputVariableScope() == EInputVariableScope.PROCESS) {
                if (process.hasAttribute(contextDependentProcessVariableDefinition.getName())) {
                    processElementService.copyProcessElementVariable(process, businessTask, contextDependentProcessVariableDefinition);
                } else {
                    processElementService.addVariableToProcessElement(process, contextDependentProcessVariableDefinition);
                }
            } else {
                // Do nothing. All variables (regardless of scope) has already been added to business task.
            }
        }
    }

    private List<ProcessElementVariableDefinition> getIoVariableSourceByExpression(RepoItem businessTask, String expression) {
        String flatData = getFlatDataByExpression(businessTask, expression);

        return parseIoVariableSourceData(flatData);
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

}
