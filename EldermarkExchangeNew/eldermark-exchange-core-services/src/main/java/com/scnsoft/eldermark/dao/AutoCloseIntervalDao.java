package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.community.AutoCloseInterval;

@Repository
public interface AutoCloseIntervalDao extends JpaRepository<AutoCloseInterval, Long> {

}
