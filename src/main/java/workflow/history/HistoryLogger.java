package workflow.history;

import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowLogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.audit.event.RuleFlowVariableLogEvent;
import org.drools.event.KnowledgeRuntimeEventManager;

import workflow.model.ProcessData;

@Singleton
@Lock(LockType.READ)
public class HistoryLogger extends WorkingMemoryLogger {
    private EntityManagerFactory entityManagerFactory;
    private TransactionManager transactionManager;
    private Map<String, Object> parameters;

    public HistoryLogger() {
        super();
    }

    public HistoryLogger(KnowledgeRuntimeEventManager session, EntityManagerFactory entityManagerFactory, TransactionManager transactionManager,
            Map<String, Object> parameters) {
        super(session);
        this.entityManagerFactory = entityManagerFactory;
        this.transactionManager = transactionManager;
        this.parameters = parameters;
    }

    public HistoryLogger(WorkingMemory workingMemory) {
        super(workingMemory);
    }

    @Override
    public void logEventCreated(LogEvent logEvent) {
        int logEventType = logEvent.getType();
        String logEventClassSimpleName = logEvent.getClass().getSimpleName();

        String processId;
        String processName;
        Long processInstanceId;

        String nodeId;
        String nodeName;
        String nodeInstanceId;

        String variableId;
        String variableInstanceId;
        String objectToString;

        if (logEvent instanceof RuleFlowLogEvent) {
            RuleFlowLogEvent ruleFlowLogEvent = (RuleFlowLogEvent) logEvent;

            processId = ruleFlowLogEvent.getProcessId();
            processName = ruleFlowLogEvent.getProcessName();
            processInstanceId = ruleFlowLogEvent.getProcessInstanceId();
        } else {
            processId = null;
            processName = null;
            processInstanceId = null;
        }

        if (logEvent instanceof RuleFlowNodeLogEvent) {
            RuleFlowNodeLogEvent ruleFlowNodeLogEvent = (RuleFlowNodeLogEvent) logEvent;

            nodeId = ruleFlowNodeLogEvent.getNodeId();
            nodeName = ruleFlowNodeLogEvent.getNodeName();
            nodeInstanceId = ruleFlowNodeLogEvent.getNodeInstanceId();
        } else {
            nodeId = null;
            nodeName = null;
            nodeInstanceId = null;
        }

        if (logEvent instanceof RuleFlowVariableLogEvent) {
            RuleFlowVariableLogEvent ruleFlowVariableLogEvent = (RuleFlowVariableLogEvent) logEvent;

            variableId = ruleFlowVariableLogEvent.getVariableId();
            variableInstanceId = ruleFlowVariableLogEvent.getVariableInstanceId();
            objectToString = ruleFlowVariableLogEvent.getObjectToString();

            return;
        } else {
            variableId = null;
            variableInstanceId = null;
            objectToString = null;
        }

        // // System.err.println("HISTORY " + logEvent);
        // // System.err.println("HISTORY " + parameters);

        Transaction suspendedTransaction = null;
        try {
            boolean newTransaction;

            // Transaction transaction = transactionManager.getTransaction();

            // if (transaction == null) {
            suspendedTransaction = transactionManager.suspend();
            transactionManager.begin();

            newTransaction = true;
            // } else {
            // newTransaction = false;
            // }

            ProcessData processData = new ProcessData();
            processData.setTimeStamp(System.nanoTime());

            processData.setLogEventType(logEventType);
            processData.setLogEventClassSimpleName(logEventClassSimpleName);

            processData.setProcessId(processId);
            processData.setProcessName(processName);
            processData.setProcessInstanceId(processInstanceId);

            processData.setNodeId(nodeId);
            processData.setNodeName(nodeName);
            processData.setNodeInstanceId(nodeInstanceId);

            processData.setVariableId(variableId);
            processData.setVariableInstanceId(variableInstanceId);
            processData.setObjectToString(objectToString);

            entityManagerFactory.createEntityManager().persist(processData);

            if (newTransaction) {
                transactionManager.commit();
            }
        } catch (SystemException e) {
            e.printStackTrace();
            setRollbackOnlyOnTransactionManager();
        } catch (NotSupportedException e) {
            e.printStackTrace();
            setRollbackOnlyOnTransactionManager();
        } catch (SecurityException e) {
            e.printStackTrace();
            setRollbackOnlyOnTransactionManager();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            setRollbackOnlyOnTransactionManager();
        } catch (RollbackException e) {
            e.printStackTrace();
            setRollbackOnlyOnTransactionManager();
        } catch (HeuristicMixedException e) {
            e.printStackTrace();
            setRollbackOnlyOnTransactionManager();
        } catch (HeuristicRollbackException e) {
            e.printStackTrace();
            setRollbackOnlyOnTransactionManager();
        } finally {
            if (suspendedTransaction != null) {
                try {
                    transactionManager.resume(suspendedTransaction);
                } catch (InvalidTransactionException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    setRollbackOnlyOnTransactionManager();
                    e.printStackTrace();
                } catch (SystemException e) {
                    setRollbackOnlyOnTransactionManager();
                    e.printStackTrace();
                }
            }
        }
    }

    private void setRollbackOnlyOnTransactionManager() {
        try {
            transactionManager.setRollbackOnly();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (SystemException e1) {
            e1.printStackTrace();
        }
    }
}
