package workline.core.repo.manager;

import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workline.core.domain.EInheritenceType;
import workline.core.domain.ERepoItemState;

public interface IRepoManager {

    void setValue(RepoItem repoItem, String attributeName, Object value);

    RepoItemAttribute addAttribute(RepoItem repoItem, String attributeName, EAttributeType type, EInheritenceType inheritenceType, RepoItem benefactor,
            Object value);

    MetaAttribute addInheritenceType(RepoItemAttribute ownerAttribute, EInheritenceType inheritenceType);

    MetaAttribute addBenefactor(RepoItemAttribute ownerAttribute, RepoItem benefactor);

    ERepoItemState getState(RepoItem repoItem);

    EInheritenceType getInheritenceType(RepoItemAttribute attribute);

    void setState(RepoItem repoItem, ERepoItemState state);

    boolean isActive(RepoItem repoItem);

    void checkConsistency(RepoItem repoItem);

}
