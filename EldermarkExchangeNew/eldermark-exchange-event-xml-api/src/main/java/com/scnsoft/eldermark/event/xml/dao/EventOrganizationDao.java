package com.scnsoft.eldermark.event.xml.dao;

import com.scnsoft.eldermark.entity.Organization;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventOrganizationDao extends com.scnsoft.eldermark.dao.OrganizationDao {

    List<Organization> findByOid(String oid);
}
