package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.FamilyHistoryDao;
import com.scnsoft.eldermark.dao.healthdata.FamilyHistoryObservationDao;
import com.scnsoft.eldermark.entity.FamilyHistory;
import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional(readOnly = true)
public class FamilyHistoryService extends BasePhrService {

    @Autowired
    private FamilyHistoryDao familyHistoryDao;

    @Autowired
    private FamilyHistoryObservationDao familyHistoryObservationDao;

    public Page<FamilyHistory> listFamilyHistory(Collection<Long> residentIds,
                                                 Pageable pageable) {
        final Sort.Order sortOrder = new Sort.Order(Sort.Direction.ASC, "relatedSubjectCode.displayName");
        final Pageable pageableWithSort = PaginationUtils.setSort(pageable, sortOrder);
        return familyHistoryDao.listResidentFamilyHistoryWithoutDuplicates(residentIds, pageableWithSort);
    }

    public FamilyHistory getFamilyHistory(Long familyHistoryId) {
        return familyHistoryDao.getOne(familyHistoryId);
    }

    public FamilyHistoryObservation getFamilyHistoryObservation(Long observationId) {
        return familyHistoryObservationDao.getOne(observationId);
    }

}
