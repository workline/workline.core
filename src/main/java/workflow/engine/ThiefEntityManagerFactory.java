package workflow.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

public class ThiefEntityManagerFactory implements EntityManagerFactory {
    private EntityManagerFactory delegate;

    private List<EntityManager> entityManagers = new ArrayList<EntityManager>();

    public ThiefEntityManagerFactory(EntityManagerFactory delegate) {
        this.delegate = delegate;
    }

    public List<EntityManager> getEntityManagers() {
        return entityManagers;
    }

    @Override
    public EntityManager createEntityManager() {
        EntityManager entityManager = new WrappedEntityManager(delegate.createEntityManager());
        entityManagers.add(entityManager);
        return entityManager;
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        EntityManager entityManager = new WrappedEntityManager(delegate.createEntityManager(map));
        entityManagers.add(entityManager);
        return entityManager;
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return delegate.getMetamodel();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public Cache getCache() {
        return delegate.getCache();
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return delegate.getPersistenceUnitUtil();
    }

}
