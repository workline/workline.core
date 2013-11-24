package workflow.core.engine.constants;

public class WorklineEngineQueries {
    public static final String NQ_FIND_BUSINESS_TASK_DEFINITION_BY_TASK_NAME = "SELECT r FROM RepoItem r JOIN r.repoItemAttributes a JOIN a.stringValues s WHERE a.name = "
            + WorklineEngineConstants.BUSINESS_TASK_DEFINITION_NAME + " AND s.value = :" + WorklineEngineQueries.NQ_PARAMETER_TASK_NAME;
    public static final String NQ_PARAMETER_TASK_NAME = "taskName";

    public static final String NQ_FIND_REPO_ITEMS_BY_NAME = "SELECT r FROM RepoItem r WHERE r.name = :" + WorklineEngineQueries.NQ_PARAMETER_REPO_ITEM_NAME;
    public static final String NQ_PARAMETER_REPO_ITEM_NAME = "repoItemName";
}
