package com.scnsoft.eldermark.service.document.cda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;

@Component
public class CcdCodeFactoryImpl implements CcdCodeFactory {

    private final CcdCodeCustomService ccdCodeCustomService;

    @Autowired
    public CcdCodeFactoryImpl(CcdCodeCustomService ccdCodeService) {
        this.ccdCodeCustomService = ccdCodeService;
    }

    @Override
    public <T extends CD> CcdCode convert(T src) {
        return convert(src, null);
    }

    @Override
    public <T extends CD> CcdCode convert(T src, String hintValueSetOid) {
        if (!CcdParseUtils.hasContent(src)
                || src.getCode() == null
                || src.getCodeSystem() == null) {
            return null;
        }
        return ccdCodeCustomService.findOrCreate(src.getCode(), src.getDisplayName(), src.getCodeSystem(),
                src.getCodeSystemName(), hintValueSetOid).orElse(null);
    }

    @Override
    public <T extends CD> List<CcdCode> convert(Collection<T> codes) {
        List<CcdCode> result = null;
        if (!CollectionUtils.isEmpty(codes)) {
            result = new ArrayList<>();
            for (T code : codes) {
                CcdCode ccdCode = this.convert(code);
                if (ccdCode != null)
                    result.add(ccdCode);
            }
        }
        return result;
    }

    @Override
    public List<CcdCode> convertInterpretationCodes(Observation ccdObservation) {
        if (ccdObservation != null) {
            return this.convert(ccdObservation.getInterpretationCodes());
        } else {
            return null;
        }
    }

}
