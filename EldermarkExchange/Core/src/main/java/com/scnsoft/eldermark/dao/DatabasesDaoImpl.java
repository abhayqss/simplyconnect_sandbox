package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Repository
public class DatabasesDaoImpl implements DatabasesDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Database> getDatabases() {
        TypedQuery<Database> query = entityManager.createQuery(
                "select d from Database d order by d.name asc", Database.class);
        return query.getResultList();
    }

    @Override
    public Database getDatabaseById(Long id) {
        return entityManager.find(Database.class, id);
    }

    @Override
    public List<Database> getDatabasesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return Collections.EMPTY_LIST;

        TypedQuery<Database> query = entityManager.createQuery(
                "select d from Database d where d.id in :ids", Database.class)
                .setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    public Database getDatabaseByCompanyId(String loginCompanyId) {
        TypedQuery<Database> query = entityManager.createQuery(
                "select d from Database d where lower(d.systemSetup.loginCompanyId)=:loginCompanyId", Database.class);
        query.setParameter("loginCompanyId", loginCompanyId.toLowerCase());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
             throw new MultipleEntitiesFoundException(
                     "More than one database with given company id was found.");
        }
    }

    @Override
    public Database getDatabaseByAlternativeId(String databaseAlternativeId) {
        TypedQuery<Database> query = entityManager.createQuery(
                "select d from Database d where d.alternativeId=:databaseAlternativeId", Database.class);
        query.setParameter("databaseAlternativeId", databaseAlternativeId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException(
                    "More than one database with given alternative id exist: " + databaseAlternativeId, e);
        }
    }

    @Override
    public List<Database> getDatabasesByEmployeeLogin(String employeeLogin){
        if (employeeLogin == null) {
            return null;
        }
        TypedQuery<Database> query = entityManager.createQuery(
                "select db from Employee e inner join e.database db where e.loginName = :loginName and e.status=0",
                Database.class);
        query.setParameter("loginName", employeeLogin);
        return query.getResultList ();
    }

    @Override
    public Database getDatabaseByOid(String oid) {
        TypedQuery<Database> query = entityManager.createQuery(
                "select d from Database d where d.oid=:oid", Database.class);
        query.setParameter("oid", oid);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException(
                    "More than one database with given oid exist: " + oid, e);
        }
    }

    @Override
    public void update(Database database) {
        entityManager.merge(database);
    }

    @Override
    public Pair<String, String> getDatabaseLogos(long id) {
        Query q = entityManager.createQuery("Select o.mainLogoPath, o.additionalLogoPath from Database o where o.id=:id");
        q.setParameter("id", id);
        Object[] result = (Object[]) q.getSingleResult();
        return new Pair<>((String)result[0], (String)result[1]);
    }
}
