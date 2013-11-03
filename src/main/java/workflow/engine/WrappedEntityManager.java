package workflow.engine;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

public class WrappedEntityManager implements EntityManager {
    private EntityManager delegate;

    public WrappedEntityManager(EntityManager delegate) {
        this.delegate = delegate;
    }

    public void persist(Object entity) {
        delegate.persist(entity);
    }

    public <T> T merge(T entity) {
        return delegate.merge(entity);
    }

    public void remove(Object entity) {
        delegate.remove(entity);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return delegate.find(entityClass, primaryKey);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return delegate.find(entityClass, primaryKey, properties);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return delegate.find(entityClass, primaryKey, lockMode);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return delegate.find(entityClass, primaryKey, lockMode, properties);
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return delegate.getReference(entityClass, primaryKey);
    }

    public void flush() {
        delegate.flush();
    }

    public void setFlushMode(FlushModeType flushMode) {
        delegate.setFlushMode(flushMode);
    }

    public FlushModeType getFlushMode() {
        return delegate.getFlushMode();
    }

    public void lock(Object entity, LockModeType lockMode) {
        delegate.lock(entity, lockMode);
    }

    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.lock(entity, lockMode, properties);
    }

    public void refresh(Object entity) {
        delegate.refresh(entity);
    }

    public void refresh(Object entity, Map<String, Object> properties) {
        delegate.refresh(entity, properties);
    }

    public void refresh(Object entity, LockModeType lockMode) {
        delegate.refresh(entity, lockMode);
    }

    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        delegate.refresh(entity, lockMode, properties);
    }

    public void clear() {
        delegate.clear();
    }

    public void detach(Object entity) {
        delegate.detach(entity);
    }

    public boolean contains(Object entity) {
        return delegate.contains(entity);
    }

    public LockModeType getLockMode(Object entity) {
        return delegate.getLockMode(entity);
    }

    public void setProperty(String propertyName, Object value) {
        delegate.setProperty(propertyName, value);
    }

    public Map<String, Object> getProperties() {
        return delegate.getProperties();
    }

    public Query createQuery(String qlString) {
        return delegate.createQuery(qlString);
    }

    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return delegate.createQuery(criteriaQuery);
    }

    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return delegate.createQuery(qlString, resultClass);
    }

    public Query createNamedQuery(String name) {
        return delegate.createNamedQuery(name);
    }

    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return delegate.createNamedQuery(name, resultClass);
    }

    public Query createNativeQuery(String sqlString) {
        return delegate.createNativeQuery(sqlString);
    }

    public Query createNativeQuery(String sqlString, Class resultClass) {
        return delegate.createNativeQuery(sqlString, resultClass);
    }

    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return delegate.createNativeQuery(sqlString, resultSetMapping);
    }

    public void joinTransaction() {
        delegate.joinTransaction();
    }

    public <T> T unwrap(Class<T> cls) {
        return delegate.unwrap(cls);
    }

    public Object getDelegate() {
        return delegate.getDelegate();
    }

    public void close() {
        delegate.close();
    }

    public boolean isOpen() {
        return delegate.isOpen();
    }

    public EntityTransaction getTransaction() {
        return delegate.getTransaction();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return delegate.getEntityManagerFactory();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return delegate.getCriteriaBuilder();
    }

    public Metamodel getMetamodel() {
        return delegate.getMetamodel();
    }

}
