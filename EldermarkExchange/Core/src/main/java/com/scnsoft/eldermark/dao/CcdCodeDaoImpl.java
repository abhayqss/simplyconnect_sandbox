package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.shared.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class CcdCodeDaoImpl implements CcdCodeDao {
    private static final Logger logger = LoggerFactory.getLogger(CcdCodeDaoImpl.class);

    public static final String GENDER_VALUE_SET_NAME = "AdministrativeGender";
    public static final String MARITAL_STATUS_VALUE_SET_NAME = "MaritalStatus";
    public static final String VITAL_SIGN_VALUE_SET_NAME = "VitalSigns";
    public static final String RACE_VALUE_SET_NAME = "Race";
    public static final String RELIGION_VALUE_SET_NAME = "ReligiousAffiliation";

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public CcdCode getGenderCcdCode(Gender gender) {
        try {
            TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSetAndCode", CcdCode.class);
            query.setParameter("code", gender.getAdministrativeGenderCode());
            query.setParameter("valueSet", GENDER_VALUE_SET_NAME);

            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Can't find Gender for code {}", gender);
            throw e;
        }
    }

    @Override
    public CcdCode getMaritalStatus(String maritalStatus) {
        try {
            final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSetAndDisplayName", CcdCode.class);
            query.setParameter("displayName", maritalStatus);
            query.setParameter("valueSet", MARITAL_STATUS_VALUE_SET_NAME);

            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Can't find MaritalStatus for displayName {}", maritalStatus);
            throw e;
        }
    }

    @Override
    public List<CcdCode> getVitalSignTypes() {
        final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSet", CcdCode.class);
        query.setParameter("valueSet", VITAL_SIGN_VALUE_SET_NAME);
        return query.getResultList();
    }

    @Override
    public CcdCode getCcdCode(String code, String codeSystem) {
        final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByCodeAndCodeSystem", CcdCode.class);
        query.setParameter("code", code);
        query.setParameter("codeSystem", codeSystem);
        List<CcdCode> results = query.getResultList();
        if (results.size() > 1) {
            throw new NonUniqueResultException();
        }
        if (results.size() == 0) {
            logger.error("Can't find CcdCode for code={}, codeSystem={}", code, codeSystem);
            return null;
        }
        return results.get(0);
    }

    @Override
    public CcdCode getCcdCode(String code, String codeSystem, String valueSet) {
        final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByCodeAndCodeSystemAndValueSet", CcdCode.class);
        query.setParameter("code", code);
        query.setParameter("codeSystem", codeSystem);
        query.setParameter("valueSet", valueSet);
        List<CcdCode> results = query.getResultList();
        if (results.size() > 1) {
            throw new NonUniqueResultException();
        }
        if (results.size() == 0) {
            logger.error("Can't find CcdCode for code={}, codeSystem={}, valueSet={}", code, codeSystem, valueSet);
            return null;
        }
        return results.get(0);
    }

    @Override
    public CcdCode find(Long id) {
        return entityManager.find(CcdCode.class, id);
    }

    @Override
    public CcdCode getReference(Long id) {
        return entityManager.getReference(CcdCode.class, id);
    }

    @Override
    public List<CcdCode> listCcdCodesByValueSetAndCodeSystem(String valueSetCode, String codeSystem) {
        final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSetCodeAndCodeSystem", CcdCode.class);
        query.setParameter("valueSet", valueSetCode);
        query.setParameter("codeSystem", codeSystem);

        return query.getResultList();
    }

    @Override
    public List<CcdCode> listWithSameDisplayName(Long codeId, Collection<String> codeSystems) {
        final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findWithSameDisplayName", CcdCode.class);
        query.setParameter("id", codeId);
        query.setParameter("codeSystems", codeSystems);
        return query.getResultList();
    }

    @Override
    public List<CcdCode> listByCodeOrDisplayName(String searchString, Collection<String> codeSystems, int offset, int limit) {
        final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByCodeOrDisplayName", CcdCode.class);
        query.setParameter("search", searchString + "%");
        query.setParameter("codeSystems", codeSystems);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Long countByCodeOrDisplayName(String searchString, Collection<String> codeSystems) {
        final TypedQuery<Long> query = entityManager.createNamedQuery("ccdCode.countByCodeOrDisplayName", Long.class);
        query.setParameter("search", searchString + "%");
        query.setParameter("codeSystems", codeSystems);
        return query.getSingleResult();
    }

    @Override
    public List<CcdCode> getGenders() {
        final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSet", CcdCode.class);
        query.setParameter("valueSet", GENDER_VALUE_SET_NAME);
        return query.getResultList();
    }

    @Override
    public CcdCode getRaceCcdCode(String raceString) {
        try {
            final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSetAndDisplayName", CcdCode.class);
            query.setParameter("displayName", raceString);
            query.setParameter("valueSet", RACE_VALUE_SET_NAME);

            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Can't find RaceCcdCode for displayName {}", raceString);
            throw e;
        }
    }

    @Override
    public CcdCode getReligionCcdCode(String religionString) {
        try {
            final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSetAndDisplayName", CcdCode.class);
            query.setParameter("displayName", religionString);
            query.setParameter("valueSet", RELIGION_VALUE_SET_NAME);

            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Can't find ReligionCcdCode for displayName {}", religionString);
            throw e;
        }
    }

    @Override
    public CcdCode getEthnicGroup(String ethnicity) {
        try {
            final TypedQuery<CcdCode> query = entityManager.createNamedQuery("ccdCode.findByValueSetAndDisplayName", CcdCode.class);
            query.setParameter("displayName", ethnicity);
            query.setParameter("valueSet", RACE_VALUE_SET_NAME);

            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.error("Can't find EthnicGroupCcdCode for displayName {}", ethnicity);
            throw e;
        }
    }

}
