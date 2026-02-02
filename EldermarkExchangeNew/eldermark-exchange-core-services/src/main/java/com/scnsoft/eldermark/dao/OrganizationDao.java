package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationDao extends AppJpaRepository<Organization, Long> {

    @Query("select d from Organization d where lower(d.systemSetup.loginCompanyId)= lower(:loginCompanyId)")
    Organization getOrganizationByCompanyId(@Param("loginCompanyId") String loginCompanyId);

    List<Organization> getByIdInOrderByName(List<Long> id);

    Organization findFirstByOid(String oid);

    Organization findFirstByName(String name);

    Organization findByAlternativeId(String alternativeId);

    boolean existsByOid(String oid);

    boolean existsByOidAndIdNot(String oid, Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByAlternativeId(String alternativeId);

    boolean existsByAlternativeIdAndIdNot(String alternativeId, Long id);

    Organization findBySystemSetup_LoginCompanyId(String companyId);

    <T> T findBySystemSetup_LoginCompanyId(String companyId, Class<T> projection);
}
