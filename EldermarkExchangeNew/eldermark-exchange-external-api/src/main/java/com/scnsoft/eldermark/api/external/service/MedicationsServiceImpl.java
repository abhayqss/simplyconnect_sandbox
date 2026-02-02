package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.MedicationInfoDto;
import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.dao.ClientMedicationDao;
import com.scnsoft.eldermark.dao.specification.ClientMedicationSpecificationGenerator;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MedicationsServiceImpl implements MedicationsService {

    private final ResidentsService residentsService;
    private final ClientMedicationDao medicationDao;
    private final ClientMedicationSpecificationGenerator medicationSpecifications;

    @Autowired
    public MedicationsServiceImpl(ResidentsService residentsService, ClientMedicationDao medicationDao, ClientMedicationSpecificationGenerator medicationSpecifications) {
        this.residentsService = residentsService;
        this.medicationDao = medicationDao;
        this.medicationSpecifications = medicationSpecifications;
    }

    @Override
    public Page<MedicationInfoDto> getInactive(Long residentId, Pageable pageable) {
        residentsService.checkAccessOrThrow(residentId);
        var filter = new ClientMedicationFilter();
        filter.setClientId(residentId);
        filter.setIncludeActive(false);
        filter.setIncludeInactive(true);
        filter.setIncludeUnknown(false);

        return getPage(filter, pageable);
    }

    @Override
    public Page<MedicationInfoDto> getActive(Long residentId, Pageable pageable) {
        residentsService.checkAccessOrThrow(residentId);
        var filter = new ClientMedicationFilter();
        filter.setClientId(residentId);
        filter.setIncludeActive(true);
        filter.setIncludeInactive(false);
        filter.setIncludeUnknown(true);

        return getPage(filter, pageable);
    }

    private Page<MedicationInfoDto> getPage(ClientMedicationFilter filter, Pageable pageable) {
        var byFilter = medicationSpecifications.byFilter(filter);

        //duplicates are not excluded in old portal external api
        var medications = medicationDao.findAll(byFilter, pageable);
        return medications.map(MedicationsServiceImpl::convert);
    }


    private static MedicationInfoDto convert(ClientMedication src) {
        MedicationInfoDto dest = new MedicationInfoDto();
        dest.setMedicationName(src.getMedicationInformation().getProductNameText());
        dest.setDirections(src.getFreeTextSig());
        dest.setStartedDate(DateTimeUtils.toEpochMilli(src.getMedicationStarted()));
        dest.setStoppedDate(DateTimeUtils.toEpochMilli(src.getMedicationStopped()));

        if (CollectionUtils.isNotEmpty(src.getIndications())) {
            var indications = src.getIndications().stream()
                    .map(Indication::getValue)
                    .filter(Objects::nonNull)
                    .map(CcdCode::getDisplayName)
                    .collect(Collectors.toList());
            dest.setIndications(indications);
        }

        return dest;
    }

}
