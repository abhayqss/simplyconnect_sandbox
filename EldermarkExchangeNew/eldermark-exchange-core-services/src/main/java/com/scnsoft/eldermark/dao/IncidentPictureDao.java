package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.IdProjectionRepository;
import com.scnsoft.eldermark.entity.event.incident.IncidentPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentPictureDao extends JpaRepository<IncidentPicture, Long>, IdProjectionRepository<Long> {

    List<IncidentPicture> findAllByIncidentReport_Id(Long incidentReportId);
}
