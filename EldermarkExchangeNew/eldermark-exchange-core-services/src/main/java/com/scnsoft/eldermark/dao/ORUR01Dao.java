package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.xds.message.ORUR01;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ORUR01Dao extends JpaRepository<ORUR01, Long> {
}
