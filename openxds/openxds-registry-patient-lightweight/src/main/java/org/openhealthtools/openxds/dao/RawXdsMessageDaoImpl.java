package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.RawXdsMessage;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

//TODO remove! use DaoImpl instead
public class RawXdsMessageDaoImpl extends HibernateDaoSupport implements RawXdsMessageDao {
    @Override
    public void saveRawXdsMessage(RawXdsMessage message) {
            getSession().persist(message);
    }
}
