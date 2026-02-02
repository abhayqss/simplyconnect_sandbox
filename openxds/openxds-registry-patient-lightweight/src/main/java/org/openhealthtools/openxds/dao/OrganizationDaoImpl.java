package org.openhealthtools.openxds.dao;

import org.apache.commons.collections.CollectionUtils;
import org.openhealthtools.openxds.entity.Organization;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;


public class OrganizationDaoImpl extends HibernateDaoSupport implements OrganizationDao {

    @Override
    public Organization findFirstByName(String name) {
        List result = this.getHibernateTemplate().find(
                "from Organization d where d.name = ?", name);

        return CollectionUtils.isNotEmpty(result) ? (Organization) result.get(0) : null;
    }

    @Override
    public Organization findFirstByNameAndDatabaseOid(String name, String databaseOid) {
        List result = this.getHibernateTemplate().find(
                "from Organization d where d.database.oid = ? and d.name = ?", databaseOid, name);

        return CollectionUtils.isNotEmpty(result) ? (Organization) result.get(0) : null;
    }

    @Override
    public Organization findByUniversalId(String oid, Long databaseId) {
        List result = this.getHibernateTemplate().find(
                "from Organization d where d.oid = ? and d.databaseId = ?", oid, databaseId);

        return CollectionUtils.isNotEmpty(result) ? (Organization) result.get(0) : null;
    }

    @Override
    public Organization findDefaultByDatabase(Long databaseId){
        List result = this.getHibernateTemplate().find(
                "from Organization d where d.databaseId = ? and d.isXdsDefault = 1", databaseId);
        return CollectionUtils.isNotEmpty(result) ? (Organization) result.get(0) : null;
    }

    @Override
    public Organization findDefaultByDatabaseOid(String databaseOid){
        List result = this.getHibernateTemplate().find(
                "from Organization d where d.database.oid = ? and d.isXdsDefault = 1", databaseOid);
        return CollectionUtils.isNotEmpty(result) ? (Organization) result.get(0) : null;
    }
}
