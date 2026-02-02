package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.Database;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;


public class SourceDatabaseDaoImpl extends HibernateDaoSupport implements SourceDatabaseDao {

    @Override
    public Database findFirstByName(String name) {
        List result = this.getHibernateTemplate().find(
                "from Database d where d.name = ?", name);

        return (result != null && !result.isEmpty()) ? (Database) result.get(0) : null;
    }

    @Override
    public Database findByOID(String oid){
        List result = this.getHibernateTemplate().find(
                "from Database d where d.oid = ?", oid);

        return (result != null && !result.isEmpty()) ? (Database) result.get(0) : null;
    }
}