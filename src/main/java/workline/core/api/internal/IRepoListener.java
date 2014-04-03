package workline.core.api.internal;

import vrds.model.RepoItem;

public interface IRepoListener {
    void execute(RepoItem repoItem);
}
