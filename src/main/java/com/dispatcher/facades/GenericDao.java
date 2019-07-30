/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.facades;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import com.dispatcher.config.StatusConfig;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.utilities.Log;
import com.dispatcher.utilities.QueryWrapper;
import com.dispatcher.utilities.UtilClass;
import com.dispatcher.utilities.UtilStatus;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class GenericDao{    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public<T> T find(Class entityClass, Object id) {
        if(entityClass == null)
            return null;
        Table table = (Table) entityClass.getAnnotation(Table.class);
        String tableName = table.name();
        boolean hasStatus = false;
        Class baseClass = entityClass.getSuperclass();
        if(baseClass != null){
            try {
                List<Field> fields = UtilClass.getFields(entityClass);
                if(fields != null){
                    for(Field field : fields){
                        Annotation[] annotations = field.getDeclaredAnnotations();
                        if(annotations.length > 0){
                            for(Annotation annotation : annotations){
                                if(annotation instanceof UtilStatus){
                                    hasStatus = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(getClass(), ex);
            }
            
        }
        
        if(hasStatus){
            Query query = entityManager.createQuery("select A from "+tableName+" A where A.status = '"+StatusConfig.ACTIVE+"' AND A.id = '"+id.toString()+"'");
            query.setMaxResults(1);
            //Query query = entityManager.createNativeQuery("SELECT * FROM "+tableName+" WHERE status == "+StatusConfig.ACTIVE);
            List<T> ts = query.getResultList();
            if(ts.isEmpty())
                return null;
            else
                return ts.get(0);
        }
        return (T) entityManager.find(entityClass, id);
    }
    
    @Transactional
    public <T> List<T> findAll(Class entityClass) {
        if(entityClass == null)
            return null;
        javax.persistence.criteria.CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return entityManager.createQuery(cq).getResultList();
    }
    
    @Transactional
    public <T> T save(T entity) throws ErrorGeneralException, Exception {
        
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            javax.validation.Validator validator = factory.getValidator();
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
            if(constraintViolations.size() > 0){
                String err = "Error: ";
                Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
                while(iterator.hasNext()){
                    ConstraintViolation<T> cv = iterator.next();
                    err = cv.getRootBeanClass().getName()+"."+cv.getPropertyPath() + " " +cv.getMessage();
                    Log.e(entity.getClass(), new RuntimeException(cv.getRootBeanClass().getName()+"."+cv.getPropertyPath() + " " +cv.getMessage()));
                }
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[0], CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[1], err))));
            }else{
                entityManager.persist(entity);
                entityManager.flush();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            entityManager.clear();
        }
        
        return entity;
    }
    
    @Transactional
    public <T> List<T> save(List<T> entities) throws Exception {
        
        List<T> list = new ArrayList();
        
        for (T t : entities) {
            list.add(save(t));
        }
        
        return list;
    }
    
    @Transactional
    public <T> T update(T entity) throws Exception {
        
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            javax.validation.Validator validator = factory.getValidator();
            Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
            if(constraintViolations.size() > 0){
                String err = "Error";
                Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
                while(iterator.hasNext()){
                    ConstraintViolation<T> cv = iterator.next();
                    err = cv.getRootBeanClass().getName()+"."+cv.getPropertyPath() + " " +cv.getMessage();
                    Log.e(entity.getClass(), new RuntimeException(cv.getRootBeanClass().getName()+"."+cv.getPropertyPath() + " " +cv.getMessage()));
                }
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[0], CommonErrorCodeConfig.GENERAL_DATABASE_ERROR[1], err))));
            }else{
                entityManager.merge(entity);
                entityManager.flush();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            entityManager.clear();
        }
        
        return entity;
    }
    
    @Transactional
    public <T> List<T> update(List<T> entities) throws Exception {
        
        List<T> list = new ArrayList();
        
        for (T t : entities) {
            list.add(update(t));
        }
        
        return list;
    }
    
    @Transactional
    public <T> T delete(T entity) throws Exception {
        
        try {
            //entityManager.remove(entityManager.merge(entity));
            entityManager.remove(entityManager.getReference(entity.getClass(), ((BaseEntity) entity).getId()));
            entityManager.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            entityManager.clear();
        }
        
        return entity;
    }
    
    @Transactional
    public <T> List<T> delete(List<T> ts) throws Exception {
        
        List<T> list = new ArrayList();
        
        for (T t : ts) {
            list.add(delete(t));
        }
        
        return list;
    }
    
    @Transactional
    public <T> T findById(Object id, Class c) {
        
        T entity = (T) entityManager.find(c, id);
        entityManager.detach(entity);
        return entity;
    }
    
    @Transactional
    public <T> List<T> findWithNamedQuery(QueryWrapper qw) throws Exception {
        
        Query query = entityManager.createQuery(qw.getQuery());
        
        if (qw.getParameters() != null && !qw.getParameters().isEmpty()) {
            for(Map.Entry entry : qw.getParameters().entrySet()){
                query.setParameter((String) entry.getKey(), entry.getValue());
            }
        }
        
        if(qw.getPageNumber() != null && qw.getPageSize() != null){
            query.setFirstResult(qw.getPageNumber() * qw.getPageSize());
            query.setMaxResults(qw.getPageSize());
        }
        if(qw.getPageSize() != null){
            query.setMaxResults(qw.getPageSize());
        }
        
        List<T> result = query.getResultList();
        
        entityManager.flush();
        entityManager.clear();
        
        return result;
    }
    
    @Transactional
    public <T> T findSingleWithNamedQuery(QueryWrapper qw) throws Exception {
        
        Query query = entityManager.createNamedQuery(qw.getQuery());
        
        if (qw.getParameters() != null && !qw.getParameters().isEmpty()) {
            
            for(Map.Entry entry : qw.getParameters().entrySet()){
                query.setParameter((String) entry.getKey(), entry.getValue());
            }
        }
        
        Object result = query.getSingleResult();
        
        entityManager.flush();
        entityManager.clear();
        
        return (T) result;
    }
    
    @Transactional
    public <T> List<T> findWithQuery(QueryWrapper qw) throws Exception {
        
        return findWithQuery(qw, 0);
    }
    
    @Transactional
    public <T> List<T> findWithQuery(QueryWrapper qw, int resultLimit) throws Exception {
        
        Query query = entityManager.createQuery(qw.getQuery());
        
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        
        if (qw.getParameters() != null && !qw.getParameters().isEmpty()) {
            for(Map.Entry entry : qw.getParameters().entrySet()){
                query.setParameter((String) entry.getKey(), entry.getValue());
            }
        }
        
        List<T> result = query.getResultList();
        
        entityManager.flush();
        entityManager.clear();
        
        return result;
    }
    
    @Transactional
    public <T> T findSingleWithQuery(QueryWrapper qw) throws Exception {
        
        Query query = entityManager.createQuery(qw.getQuery());
        
        if (qw.getParameters() != null && !qw.getParameters().isEmpty()) {
            for(Map.Entry entry : qw.getParameters().entrySet()){
                query.setParameter((String) entry.getKey(), entry.getValue());
            }
        }
        
        T result = (T) query.getSingleResult();
        
        entityManager.flush();
        entityManager.clear();
        
        return result;
    }
    
    @Transactional
    public <T> List<T> findWithNativeQuery(String nativeQuery) throws Exception {
        return findWithNativeQuery(nativeQuery, 0);
    }
    
    @Transactional
    public <T> List<T> findWithNativeQuery(String nativeQuery, int resultLimit) throws Exception {
        
        Query query = entityManager.createNativeQuery(nativeQuery);
        
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        
        List<T> result = query.getResultList();
        
        entityManager.flush();
        entityManager.clear();
        
        return result;
    }
    
    @Transactional
    public <T> T findSingleWithNativeQuery(String nativeQuery) throws Exception {
        
        Query query = entityManager.createNativeQuery(nativeQuery);
        
        T result = (T) query.getSingleResult();
        
        entityManager.flush();
        entityManager.clear();
        
        return result;
    }
}
