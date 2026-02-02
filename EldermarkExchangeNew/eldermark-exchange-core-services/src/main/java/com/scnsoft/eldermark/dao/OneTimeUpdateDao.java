package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.OneTimeUpdate;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OneTimeUpdateDao extends AppJpaRepository<OneTimeUpdate, String> {

    List<OneTimeUpdate> findAllByAppliedAtIsNullOrderByApplyOrderingAsc();

    @Modifying
    @Query("update OneTimeUpdate set appliedAt = :when where updateName = :updateName")
    void appliedSuccessfully(@Param("updateName") String updateName,
                             @Param("when") Instant when);
}
