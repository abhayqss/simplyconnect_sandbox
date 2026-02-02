package org.openhealthtools.openxds.registry.dao;

import org.openhealthtools.openxds.registry.Document;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class XdsRepoDocumentDaoImpl extends HibernateDaoSupport implements XdsRepoDocumentDao {
    @Override
    public Document findOne(Long patientRepoId) {
        return (Document) getSession().load(Document.class, patientRepoId);
    }


    @Override
    public Document save(Document entity) {
        if (entity.getId() == null) {
            getSession().persist(entity);
            return entity;
        } else {
            return (Document) getSession().merge(entity);
        }
    }

}
