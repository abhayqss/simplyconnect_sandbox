package com.scnsoft.eldermark.service.transformer.populator;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.service.DataSourceService;
import com.scnsoft.eldermark.web.entity.PeriodDto;
import com.scnsoft.eldermark.web.entity.ProblemInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProblemInfoPopulator implements Populator<ProblemObservation, ProblemInfoDto> {

    protected static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public void populate(ProblemObservation srcObservation, ProblemInfoDto target) {
        if (srcObservation == null) {
            return;
        }
        Problem src = srcObservation.getProblem();
        target.setId(srcObservation.getId());

        target.setAgeObservationUnit(srcObservation.getAgeObservationUnit());
        target.setAgeObservationValue(srcObservation.getAgeObservationValue());
        correctAgeObservationFields(target);

        target.setDataSource(DataSourceService.transform(srcObservation.getDatabase(), src.getResidentId()));

        CcdCode srcObservationCode = srcObservation.getProblemCode();
        if (srcObservationCode != null) {
            target.setDiagnosisCode(srcObservationCode.getCode());
            target.setDiagnosisCodeSet(srcObservationCode.getCodeSystemName());
        }
        Map<String, String> translationsMap = new HashMap<>();
        for (CcdCode srcTranslation : srcObservation.getTranslations()) {
            translationsMap.put(srcTranslation.getCode(), srcTranslation.getCodeSystemName());
        }
        target.setTranslations(translationsMap);

        target.setProblemName(srcObservation.getProblemName());
        target.setStatus(StringUtils.capitalize(StringUtils.defaultIfEmpty(
                srcObservation.getProblemStatusText(), src.getStatusCode())));
        target.setHealthStatusObservation(srcObservation.getHealthStatusObservationText());

        CcdCode problemType = srcObservation.getProblemType();
        if (problemType != null) {
            target.setProblemType(problemType.getDisplayName());
        }

        PeriodDto period = new PeriodDto();
        target.setPeriod(period);
        if (src.getTimeLow() != null) {
            period.setStartDate(src.getTimeLow().getTime());
            period.setStartDateStr(DATE_TIME_FORMAT.format(src.getTimeLow()));
        }
        if (src.getTimeHigh() != null) {
            period.setEndDate(src.getTimeHigh().getTime());
            period.setEndDateStr(DATE_TIME_FORMAT.format(src.getTimeHigh()));
        }
    }

    private void correctAgeObservationFields(ProblemInfoDto target) {
        if ("day".equals(target.getAgeObservationUnit()) && target.getAgeObservationValue() != null) {
            if (target.getAgeObservationValue() > 730) {
                target.setAgeObservationUnit("a");
                target.setAgeObservationValue(target.getAgeObservationValue() / 365);
            } else if (target.getAgeObservationValue() > 100) {
                target.setAgeObservationUnit("m");
                target.setAgeObservationValue(target.getAgeObservationValue() / 30);
            }
        }
    }
}
