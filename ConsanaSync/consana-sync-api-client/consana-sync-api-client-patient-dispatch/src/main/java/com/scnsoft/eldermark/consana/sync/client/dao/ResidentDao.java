package com.scnsoft.eldermark.consana.sync.client.dao;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentDao extends JpaRepository<Resident, Long> {}
