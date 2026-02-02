package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CcdCodeDao extends JpaRepository<CcdCode, Long> {

    CcdCode getFirstByCodeAndCodeSystem(String code, String codeSystem);

    @Query("SELECT c FROM CcdCode c INNER JOIN c.valueSets s WHERE c.code = :code AND s.oid = :valueSet")
    CcdCode getByCodeAndValueSet(@Param("code") String code, @Param("valueSet") String valueSet);

    CcdCode getFirstByValueSetAndDisplayName(String valueSet, String displayName);

    CcdCode getFirstByDisplayNameAndCodeSystemName(String displayName, String codeDisplayName);

}
