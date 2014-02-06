package workline.core.api.internal;

import java.util.Collection;
import java.util.Set;

import vrds.model.EAttributeType;
import vrds.model.IValueWrapper;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.AttributeValueHandler;

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

    <T, W extends IValueWrapper<T>> T getNonInheritingValue(RepoItem repoItem, String attributeName, AttributeValueHandler<T, W> attributeValueHandler);

    <T, W extends IValueWrapper<T>> Set<T> getInheritingValues(RepoItem repoItem, String attributeName, AttributeValueHandler<T, W> attributeValueHandler);

    void setValue(RepoItem repoItem, String attributeName, Object value);

    void setValue(RepoItem repoItem, RepoItem benefactor, String attributeName, Object value);

    <T> void setSimpleValues(RepoItem repoItem, String attributeName, AttributeValueHandler<T, ?> attributeValueHandler, T ... values);

    Collection<RepoItemAttribute> getInheritors(RepoItem benefactor, String attributeName);

}
