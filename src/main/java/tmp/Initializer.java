package tmp;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.AttributeValueHandler;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import workline.core.api.internal.IRepoHandler;
import workline.core.api.internal.IRepoManager;
import workline.core.domain.EInheritenceType;
import workline.core.engine.constants.WorklineEngineConstants;

@Startup
@Singleton
public class Initializer {
    private static final String REPO_ITEM_TYPE_ATTRIBUTE_NAME = "type";

    @Inject
    private Logger logger;

    @Inject
    private IRepoManager repoManager;
    @Inject
    private IRepoHandler repoHandler;

    // TODO RepoItems should have an attribute 'types' that tags it as a certain type of RepoItem. E.g. 'accessRight'
    @PostConstruct
    public void init() {
        RepoItem johnSmith = createRepoItem(
                new RepoItemAttributeData<String>(REPO_ITEM_TYPE_ATTRIBUTE_NAME, StringAttributeValueHandler.getInstance(), "Person"),
                new RepoItemAttributeData<String>("firstName", StringAttributeValueHandler.getInstance(), "John"),
                new RepoItemAttributeData<String>("lastName", StringAttributeValueHandler.getInstance(), "Smith"));

        logger.info("John Smith id : '" + johnSmith.getId() + "'");

        RepoItem testWorklineProcessRepoItem = new RepoItem();
        repoHandler.addAttribute(testWorklineProcessRepoItem, "name", EAttributeType.STRING, "testWorklineProcess");
        repoHandler.persistRepoItem(testWorklineProcessRepoItem);

        String nameAttributeName4AccessRight = "name";
        String accountAttributeName4AccessRight = "account";
        String additionalIOAttributeName4AccessRight = "(Additional input for Access right request Define request)";
        String inputBehaviourSourceAttributeName4AccessRight = "Input behaviour for (Access right request).(Define request)";
        String approvalPointAttributeName4AccessRight = "approvalPoint";

        String accessRightType = "accessRight";

        String erpName = "erp";
        String erpAdditionalIO = "issuedAccessRight;REPO::IssuedAccessRight;PROCESS;NEW;TODO\n"
                + "account;REPO::AccessRight;PROCESS;TODO;issuedAccessRight.account\n"
                + "recipient;REPO::Person;PROCESS;TODO;\n"
                + "costCenter;INTEGER;PROCESS;TODO;issuedAccessRight.costCenter\n"
                + "issuedAccessRightType;REPO::AccessRight;PROCESS;TODO;issuedAccessRight.type\n";
        String erpApprovalPoint1 = "Recipient.Manager";
        String erpApprovalPoint2 = "AccessRight.Owner";

        String userAccountName = "userAccount";

        String clerkName = "clerk";

        RepoItem erp = createRepoItem(
                new RepoItemAttributeData<String>(REPO_ITEM_TYPE_ATTRIBUTE_NAME, StringAttributeValueHandler.getInstance(), accessRightType),
                new RepoItemAttributeData<String>(nameAttributeName4AccessRight, StringAttributeValueHandler.getInstance(), erpName),
                new RepoItemAttributeData<String>(additionalIOAttributeName4AccessRight, StringAttributeValueHandler.getInstance()),
                new RepoItemAttributeData<RepoItem>(accountAttributeName4AccessRight, RepoItemAttributeValueHandler.getInstance()),
                new RepoItemAttributeData<String>(approvalPointAttributeName4AccessRight, StringAttributeValueHandler.getInstance()));

        RepoItem userAccount = createRepoItem(
                new RepoItemAttributeData<String>(REPO_ITEM_TYPE_ATTRIBUTE_NAME, StringAttributeValueHandler.getInstance(), accessRightType),
                new RepoItemAttributeData<String>(nameAttributeName4AccessRight, StringAttributeValueHandler.getInstance(), userAccountName),
                new RepoItemAttributeData<String>(additionalIOAttributeName4AccessRight, StringAttributeValueHandler.getInstance(), EInheritenceType.CUMULATE,
                        erp),
                new RepoItemAttributeData<RepoItem>(accountAttributeName4AccessRight, RepoItemAttributeValueHandler.getInstance(), EInheritenceType.INHERIT,
                        erp),
                new RepoItemAttributeData<String>(approvalPointAttributeName4AccessRight, StringAttributeValueHandler.getInstance(), EInheritenceType.CUMULATE,
                        erp));

        RepoItem clerk = createRepoItem(
                new RepoItemAttributeData<String>(REPO_ITEM_TYPE_ATTRIBUTE_NAME, StringAttributeValueHandler.getInstance(), accessRightType),
                new RepoItemAttributeData<String>(nameAttributeName4AccessRight, StringAttributeValueHandler.getInstance(), clerkName),
                new RepoItemAttributeData<String>(additionalIOAttributeName4AccessRight, StringAttributeValueHandler.getInstance(), EInheritenceType.CUMULATE,
                        erp),
                new RepoItemAttributeData<RepoItem>(accountAttributeName4AccessRight, RepoItemAttributeValueHandler.getInstance(), EInheritenceType.INHERIT,
                        erp),
                new RepoItemAttributeData<String>(approvalPointAttributeName4AccessRight, StringAttributeValueHandler.getInstance(), EInheritenceType.CUMULATE,
                        erp));

        repoManager.setValue(erp, additionalIOAttributeName4AccessRight, erpAdditionalIO);
        repoManager.setValue(erp, accountAttributeName4AccessRight, userAccount);
        repoManager.setValue(erp, approvalPointAttributeName4AccessRight, erpApprovalPoint1);
        // --

        String definitionName4DefineRequest = "defineRequest";
        String ioVariableSource4DefineRequest = "accessRight." + additionalIOAttributeName4AccessRight;
        String taskSpecificProcessVariablesDefinition4DefineRequest = "accessRight;REPO::AccessRight;PROCESS;TODO";
        String inputBehaviourSource4DefineRequest = "accessRight." + inputBehaviourSourceAttributeName4AccessRight;
        createTaskDefinition(definitionName4DefineRequest, ioVariableSource4DefineRequest, taskSpecificProcessVariablesDefinition4DefineRequest,
                inputBehaviourSource4DefineRequest);

        String definitionName4Approve = "approve";
        String ioVariableSource4Approve = "";
        String taskSpecificProcessVariablesDefinition4Approve = "";
        String inputBehaviourSourceApprove = "";
        createTaskDefinition(definitionName4Approve, ioVariableSource4Approve, taskSpecificProcessVariablesDefinition4Approve, inputBehaviourSourceApprove);

        logger.info("ERP id : '" + erp.getId() + "'");
    }

    private RepoItem createRepoItem(RepoItemAttributeData ... attributeDataCollection) {
        RepoItem repoItem = new RepoItem();

        repoHandler.persistRepoItem(repoItem);

        for (RepoItemAttributeData repoItemAttributeData : attributeDataCollection) {
            RepoItemAttribute attribute = repoHandler.addAttribute(repoItem, repoItemAttributeData.name,
                    repoItemAttributeData.attributeValueHandler.getEAttributeType());

            if (repoItemAttributeData.inheritenceType != null) {
                repoManager.addInheritenceType(attribute, repoItemAttributeData.inheritenceType);
            } else {
                repoManager.addInheritenceType(attribute, EInheritenceType.OVERRIDE);
            }
            if (repoItemAttributeData.inheritenceSource != null) {
                repoManager.addInheritenceSource(attribute, repoItemAttributeData.inheritenceSource);
            }
            if (repoItemAttributeData.value != null) {
                repoManager.setValue(repoItem, repoItemAttributeData.name, repoItemAttributeData.value);
            }
        }

        return repoItem;
    }

    private void createTaskDefinition(String taskName, String ioVariableSource, String taskSpecificProcessVariablesDefinition, String inputBehaviourSource) {
        RepoItem task = new RepoItem();

        repoHandler.addAttribute(task, WorklineEngineConstants.BUSINESS_TASK_DEFINITION_NAME, EAttributeType.STRING, taskName);
        repoHandler.addAttribute(task, WorklineEngineConstants.IO_VARIABLE_SOURCE, EAttributeType.STRING, ioVariableSource);
        repoHandler.addAttribute(task, WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION, EAttributeType.STRING,
                taskSpecificProcessVariablesDefinition);
        repoHandler.addAttribute(task, WorklineEngineConstants.INPUT_BEHAVIOUR_SOURCE, EAttributeType.STRING, inputBehaviourSource);

        repoHandler.persistRepoItem(task);
    }

    private class RepoItemAttributeData<T> {
        String name;
        AttributeValueHandler<T, ?> attributeValueHandler;
        T value;
        EInheritenceType inheritenceType;
        RepoItem inheritenceSource;

        public RepoItemAttributeData(String name, AttributeValueHandler<T, ?> attributeValueHandler) {
            this(name, attributeValueHandler, null, null, null);
        }

        public RepoItemAttributeData(String name, AttributeValueHandler<T, ?> attributeValueHandler, T value) {
            this(name, attributeValueHandler, null, null, value);
        }

        public RepoItemAttributeData(String name, AttributeValueHandler<T, ?> attributeValueHandler, EInheritenceType inheritenceType,
                RepoItem inheritenceSource) {
            this(name, attributeValueHandler, inheritenceType, inheritenceSource, null);
        }

        public RepoItemAttributeData(String name, AttributeValueHandler<T, ?> attributeValueHandler, EInheritenceType inheritenceType,
                RepoItem inheritenceSource,
                T value) {
            this.name = name;
            this.attributeValueHandler = attributeValueHandler;
            this.inheritenceType = inheritenceType;
            this.inheritenceSource = inheritenceSource;
            this.value = value;
        }
    }
}
