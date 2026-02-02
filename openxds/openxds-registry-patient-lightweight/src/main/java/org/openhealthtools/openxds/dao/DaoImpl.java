package org.openhealthtools.openxds.dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DaoImpl extends HibernateDaoSupport {
    public void persist(Object object) {
            getSession().persist(object);
    }
}
