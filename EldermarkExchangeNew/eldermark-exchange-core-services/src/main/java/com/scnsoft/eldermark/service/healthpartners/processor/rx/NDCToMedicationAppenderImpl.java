package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
class NDCToMedicationAppenderImpl implements NDCToMedicationAppender {
    private static final Logger logger = LoggerFactory.getLogger(NDCToMedicationAppenderImpl.class);

    @Autowired
    private CcdCodeCustomService ccdCodeCustomService;

    @Override
    public void addUniqueNdcToMedication(Medication medication, String nationalDrugCode, String drugName) {
        logger.info("Adding NDC to medication");
        var translationCodes = medication.getMedicationInformation().getTranslationProductCodes();
        if (CollectionUtils.emptyIfNull(translationCodes).stream().noneMatch(code -> equalsNdcCode(code, nationalDrugCode))) {
            var ccdCode = ccdCodeCustomService.findOrCreate(nationalDrugCode, drugName, CodeSystem.NDC).orElseThrow();
            if (translationCodes == null) {
                translationCodes = new ArrayList<>();
                medication.getMedicationInformation().setTranslationProductCodes(translationCodes);
            }
            translationCodes.add(ccdCode);
        }
        logger.info("Added NDC to medication");
    }

    private boolean equalsNdcCode(CcdCode ccdCode, String nationalDrugCode) {
        return CodeSystem.NDC.getOid().equals(ccdCode.getCodeSystem())
                && StringUtils.equals(nationalDrugCode, ccdCode.getCode());
    }
}
