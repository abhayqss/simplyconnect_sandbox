package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.InterpretiveCcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ConcreteCcdCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterpretiveCcdCodeDao extends JpaRepository<InterpretiveCcdCode, Long> {

    @Query("SELECT o FROM InterpretiveCcdCode o WHERE o.referredCcdCode=:originalCode AND o.displayName=:displayName")
    InterpretiveCcdCode getCcdCode(@Param("originalCode") ConcreteCcdCode originalCode,
            @Param("displayName") String displayName);

    @Override
    InterpretiveCcdCode save(InterpretiveCcdCode ccdCode);

}
