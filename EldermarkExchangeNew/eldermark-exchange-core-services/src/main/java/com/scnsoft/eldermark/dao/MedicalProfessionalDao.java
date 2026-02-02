package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.MedicalProfessional;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalProfessionalDao extends AppJpaRepository<MedicalProfessional, Long> {
}
