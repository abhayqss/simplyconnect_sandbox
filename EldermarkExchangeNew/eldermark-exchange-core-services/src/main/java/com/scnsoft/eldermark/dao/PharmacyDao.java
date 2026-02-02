package com.scnsoft.eldermark.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.client.ClientPharmacy;
import com.scnsoft.eldermark.entity.community.Community;

@Repository
public interface PharmacyDao extends JpaRepository<ClientPharmacy, Long> {

    @Query("select org from ClientPharmacy resPharmacy left join resPharmacy.community org left join resPharmacy.client res where res.id IN (:clientIds) order by resPharmacy.rank")
    List<Community> listPharmaciesAsCommunity(@Param("clientIds") Collection<Long> clientIds);
}
