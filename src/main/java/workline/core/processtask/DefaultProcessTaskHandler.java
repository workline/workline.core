package workline.core.processtask;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.IntegerAttributeValueHandler;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.api.internal.IEngine;
import workline.core.api.internal.IProcessElementService;
import workline.core.api.internal.IProcessTaskHandler;
import workline.core.api.internal.IRepoHandler;
import workline.core.domain.EInputVariableScope;
import workline.core.domain.ProcessElementVariableDefinition;
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
            // TODO Do we need this? The process task should already know about the process.
            repoHandler.addAttribute(businessTask, WorklineEngineConstants.PROCESS, EAttributeType.REPO_ITEM, process);
            repoHandler.addAttribute(businessTask, WorklineEngineConstants.PROCESS_TASK, EAttributeType.REPO_ITEM, processTask);
            businessTaskHandler.initTask(process, businessTask);
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
            RepoItem processTask = businessTask.getValue(WorklineEngineConstants.PROCESS_TASK, RepoItemAttributeValueHandler.getInstance());

            finishProcessTask(processTask);
        }
    }

    @Override
    public Object readVariable(Long businessTaskId, String variableName) {
        return businessTaskHandler.readVariable(businessTaskId, variableName);

    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Long repoItemId) {
        businessTaskHandler.writeVariable(businessTaskId, variableName, repoItemId);
    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Object value) {
        businessTaskHandler.writeVariable(businessTaskId, variableName, value);
    }

    private void createTaskSpecificProcessVariables(RepoItem process, RepoItem businessTaskDefinition) {
        String taskSpecificProcessVariableDefinitionData = businessTaskDefinition.getValue(WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION,
                StringAttributeValueHandler.getInstance());
        List<ProcessElementVariableDefinition> taskSpecificProcessVariableDefinitionList = businessTaskHandler
                .parseIoVariableSourceData(taskSpecificProcessVariableDefinitionData);

        for (ProcessElementVariableDefinition taskSpecificProcessVariableDefinition : taskSpecificProcessVariableDefinitionList) {
            if (taskSpecificProcessVariableDefinition.getInputVariableScope() == EInputVariableScope.PROCESS) {
                processElementService.addVariableToProcessElement(process, taskSpecificProcessVariableDefinition);
            } else {
                // Nothing to do. TASK scoped variables are added to the business tasks later.
            }
        }
    }

    @TODO(
            tags = { TODOTag.SPECIFICATION_REQUIRED },
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
        Long workflowTaskId = processTask.getValue(WorklineEngineConstants.WORKFLOW_TASK_ID, IntegerAttributeValueHandler.getInstance());

        RepoItem process = processTask.getValue(WorklineEngineConstants.PROCESS, RepoItemAttributeValueHandler.getInstance());
        Long processInstanceId = process.getValue(WorklineEngineConstants.PROCESS_INSTANCE_ID, IntegerAttributeValueHandler.getInstance());
        Long sessionId = process.getValue(WorklineEngineConstants.SESSION_ID, IntegerAttributeValueHandler.getInstance());

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
        RepoItemAttribute attrbiuteBusinessTasksOfProcessTask = repoHandler.addAttribute(processTask, WorklineEngineConstants.BUSINESS_TASKS,
                EAttributeType.REPO_ITEM);
        RepoItemAttributeValueHandler.getInstance().setSimpleValues(attrbiuteBusinessTasksOfProcessTask, listOfbusinessTasks);
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
