package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.document.CcdCode;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CcdCodeService {

    CcdCode findById(Long id);

    CcdCode getOne(Long id);

    List<CcdCode> findByDisplayNameLikeAndValueSet(String displayName, ValueSetEnum valueSetEnum, Sort sort);

    CcdCode findByCodeAndValueSet(String code, ValueSetEnum valueSetEnum);

}
