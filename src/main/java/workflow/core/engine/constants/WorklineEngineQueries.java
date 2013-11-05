package workflow.core.engine.constants;

public class WorklineEngineQueries {
    public static final String GET_BUSINESS_TASK_DEFINITION_BY_TASK_NAME = "SELECT r FROM RepoItem r JOIN r.repoItemAttributes a JOIN a.stringValues s WHERE a.name = "
            + WorklineEngineConstants.BUSINESS_TASK_DEFINITION_NAME + " AND s.value = :taskName";

    public static final String GET_REPO_ITEMS_BY_NAME = "SELECT r FROM RepoItem r WHERE r.name = :repoItemName";
}
