package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import com.scnsoft.eldermark.service.CcdCodeService;
import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CcdCodeFacadeImpl implements CcdCodeFacade {

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private ListAndItemConverter<CcdCode, CcdCodeDto> ccdCodeDtoConverter;

    @Override
    public List<CcdCodeDto> findReferralReason(String search) {
        return ccdCodeDtoConverter.convertList(ccdCodeService.findByDisplayNameLikeAndValueSet(search, ValueSetEnum.PROCEDURE_REASON,
                Sort.by(CcdCode_.DISPLAY_NAME)));
    }

}
