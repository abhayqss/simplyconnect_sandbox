package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.AdmitIntakeResidentDateDao;
import com.scnsoft.eldermark.entity.AdmitIntakeResidentDate;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdmitIntakeHistoryServiceImpl implements AdmitIntakeHistoryService {

    @Autowired
    private AdmitIntakeResidentDateDao admitIntakeResidentDateDao;

    @Override
    public Page<AdmitIntakeResidentDate> getAdmitIntakeHistoryForResident(Long residentId, Pageable pageable) {
        final Sort.Order sortOrder = new Sort.Order(Sort.Direction.DESC, "admitIntakeDate");
        pageable = PaginationUtils.setSort(pageable, sortOrder);
        return admitIntakeResidentDateDao.getAllByResidentId(residentId, pageable);
    }
}
