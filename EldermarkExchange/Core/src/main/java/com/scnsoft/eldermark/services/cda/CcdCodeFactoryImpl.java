package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
@Component
public class CcdCodeFactoryImpl implements CcdCodeFactory {

    private final CcdCodeService ccdCodeService;

    @Autowired
    public CcdCodeFactoryImpl(CcdCodeService ccdCodeService) {
        this.ccdCodeService = ccdCodeService;
    }

    @Override
    public <T extends CD> CcdCode convert(T src) {
        return convert(src, null);
    }

    @Override
    public <T extends CD> CcdCode convert(T src, String hintValueSetOid) {
        if ((!CcdParseUtils.hasContent(src)) || (src.getCode() == null) || (src.getCodeSystem() == null)) return null;
        return ccdCodeService.findOrCreate(src.getCode(), src.getDisplayName(), src.getCodeSystem(), src.getCodeSystemName(), hintValueSetOid);
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
