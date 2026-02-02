package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.ThirdPartyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author phomal
 *
 * Created on 1/19/2018.
 */
@Repository
public interface ThirdPartyApplicationDao extends JpaRepository<ThirdPartyApplication, Long> {

    @Modifying
    @Query("UPDATE ThirdPartyApplication SET timeZoneOffset = :offset WHERE id = :userId")
    void updateTimezone(@Param("userId") Long userId, @Param("offset") Long timeZoneOffset);

    ThirdPartyApplication findByName(@Param("name") String name);

}
