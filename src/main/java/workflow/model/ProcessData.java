package workflow.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.drools.runtime.StatefulKnowledgeSession;

@Entity
public class ProcessData {
    @Id
    @GeneratedValue
    private Long id;

    private Long timeStamp;

    private Integer logEventType;
    private String logEventClassSimpleName;

    private Integer sessionId;

    private String processId;
    private String processName;
    private Long processInstanceId;

    private String nodeId;
    private String nodeName;
    private String nodeInstanceId;

    private String variableId;
    private String variableInstanceId;
    private String objectToString;

    private transient Long workItemId;
    private transient StatefulKnowledgeSession statefulKnowledgeSession;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public Integer getLogEventType() {
        return logEventType;
    }

    public void setLogEventType(Integer logEventType) {
        this.logEventType = logEventType;
    }

    public String getLogEventClassSimpleName() {
        return logEventClassSimpleName;
    }

    public void setLogEventClassSimpleName(String logEventClassSimpleName) {
        this.logEventClassSimpleName = logEventClassSimpleName;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableInstanceId() {
        return variableInstanceId;
    }

    public void setVariableInstanceId(String variableInstanceId) {
        this.variableInstanceId = variableInstanceId;
    }

    public String getObjectToString() {
        return objectToString;
    }

    public void setObjectToString(String objectToString) {
        this.objectToString = objectToString;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    public StatefulKnowledgeSession getStatefulKnowledgeSession() {
        return statefulKnowledgeSession;
    }

    public void setStatefulKnowledgeSession(StatefulKnowledgeSession statefulKnowledgeSession) {
        this.statefulKnowledgeSession = statefulKnowledgeSession;
    }
}
