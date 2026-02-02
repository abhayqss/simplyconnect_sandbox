package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.xds.message.AdtMessage;

@Repository
public interface AdtMessageDao extends JpaRepository<AdtMessage, Long> {

}
