package workline.core.processtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.IValueWrapper;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.AttributeValueHandler;
import vrds.model.attributetype.IntegerAttributeValueHandler;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.api.internal.IEngine;
import workline.core.api.internal.IIoVariableSourceDataParser;
import workline.core.api.internal.IProcessElementService;
import workline.core.api.internal.IProcessTaskHandler;
import workline.core.api.internal.IRepoHandler;
import workline.core.domain.EInputVariableScope;
import workline.core.domain.ProcessElementVariableMappingDefinition;
import workline.core.engine.constants.WorklineEngineConstants;
import workline.core.engine.constants.WorklineEngineQueries;
import workline.core.util.Primary;

@Logged
@Stateless
public class DefaultProcessTaskHandler implements IProcessTaskHandler {
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IBusinessTaskHandler businessTaskHandler;
    @Inject
    private IProcessElementService processElementService;
    @Inject
    private IEngine engine;
    @Inject
    private IIoVariableSourceDataParser ioVariableSourceDataParser;

    @Inject
    @Primary
    private EntityManager entityManager;

    @Override
    public void initProcessTask(Long processRepoItemId, Long workflowTaskId, String taskName) {
        RepoItem process = getProcess(processRepoItemId);
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(taskName);

        RepoItem processTask = createNewProcessTask(process, taskName, workflowTaskId);

        createTaskSpecificProcessVariables(process, businessTaskDefinition);

        List<RepoItem> listOfbusinessTasks = createBusinessTasks(taskName, businessTaskDefinition);

        addBusinessTasksToProcessTask(processTask, listOfbusinessTasks);

        for (RepoItem businessTask : listOfbusinessTasks) {
            repoHandler.addAttribute(businessTask, WorklineEngineConstants.PROCESS, EAttributeType.REPO_ITEM, process);
            repoHandler.addAttribute(businessTask, WorklineEngineConstants.PROCESS_TASK, EAttributeType.REPO_ITEM, processTask);
            businessTaskHandler.initBusinessTask(process, businessTask);
        }

    }

    @TODO(tags = { TODOTag.PROCESS_TO_TASK_RELATIONSHIP }, value = "Probably need to copy task variables to process variables at this point.")
    @Override
    public void finishBusinessTask(Long businessTaskId) {
        // TODO Auto-generated method stub
        // FIXME This is extremely simplified.
        // Different logic must be called depending on the task.
        if (isLast(businessTaskId)) {
            RepoItem businessTask = getBusinessTask(businessTaskId);
            RepoItem processTask = repoHandler.getNonInheritingValue(businessTask, WorklineEngineConstants.PROCESS_TASK,
                    RepoItemAttributeValueHandler.getInstance());

            finishProcessTask(processTask);
        }
    }

    @Override
    public Object readVariable(Long businessTaskId, String variableName) {
        return businessTaskHandler.readVariable(businessTaskId, variableName);
    }

    @Override
    public <T, W extends IValueWrapper<T>> T readVariable(Long businessTaskId, String variableName, AttributeValueHandler<T, W> attributeValueHandler) {
        return businessTaskHandler.readVariable(businessTaskId, variableName, attributeValueHandler);
    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Long repoItemId) {
        businessTaskHandler.writeVariable(businessTaskId, variableName, repoItemId);
    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Object value) {
        businessTaskHandler.writeVariable(businessTaskId, variableName, value);
    }

    @Override
    public void modifyVariable(Long businessTaskId, String variableName, String attributeName, Long repoItemId) {
        businessTaskHandler.modifyVariable(businessTaskId, variableName, attributeName, repoItemId);
    }

    @Override
    public void modifyVariable(Long businessTaskId, String variableName, String attributeName, Object value) {
        businessTaskHandler.modifyVariable(businessTaskId, variableName, attributeName, value);
    }

    private void createTaskSpecificProcessVariables(RepoItem process, RepoItem businessTaskDefinition) {
        String taskSpecificProcessVariableMappingDefinitionData = repoHandler.getNonInheritingValue(businessTaskDefinition,
                WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION, StringAttributeValueHandler.getInstance());
        List<ProcessElementVariableMappingDefinition> taskSpecificProcessVariableMappingDefinitionList = ioVariableSourceDataParser
                .parseIoVariableSourceData(Collections.singleton(taskSpecificProcessVariableMappingDefinitionData));

        for (ProcessElementVariableMappingDefinition taskSpecificProcessVariableMappingDefinition : taskSpecificProcessVariableMappingDefinitionList) {
            if (taskSpecificProcessVariableMappingDefinition.getInputVariableScope() == EInputVariableScope.PROCESS) {
                processElementService.processVariableMappingForProcessElement(process, taskSpecificProcessVariableMappingDefinition);
            }
            // else {
            // // Nothing to do. TASK scoped variables are added to the business tasks later.
            // }
        }
    }

    @TODO(
            tags = { TODOTag.MISSING_IMPLEMENTATION, TODOTag.SPECIFICATION_REQUIRED },
            value = "How to create business tasks? | A business task needs to be created for every participating actor/actor group.")
    private List<RepoItem> createBusinessTasks(String taskName, RepoItem businessTaskDefinition) {
        // TODO Auto-generated method stub

        List<RepoItem> businessTasks = new ArrayList<>();

        RepoItem businessTask = new RepoItem();

        repoHandler.addAttribute(businessTask, WorklineEngineConstants.BUSINESS_TASK_DEFINITION, EAttributeType.REPO_ITEM, businessTaskDefinition);
        repoHandler.addAttribute(businessTask, WorklineEngineConstants.FINISHED, EAttributeType.INTEGER);

        repoHandler.persistRepoItem(businessTask);

        businessTasks.add(businessTask);

        return businessTasks;
    }

    private boolean isLast(Long businessTaskId) {
        // TODO Auto-generated method stub
        return true;
    }

    private void finishProcessTask(RepoItem processTask) {
        Long workflowTaskId = repoHandler.getNonInheritingValue(processTask, WorklineEngineConstants.WORKFLOW_TASK_ID,
                IntegerAttributeValueHandler.getInstance());

        RepoItem process = repoHandler.getNonInheritingValue(processTask, WorklineEngineConstants.PROCESS, RepoItemAttributeValueHandler.getInstance());
        Long processInstanceId = repoHandler.getNonInheritingValue(process, WorklineEngineConstants.PROCESS_INSTANCE_ID,
                IntegerAttributeValueHandler.getInstance());
        Long sessionId = repoHandler.getNonInheritingValue(process, WorklineEngineConstants.SESSION_ID, IntegerAttributeValueHandler.getInstance());

        engine.completeWorkItem((int) (long) sessionId, processInstanceId, workflowTaskId);
    }

    private RepoItem createNewProcessTask(RepoItem process, String taskName, Long workflowTaskId) {
        RepoItem processTask = new RepoItem();

        repoHandler.addAttribute(processTask, WorklineEngineConstants.PROCESS, EAttributeType.REPO_ITEM, process);
        repoHandler.addAttribute(processTask, WorklineEngineConstants.WORKFLOW_TASK_ID, EAttributeType.INTEGER, workflowTaskId);
        // TODO Do we need this?
        repoHandler.addAttribute(processTask, WorklineEngineConstants.TASK_NAME, EAttributeType.STRING, taskName);
        repoHandler.persistRepoItem(processTask);

        return processTask;
    }

    private void addBusinessTasksToProcessTask(RepoItem processTask, List<RepoItem> listOfbusinessTasks) {
        RepoItemAttribute attributeBusinessTasksOfProcessTask = repoHandler.addAttribute(processTask, WorklineEngineConstants.BUSINESS_TASKS,
                EAttributeType.REPO_ITEM);
        RepoItemAttributeValueHandler.getInstance().setSimpleValues(attributeBusinessTasksOfProcessTask, listOfbusinessTasks);
    }

    private RepoItem getProcess(Long processRepoItemId) {
        return repoHandler.getRepoItem(processRepoItemId);
    }

    private RepoItem getBusinessTask(Long businessTaskId) {
        return repoHandler.getRepoItem(businessTaskId);
    }

    private RepoItem getBusinessTaskDefinition(String taskName) {
        RepoItem businessTaskDefinition = entityManager.createQuery(WorklineEngineQueries.NQ_FIND_BUSINESS_TASK_DEFINITION_BY_TASK_NAME, RepoItem.class)
                .setParameter(WorklineEngineQueries.NQ_PARAMETER_TASK_NAME, taskName).getSingleResult();

        return businessTaskDefinition;
    }

}
