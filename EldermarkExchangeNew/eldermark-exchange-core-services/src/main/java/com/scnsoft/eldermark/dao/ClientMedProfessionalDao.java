package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.client.ClientMedProfessional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientMedProfessionalDao extends JpaRepository<ClientMedProfessional, Long> {

    @Query("select resMedProfessional from ClientMedProfessional resMedProfessional join resMedProfessional.client res  where res.id IN (:clientIds) order by resMedProfessional.rank")
    List<ClientMedProfessional> listByClientIds(@Param("clientIds") List<Long> clientIds);

}
