package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.MedicationDao;
import com.scnsoft.eldermark.entity.Indication;
import com.scnsoft.eldermark.entity.Medication;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.web.entity.MedicationInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 1/10/2017.
 */
@Service
@Transactional(readOnly = true)
public class MedicationService extends BasePhrService {

    @Autowired
    MedicationDao medicationDao;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    public Page<MedicationInfoDto> getUserMedicationsActive(Long userId, Pageable pageable) {
        return getUserMedications(userId, true, false, pageable);
    }

    public Page<MedicationInfoDto> getUserMedicationsHistory(Long userId, Pageable pageable) {
        return getUserMedications(userId, false, true, pageable);
    }

    private Page<MedicationInfoDto> getUserMedications(Long userId, boolean includeActive, boolean includeInactive, Pageable pageable) {

        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_PHR);
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MEDICATIONS_LIST);
        Collection<Long> activeResidentIds = getResidentIdsOrThrow(userId);
        Page<Medication> page = medicationDao.listResidentMedicationsWithoutDuplicates(activeResidentIds, includeActive, includeInactive, pageable);
        Page<MedicationInfoDto> resultingPage = page.map(new Converter<Medication, MedicationInfoDto>() {
            @Override
            public MedicationInfoDto convert(Medication source) {
                return transform(source);
            }
        });

        return resultingPage;
    }

    static MedicationInfoDto transform(Medication src) {
        MedicationInfoDto dest = new MedicationInfoDto();
        dest.setMedicationName(src.getMedicationInformation().getProductNameText());
        dest.setDirections(src.getFreeTextSig());
        if (src.getMedicationStarted()!=null) {
            dest.setStartedDate(src.getMedicationStarted().getTime());
            dest.setStartedDateStr(DATE_TIME_FORMAT.format(src.getMedicationStarted()));
        }
        if (src.getMedicationStopped()!=null) {
            dest.setStoppedDate(src.getMedicationStopped().getTime());
            dest.setStoppedDateStr(DATE_TIME_FORMAT.format(src.getMedicationStopped()));
        }
        dest.setStatus(src.getStatusCode());
        List<Indication> indications = src.getIndications();
        if (CollectionUtils.isNotEmpty(indications)) {
            dest.setIndications(new ArrayList<String>());
            for (Indication indication: indications) {
                if (indication.getValue()!=null) {
                    dest.getIndications().add(indication.getValue().getDisplayName());
                }
            }
        }
        return dest;

    }


}
