package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CcdCodeDao extends AppJpaRepository<CcdCode, Long> {
    List<CcdCode> findByValueSetName(String valueSetName);

    @Query("Select p from CcdCode p where p.code=:name and p.codeSystem =:roleCodeSystem")
    CcdCode getCcdCode(@Param("name") String name, @Param("roleCodeSystem") String roleCodeSystem);

    //remove getCcdCode and use this one instead?
    CcdCode findFirstByCodeAndCodeSystem(String code, String codeSystem);

    CcdCode findFirstByDisplayNameAndCodeSystem(String name, String codeSystem);

    List<CcdCode> findByValueSetNameAndDisplayNameIn(String valueSetName, Iterable<String> displayNames);

    List<CcdCode> findByValueSetNameAndDisplayNameInAndInterpretation(String valueSetName, Iterable<String> displayNames, boolean interpretation);
}
