/**
 * Copyright (c) 2009-2010 Misys Open Source Solutions (MOSS) and others
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * <p>
 * Contributors:
 * Misys Open Source Solutions - initial API and implementation
 * -
 */

package org.openhealthtools.openxds.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openhealthtools.openxds.entity.Organization;
import org.openhealthtools.openxds.entity.PersonIdentifier;
import org.openhealthtools.openxds.entity.PersonIdentifierMerged;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Collections;
import java.util.List;

/*
 * @author <a href="mailto:Rasakannu.Palaniyandi@misys.com">Raja</a>
 */

public class XdsRegistryPatientDaoImpl extends HibernateDaoSupport implements XdsRegistryPatientDao {
    private static final Log log = LogFactory.getLog(XdsRegistryPatientDaoImpl.class);

    private OrganizationDao organizationDao;

    public PersonIdentifier getPersonById(PersonIdentifier patientId) throws RegistryPatientException {
        List list;

        //uid and uid type will be mandatory
        //when changing default XDS community - MPI af should also be rewritten for default (postgres registry should also be modified)?
        //1. if it is a full match (aa and af(uid and type)) - return it
        //2. if aa and af are present and no match found - check if af is matching default community
        //3. if no af provided - find with null af

        try {
            list = findExactPersonIdentifier(patientId);
            if (list.size() == 0) {
                list = findPersonIdentifierFromDefaultCommunity(patientId);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve person identifier from registry patient service", e);
            throw new RegistryPatientException(e);
        }

        return list.size() > 0 ? (PersonIdentifier) list.get(0) : null;
    }

    @Override
    public List<PersonIdentifier> getPersonsByPatientIdAndAssigningAuthority(String patientId, String assigningAuthority) {
        final Criteria criteria = buildPersonIdentifierCommonCriteria(patientId, assigningAuthority, "N");
        return criteria.list();
    }

    private List<PersonIdentifier> findExactPersonIdentifier(PersonIdentifier personIdentifier) {
        Criteria criteria = buildPersonIdentifierExactCriteria(
                personIdentifier.getPatientId(),
                personIdentifier.getAssigningAuthority(),
                "N",
                personIdentifier.getAssigningFacilityUniversal()
        );
        return criteria.list();
    }

    private List findPersonIdentifierFromDefaultCommunity(PersonIdentifier personIdentifier) {
        if (personIdentifier.getAssigningAuthorityUniversal() == null) {
            return Collections.emptyList();
        }

        final Organization defaultFacility = organizationDao.findDefaultByDatabaseOid(personIdentifier.getAssigningAuthorityUniversal());
        if (defaultFacility == null) {
            return Collections.emptyList();
        }

        if (personIdentifier.getAssigningFacilityUniversal() != null && !personIdentifier.getAssigningFacilityUniversal().equals(defaultFacility.getOid())) {
            return Collections.emptyList();
        }

        final Criteria criteria = buildPersonIdentifierDefaultCommunityCriteria(
                personIdentifier.getPatientId(),
                personIdentifier.getAssigningAuthority(),
                "N",
                defaultFacility.getOid()
        );
        return criteria.list();
    }

    private Criteria buildPersonIdentifierDefaultCommunityCriteria(String patientId, String assigningAuthority, String deletePatient, String defaultFacilityOid) {
        return buildPersonIdentifierCommonCriteria(patientId, assigningAuthority, deletePatient)
                .add(Restrictions.or(
                        Restrictions.isNull("assigningFacilityUniversal"),
                        Restrictions.eq("assigningFacilityUniversal", defaultFacilityOid)
                        )
                );
    }

    private Criteria buildPersonIdentifierExactCriteria(String patientId, String assigningAuthority, String deletePatient, String assigningFacilityUniversal) {
        return buildPersonIdentifierCommonCriteria(patientId, assigningAuthority, deletePatient)
                .add(assigningFacilityUniversal == null ? Restrictions.isNull("assigningFacilityUniversal") :
                        Restrictions.eq("assigningFacilityUniversal", assigningFacilityUniversal));
    }

    private Criteria buildPersonIdentifierCommonCriteria(String patientId, String assigningAuthority, String deletePatient) {
        return getSession().createCriteria(PersonIdentifier.class)
                .add(Restrictions.eq("patientId", patientId))
                .add(Restrictions.like("assigningAuthority", "%" + assigningAuthority + "%"))
                .add(Restrictions.eq("deleted", deletePatient));
    }

    public PersonIdentifier getPersonByResidentId(Long residentId) throws RegistryPatientException {
        List list;
        PersonIdentifier personIdentifier = null;

        try {
            list = getSession().createQuery(
                    "select p from PersonIdentifier p where p.residentId = :residentId AND p.deleted = :deletePatient")
                    .setParameter("residentId", residentId)
                    .setParameter("deletePatient", "N")
                    .list();
        } catch (Exception e) {
            log.error("Failed to retrieve person identifier from registry patient service", e);
            throw new RegistryPatientException(e);
        }

        if (list.size() > 0)
            personIdentifier = (PersonIdentifier) list.get(0);

        return personIdentifier;
    }

    public void mergePersonIdentifier(PersonIdentifier mergePersonIdentifier) throws RegistryPatientException {
        try {
            getSession().merge(mergePersonIdentifier);
        } catch (Exception e) {
            throw new RegistryPatientException(e);
        }
    }

    public void savePersonIdentifier(PersonIdentifier identifier) throws RegistryPatientException {
        try {
            getSession().persist(identifier);
        } catch (Exception e) {
            throw new RegistryPatientException(e);
        }

    }

    public void updatePersonIdentifier(PersonIdentifier identifier) throws RegistryPatientException {
        try {
            getSession().merge(identifier);
        } catch (Exception e) {
            throw new RegistryPatientException(e);
        }
    }

    public void mergeResidents(Long merged, Long survived) {
        final PersonIdentifierMerged record = new PersonIdentifierMerged();
        record.setMergedResidentId(merged);
        record.setSurvivingResidentId(survived);
        record.setMerged(true);
        getSession().persist(record);
    }

    public void unMergeResidents(Long merged, Long survived) {
        Query q = getSession().createQuery("delete from PersonIdentifierMerged m where m.mergedResidentId=:merged AND m.survivingResidentId=:surviving");
        q.setLong("merged", merged);
        q.setLong("surviving", survived);
        q.executeUpdate();
    }

    public void setOrganizationDao(OrganizationDao organizationDao) {
        this.organizationDao = organizationDao;
    }

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }
}
