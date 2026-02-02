package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabasePasswordSettingsDaoImpl extends BaseDaoImpl<DatabasePasswordSettings> implements  DatabasePasswordSettingsDao {

    public DatabasePasswordSettingsDaoImpl() {
        super(DatabasePasswordSettings.class);
    }

    @Override
    public List<DatabasePasswordSettings> getOrganizationPasswordSettings(Long organizationId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DatabasePasswordSettings> criteria = cb.createQuery(DatabasePasswordSettings.class);

        Root<DatabasePasswordSettings> root = criteria.from(DatabasePasswordSettings.class);
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(root.<String>get("databaseId"), organizationId));

        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<DatabasePasswordSettings> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public DatabasePasswordSettings getOrganizationSpecificSetting(Long organizationId, PasswordSettingsType passwordSettingsType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DatabasePasswordSettings> criteria = cb.createQuery(DatabasePasswordSettings.class);

        Root<DatabasePasswordSettings> root = criteria.from(DatabasePasswordSettings.class);
        Join<DatabasePasswordSettings, PasswordSettings> joinPasswordSettings = root.join("passwordSettings");
        criteria.select(root);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(root.<Long>get("databaseId"), organizationId));
        predicates.add(cb.equal(joinPasswordSettings.<String>get("passwordSettingsType"), passwordSettingsType));

        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<DatabasePasswordSettings> query = entityManager.createQuery(criteria);
        return query.getSingleResult();
    }

    @Override
    public void updateDatabasePasswordSettings(List<DatabasePasswordSettings> databasePasswordSettings) {
        for (DatabasePasswordSettings entity : databasePasswordSettings) {
            this.merge(entity);
        }
    }

}
