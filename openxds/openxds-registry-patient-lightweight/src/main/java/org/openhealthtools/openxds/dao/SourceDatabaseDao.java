package org.openhealthtools.openxds.dao;


import org.openhealthtools.openxds.entity.Database;
public interface SourceDatabaseDao {
    Database findFirstByName(String name);

    Database findByOID(String oid);
}
