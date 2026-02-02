package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.Resident;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class XdsRepoPatientDaoImpl extends HibernateDaoSupport implements XdsRepoPatientDao {
    @Override
    public Resident findOne(Long patientRepoId) {
        return (Resident) getSession().load(Resident.class, patientRepoId);
    }


    @Override
    public Resident save(Resident entity) {
        if (entity.getId() == null) {
            getSession().persist(entity);
            return entity;
        } else {
            return (Resident) getSession().merge(entity);
        }
    }

    @Override
    public Resident saveAndFlush(Resident entity) {
        Resident result = save(entity);
        getSession().flush();

        return result;
    }

}
