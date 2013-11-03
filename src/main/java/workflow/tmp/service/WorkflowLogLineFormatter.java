package workflow.tmp.service;

import javax.interceptor.InvocationContext;

import loggee.api.LogLineFormatter;

public class WorkflowLogLineFormatter implements LogLineFormatter {
    @Override
    public String format(String logMessage, InvocationContext invocationContext) {
        return "WORKFLOW - " + logMessage;
    }
}
