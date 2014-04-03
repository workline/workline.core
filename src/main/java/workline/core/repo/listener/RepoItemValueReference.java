package workline.core.repo.listener;

import vrds.model.RepoItem;

public class RepoItemValueReference {
    private RepoItem repoItem;
    private String attributeName;

    public RepoItemValueReference(RepoItem repoItem, String attributeName) {
        this.repoItem = repoItem;
        this.attributeName = attributeName;
    }

    public RepoItem getRepoItem() {
        return repoItem;
    }

    public void setRepoItem(RepoItem repoItem) {
        this.repoItem = repoItem;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String toString() {
        return "RepoItemValueReference [repoItem=" + repoItem + ", attributeName=" + attributeName + "]";
    }

}
