package com.scnsoft.eldermark.converter.entity2dto.organization;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.dto.ProblemObservationCodeDto;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;

@Component
@Deprecated
public class ProblemObservationCodeDtoConverter implements Converter<ProblemObservation, ProblemObservationCodeDto> {

    @Override
    public ProblemObservationCodeDto convert(ProblemObservation source) {
        ProblemObservationCodeDto target = new ProblemObservationCodeDto();
        CcdCode problemCode = findBestCodeWithIcd10Priority(source);
        String code = problemCode == null ? source.getProblemIcdCode() : problemCode.getCode();
        String codeSet = problemCode == null ? source.getProblemIcdCodeSet() : problemCode.getCodeSystemName();
        target.setCode(code);
        target.setCodeSet(codeSet);
        return target;

    }

    private CcdCode findBestCodeWithIcd10Priority(ProblemObservation problemObservation) {
        final List<CcdCode> problemCodes = new ArrayList<>();
        if (problemObservation.getProblemCode() != null) {
            problemCodes.add(problemObservation.getProblemCode());
        }
        problemCodes.addAll(problemObservation.getTranslations());
        if (problemCodes.isEmpty()) {
            return null;
        }

        for (CcdCode ccdCode : problemCodes) {
            if ("ICD-10-CM".equals(ccdCode.getCodeSystem())) {
                return ccdCode;
            }
        }
        return problemCodes.get(0);
    }

}
