package com.scnsoft.eldermark.consana.sync.server.dao;


import com.scnsoft.eldermark.consana.sync.server.model.entity.MPI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MPIDao extends JpaRepository<MPI, Long> {

    List<MPI> getAllByResident_Id(Long residentId);
}
