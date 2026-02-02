package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.specification.CcdCodeSpecificationGenerator;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CcdCodeServiceImpl implements CcdCodeService {

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private CcdCodeSpecificationGenerator ccdCodeSpecificationGenerator;

    @Override
    @Transactional(readOnly = true)
    public CcdCode findById(Long id) {
        return ccdCodeDao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CcdCode getOne(Long id) {
        return ccdCodeDao.getOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CcdCode> findByDisplayNameLikeAndValueSet(String displayName, ValueSetEnum valueSetEnum, Sort sort) {
        var byDisplayNameLike = ccdCodeSpecificationGenerator.byDisplayNameLike(displayName);
        var byValueSet = ccdCodeSpecificationGenerator.byValueSet(valueSetEnum);

        return ccdCodeDao.findAll(byDisplayNameLike.and(byValueSet), sort);
    }

    @Override
    @Transactional(readOnly = true)
    public CcdCode findByCodeAndValueSet(String code, ValueSetEnum valueSetEnum) {
        var byCode = ccdCodeSpecificationGenerator.byCode(code);
        var byValueSet = ccdCodeSpecificationGenerator.byValueSet(valueSetEnum);

        return CareCoordinationUtils.getFistNotNull(ccdCodeDao.findAll(byCode.and(byValueSet))).orElse(null);
    }
}
