package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.NationalDrugCode;

import java.util.Optional;

public interface NationalDrugCodeDao extends AppJpaRepository<NationalDrugCode, Long> {

    Optional<NationalDrugCode> findFirstByNationalDrugCode(String ndc);

}
