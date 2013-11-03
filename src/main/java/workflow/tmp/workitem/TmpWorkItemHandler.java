package workflow.tmp.workitem;

import java.util.List;

import javax.persistence.EntityManager;

import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

import workflow.engine.ThiefEntityManagerFactory;
import workflow.model.Member;

public class TmpWorkItemHandler implements WorkItemHandler {
    private Long workItemId;
    private ThiefEntityManagerFactory thiefEntityManagerFactory;

    public TmpWorkItemHandler(ThiefEntityManagerFactory thiefEntityManagerFactory) {
        this.thiefEntityManagerFactory = thiefEntityManagerFactory;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        System.err.println("Work item name: " + workItem.getName());

        // TODO delete
        System.err.println("TMP: " + workItem.getParameter("tmp"));

        List<EntityManager> entityManagers = thiefEntityManagerFactory.getEntityManagers();
        if (entityManagers != null) {
            for (EntityManager entityManager : entityManagers) {
                ProcessInstanceInfo processInstanceInfo = entityManager.find(ProcessInstanceInfo.class, workItem.getProcessInstanceId());
                if (processInstanceInfo != null) {
                    Member member = new Member();
                    member.setName("memberName" + entityManager);
                    entityManager.persist(member);
                    try {
                        // entityManager.flush();
                        // System.err.println("WORK_ITEM_HANDLER Saved " + member.getName());
                    } catch (Exception e) {
                        // System.err.println("WORK_ITEM_HANDLER " + e.getMessage());
                    }
                }
            }
        }

        workItemId = workItem.getId();
        // System.err.println("WORK_ITEM_HANDLER execute workItem " + workItem.getName() + ", work item id: " + workItemId);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // System.err.println("WORK_ITEM_HANDLER abort workItem " + workItem.getName());
    }

    public Long getWorkItemId() {
        return workItemId;
    }
}
