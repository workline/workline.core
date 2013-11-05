package workflow.core.repo;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;

public interface IRepoHandler {
    RepoItem getRepoItem(Long repoItemId);

    void persistRepoItem(RepoItem repoItem);

    void persistRepoItemAttribute(RepoItemAttribute repoItemAttribute);

    void addAttribute(RepoItem repoItem, String attributeName, EAttributeType type);

    void addAttribute(RepoItem repoItem, String attributeName, EAttributeType type, Object value);

}
