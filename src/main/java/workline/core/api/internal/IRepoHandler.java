package workline.core.api.internal;

import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;

public interface IRepoHandler {
    RepoItem getRepoItem(Long repoItemId);

    void persistRepoItem(RepoItem repoItem);

    void persistRepoItemAttribute(RepoItemAttribute repoItemAttribute);

    RepoItemAttribute addAttribute(RepoItem repoItem, String attributeName, EAttributeType type);

    RepoItemAttribute addAttribute(RepoItem repoItem, String attributeName, EAttributeType type, Object value);

    MetaAttribute createMetaAttribute(RepoItemAttribute ownerAttribute, String name, EAttributeType type, Object value);

    MetaAttribute createMetaAttribute(String name, EAttributeType type, Object value);

    MetaAttribute createMetaAttribute(RepoItemAttribute ownerAttribute, String name, EAttributeType type);

    MetaAttribute createMetaAttribute(String name, EAttributeType type);
}
