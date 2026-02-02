package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.document.ccd.DiagnosisCcdCode;

import java.util.List;

public interface DiagnosisCcdCodeDao extends AppJpaRepository<DiagnosisCcdCode, Long> {

    List<DiagnosisCcdCode> findAllByCodeAndCodeSystem(String code, String codeSystem);

}
