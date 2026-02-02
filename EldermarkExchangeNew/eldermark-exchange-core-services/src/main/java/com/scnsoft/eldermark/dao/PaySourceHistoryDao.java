package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.facesheet.PaySourceHistory;

@Repository
public interface PaySourceHistoryDao extends JpaRepository<PaySourceHistory, Long> {

    @Query("select p from PaySourceHistory p join p.client res where res.id in (:clientIds) and p.endDate is null order by p.id desc")
    List<PaySourceHistory> listByClientIds(@Param("clientIds") List<Long> clientIds);
}
