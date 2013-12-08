package workline.core.repo.manager;

import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workline.core.domain.EInheritenceType;
import workline.core.domain.ERepoItemState;

public interface IRepoManager {

    void setValue(RepoItem repoItem, String attributeName, Object value);

    ERepoItemState getState(RepoItem repoItem);

    EInheritenceType getInheritenceType(RepoItemAttribute attribute);

    void setState(RepoItem repoItem, ERepoItemState state);

    boolean isActive(RepoItem repoItem);

    void checkConsistency(RepoItem repoItem);

}
