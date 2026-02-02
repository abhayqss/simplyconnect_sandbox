package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAllergyDao extends AppJpaRepository<ClientAllergy, Long>{
}
