package workline.core.businesstask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.api.internal.IProcessElementService;
import workline.core.api.internal.IRepoHandler;
import workline.core.domain.EInputVariableScope;
import workline.core.domain.EInputVariableType;
import workline.core.domain.InputBehaviourLogicURI;
import workline.core.domain.InputVariableTypeData;
import workline.core.domain.MappedToData;
import workline.core.domain.ProcessElementVariableDefinition;
import workline.core.engine.constants.WorklineEngineConstants;
import workline.core.repo.manager.IRepoManager;

@Logged
public class DefaultBusinessTaskHandler implements IBusinessTaskHandler {
    @Inject
    private IRepoManager repoManager;
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IProcessElementService processElementService;

    @Override
    public void initTask(RepoItem process, RepoItem businessTask) {
        preProcess(businessTask);
        initialIO(process, businessTask);
        // TODO LATER Notify actors
    }

    @TODO(tags = { TODOTag.MISSING_IMPLEMENTATION, TODOTag.INHERITENCE })
    @Override
    public Object readVariable(Long businessTaskId, String variableName) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        return businessTask.getValue(variableName);
    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Long repoItemId) {
        RepoItem repoItem = repoHandler.getRepoItem(repoItemId);

        writeVariable(businessTaskId, variableName, repoItem);
    }

    @TODO(tags = { TODOTag.INHERITENCE })
    @Override
    public void writeVariable(Long businessTaskId, String variableName, Object value) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        repoManager.setValue(businessTask, variableName, value);

        RepoItemAttribute variable = businessTask.getAttribute(variableName);
        setMappedVariableIfNeeded(variable, value, businessTask);

        runInputBehaviourLogic(businessTask);

        RepoItem process = businessTask.getValue(WorklineEngineConstants.PROCESS, RepoItemAttributeValueHandler.getInstance());
        setContextDependentIO(process, businessTask);
    }

    @Override
    public void finishTask(RepoItem businessTask) {
        postProcess(businessTask);
        close(businessTask);
    }

    @TODO(
            tags = { TODOTag.SPECIFICATION_REQUIRED },
            value = "How should accessRight RepoItems look like? What to do with the parsed data (inputVariableScope, inputVariableSelectionData)?")
    public List<ProcessElementVariableDefinition> parseIoVariableSourceData(String ioFlatData) {
        if (ioFlatData == null) {
            return Collections.emptyList();
        }

        List<ProcessElementVariableDefinition> processElementVariableDefinitions = new ArrayList<>();

        String[] ioFlatDataTokens = ioFlatData.split(";");
        String variableName = ioFlatDataTokens[0];
        String typeDataAsString = ioFlatDataTokens[1];
        String scopeAsString = ioFlatDataTokens[2];
        String inputVariableSelectionQuery = ioFlatDataTokens[3];

        String[] typeDataTokens = typeDataAsString.split("::");
        String typeAsString = typeDataTokens[0];
        String typeRepoName = typeDataTokens[1];

        EInputVariableScope inputVariableScope = EInputVariableScope.valueOf(scopeAsString);
        EInputVariableType inputVariableType = EInputVariableType.valueOf(typeAsString);

        ProcessElementVariableDefinition processElementVariableDefinition = new ProcessElementVariableDefinition();

        InputVariableTypeData type = new InputVariableTypeData();
        type.setInputVariableType(inputVariableType);
        type.setRepoName(typeRepoName);

        processElementVariableDefinition.setName(variableName);
        processElementVariableDefinition.setType(type);
        processElementVariableDefinition.setInputVariableScope(inputVariableScope);
        processElementVariableDefinition.setInputVariableSelectionQuery(inputVariableSelectionQuery);

        processElementVariableDefinitions.add(processElementVariableDefinition);

        return processElementVariableDefinitions;
    }

    private void setMappedVariableIfNeeded(RepoItemAttribute variable, Object value, RepoItem businessTask) {
        String mappedToExpression = variable.getMetaAttributeValue(WorklineEngineConstants.VARIABLE_MAPPED_TO_EXPRESSION,
                StringAttributeValueHandler.getInstance());

        if (mappedToExpression != null) {
            MappedToData mappedToData = parseMappedToExpression(mappedToExpression);

            RepoItem mappedToVariable = businessTask.getValue(mappedToData.getVariableName(), RepoItemAttributeValueHandler.getInstance());
            repoManager.setValue(mappedToVariable, mappedToData.getVariableNameOfVariable(), value);
        }

    }

    private MappedToData parseMappedToExpression(String mappedToExpression) {
        MappedToData mappedToData = new MappedToData();

        String[] mappedToExpressionTokens = mappedToExpression.split("\\.");

        String variableName = mappedToExpressionTokens[0];
        String variableNameOfVariable = mappedToExpressionTokens[1];

        mappedToData.setVariableName(variableName);
        mappedToData.setVariableNameOfVariable(variableNameOfVariable);

        return null;
    }

    private void preProcess(RepoItem businessTask) {
        // TODO LATER Implement
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
        // FIXME LATER Auto-generated method stub
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
            MetaAttribute dynamicTag = repoHandler.createMetaAttribute(WorklineEngineConstants.DYNAMIC, EAttributeType.STRING, ioVariableSourceExpression);

            processElementService.addVariableToProcessElement(businessTask, contextDependentProcessVariableDefinition, dynamicTag);

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

    @TODO(tags = { TODOTag.MISSING_IMPLEMENTATION, TODOTag.INHERITENCE }, value = "getValue() inheritence")
    private String getFlatDataByExpression(RepoItem businessTask, String expression) {
        String flatData;

        String[] expressionTokens = expression.split("\\.");
        String attributeNameReferencingRepoItem = expressionTokens[0];
        String attributeNameOfReferencedRepoItem = expressionTokens[1];

        RepoItem repoItemContainingFlatData = businessTask.getValue(attributeNameReferencingRepoItem, RepoItemAttributeValueHandler.getInstance());

        if (repoItemContainingFlatData == null) {
            flatData = null;
        } else {
            flatData = repoItemContainingFlatData.getValue(attributeNameOfReferencedRepoItem, StringAttributeValueHandler.getInstance());
        }

        return flatData;
    }

}
