package workflow.tmp.service;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workflow.repo.service.RepoService;
import workflow.util.Primary;

@Stateless
@Logged
public class PersonService {
    private static final String PROCESS_DATA_REPO_NAME = "ProcessData";
    private static final String FIRST_NAME_ATTRIBUTE_NAME = "First name";
    private static final String MOTHER_ATTRIBUTE_NAME = "Mother";

    @Inject
    @Primary
    private EntityManager entityManager;
    @Inject
    private RepoService repoService;

    // TODO Temporary method, too specific
    public Long createRandomPerson(String name) {

        RepoItem repo = new RepoItem();
        repo.setRepoName(PROCESS_DATA_REPO_NAME);

        RepoItemAttribute firstNameRepoAttribute = new RepoItemAttribute();
        firstNameRepoAttribute.setNameAndType(FIRST_NAME_ATTRIBUTE_NAME, EAttributeType.STRING);
        firstNameRepoAttribute.setValue(name);

        RepoItemAttribute motherRepoAttribute = new RepoItemAttribute();
        motherRepoAttribute.setNameAndType(MOTHER_ATTRIBUTE_NAME, EAttributeType.REPO_ITEM);
        RepoItem mom = null;
        try {
            mom = repoService.getRepo(2L);
            mom = repoService.getRepo(1L);
        } catch (Exception e) {
        }
        motherRepoAttribute.setValue(mom);

        Set<RepoItemAttribute> repoAttributes = new HashSet<RepoItemAttribute>();

        repoAttributes.add(firstNameRepoAttribute);
        repoAttributes.add(motherRepoAttribute);

        repo.setRepoItemAttributes(repoAttributes);

        entityManager.persist(repo);

        return repo.getId();
    }
}
