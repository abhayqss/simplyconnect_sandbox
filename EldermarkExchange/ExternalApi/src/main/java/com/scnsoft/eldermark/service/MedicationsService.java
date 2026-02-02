package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.MedicationDao;
import com.scnsoft.eldermark.entity.Indication;
import com.scnsoft.eldermark.entity.Medication;
import com.scnsoft.eldermark.web.entity.MedicationInfoDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 1/31/2018.
 */
@Service
@Transactional(readOnly = true)
public class MedicationsService {

    private final ResidentsService residentsService;
    private final MedicationDao medicationDao;

    @Autowired
    public MedicationsService(ResidentsService residentsService, MedicationDao medicationDao) {
        this.residentsService = residentsService;
        this.medicationDao = medicationDao;
    }

    public Page<MedicationInfoDto> getInactive(Long residentId, Pageable pageable) {
        residentsService.checkAccessOrThrow(residentId);
        final Page<Medication> medications = medicationDao.listResidentMedications(residentId, false, true, pageable);
        return convert(medications);
    }

    public Page<MedicationInfoDto> getActive(Long residentId, Pageable pageable) {
        residentsService.checkAccessOrThrow(residentId);
        final Page<Medication> medications = medicationDao.listResidentMedications(residentId, true, false, pageable);
        return convert(medications);
    }


    private static Page<MedicationInfoDto> convert(Page<Medication> medications) {
        return medications.map(new Converter<Medication, MedicationInfoDto>() {
            @Override
            public MedicationInfoDto convert(Medication source) {
                return MedicationsService.convert(source);
            }
        });
    }

    private static MedicationInfoDto convert(Medication src) {
        MedicationInfoDto dest = new MedicationInfoDto();
        dest.setMedicationName(src.getMedicationInformation().getProductNameText());
        dest.setDirections(src.getFreeTextSig());
        if (src.getMedicationStarted() != null) {
            dest.setStartedDate(src.getMedicationStarted().getTime());
        }
        if (src.getMedicationStopped() != null) {
            dest.setStoppedDate(src.getMedicationStopped().getTime());
        }
        List<Indication> indications = src.getIndications();
        if (CollectionUtils.isNotEmpty(indications)) {
            dest.setIndications(new ArrayList<String>());
            for (Indication indication : indications) {
                if (indication.getValue() != null) {
                    dest.getIndications().add(indication.getValue().getDisplayName());
                }
            }
        }
        return dest;
    }

}
