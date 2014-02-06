package workline.core.domain;

import java.util.Collection;

import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IRepoHandler;
import workline.core.repo.manager.IRepoManager;

public enum EInheritenceType {
    INHERIT {
        @TODO(tags = { TODOTag.SPECIFICATION_REQUIRED })
        @Override
        protected void setOwnValue(IRepoHandler repoHandler, RepoItem repoItem, RepoItem benefactor, String attributeName, Object value) {
            // Do nothing.
        }
    },
    OVERRIDE {
        @TODO(tags = { TODOTag.SPECIFICATION_REQUIRED })
        @Override
        protected void setInheritingValue(IRepoManager repoManager, IRepoHandler repoHandler, RepoItem repoItem, RepoItem benefactor, String attributeName,
                Object value) {
            // Do nothing;
        }
    },
    CUMULATE;

    public void setValue(IRepoManager repoManager, IRepoHandler repoHandler, RepoItem repoItem, String attributeName, Object value) {
        setValue(repoManager, repoHandler, repoItem, null, attributeName, value);
    }

    protected void setValue(IRepoManager repoManager, IRepoHandler repoHandler, RepoItem repoItem, RepoItem benefactor, String attributeName, Object value) {
        setOwnValue(repoHandler, repoItem, benefactor, attributeName, value);
        setInheritorValues(repoManager, repoHandler, repoItem, attributeName, value);
    }

    protected void setOwnValue(IRepoHandler repoHandler, RepoItem repoItem, RepoItem benefactor, String attributeName, Object value) {
        repoHandler.setValue(repoItem, benefactor, attributeName, value);
    }

    protected void setInheritorValues(IRepoManager repoManager, IRepoHandler repoHandler, RepoItem repoItem, String attributeName, Object value) {
        Collection<RepoItemAttribute> inheritors = repoHandler.getInheritors(repoItem, attributeName);
        for (RepoItemAttribute attribute : inheritors) {
            EInheritenceType inheritorInheritenceType = getInheritorInheritenceType(repoManager, attribute);
            inheritorInheritenceType.setInheritingValue(repoManager, repoHandler, attribute.getRepoItem(), repoItem, attributeName, value);
        }
    }

    protected void setInheritingValue(IRepoManager repoManager, IRepoHandler repoHandler, RepoItem repoItem, RepoItem benefactor, String attributeName,
            Object value) {

        setValue(repoManager, repoHandler, repoItem, benefactor, attributeName, value);
    }

    protected EInheritenceType getInheritorInheritenceType(IRepoManager repoManager, RepoItemAttribute inheritor) {
        EInheritenceType inheritorInheritenceType = repoManager.getInheritenceType(inheritor);

        return inheritorInheritenceType;
    }
}
