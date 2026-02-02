package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.client.RxNormClient;
import com.scnsoft.eldermark.consana.sync.server.client.dto.ConceptGroupDto;
import com.scnsoft.eldermark.consana.sync.server.client.dto.ConceptPropertiesDto;
import com.scnsoft.eldermark.consana.sync.server.client.dto.RxNormResponseDto;
import com.scnsoft.eldermark.consana.sync.server.dao.CcdCodeDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.MedicationInformation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.services.gateway.ConsanaGateway;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.Medication;
import org.hl7.fhir.instance.model.MedicationOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;

import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.MED_INFORMATION_LEGACY_TABLE;
import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.RX_NORM_CODE_SYSTEM_NAME;
import static com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils.getRxCui;
import static java.util.Optional.ofNullable;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaMedicationInformationConverter {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaMedicationInformationConverter.class);

    @Autowired
    private ConsanaGateway consanaGateway;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    //todo check actual autowiring of FeignClient
    private RxNormClient rxNormClient;

    public MedicationInformation convert(MedicationOrder source, Resident resident, MedicationInformation target) {
        if (target == null) {
            target = new MedicationInformation();
        }
        target.setLegacyId(0L);
        target.setDatabase(resident.getDatabase());
        target.setOrganization(resident.getFacility());
        target.setLegacyTable(MED_INFORMATION_LEGACY_TABLE);
        if (!source.hasMedication()) {
            return target;
        }
        var refMedication = source.getMedication().castToReference(source.getMedication());
        target.setProductNameText(refMedication.getDisplay());
        Medication medication = consanaGateway.getMedication(refMedication.getReference());
        String rxcui = getRxCui(medication);
        if (StringUtils.isNotEmpty(rxcui)) {
            RxNormResponseDto rxNormResponse = rxNormClient.getRxNorm(rxcui);
            try {
                String rxNorm = getRxNorm(rxcui, rxNormResponse);
                target.setProductNameCode(ccdCodeDao.getFirstByDisplayNameAndCodeSystemName(rxNorm, RX_NORM_CODE_SYSTEM_NAME));
            } catch (Exception ex) {
                logger.warn("Exception during retrieval of rx norm for rxcui", rxcui, ex);
            }
        }
        return target;
    }

    private String getRxNorm(String rxcui, RxNormResponseDto rxNormResponse) {
        return ofNullable(rxNormResponse.getAllRelatedGroup())
                .flatMap(alr -> alr.getConceptGroup()
                        .stream()
                        .map(ConceptGroupDto::getConceptProperties)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .filter(props -> rxcui.equals(props.getRxcui()))
                        .findFirst()
                )
                .map(ConceptPropertiesDto::getName)
                .orElse(null);
    }

}
