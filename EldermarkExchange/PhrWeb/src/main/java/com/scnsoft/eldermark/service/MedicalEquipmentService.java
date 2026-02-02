package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.MedicalEquipmentDao;
import com.scnsoft.eldermark.entity.MedicalEquipment;
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
public class MedicalEquipmentService extends BasePhrService {

    @Autowired
    private MedicalEquipmentDao medicalEquipmentDao;

    public Page<MedicalEquipment> getMedicalEquipments(final Collection<Long> residentIds, final Pageable pageable) {
        Sort.Order ORDER_BY_EFFECTIVE_DATE_DESC = new Sort.Order(Sort.Direction.DESC, "effectiveTimeHigh");
        Sort.Order ORDER_BY_SUPPLY_DEVICE = new Sort.Order(Sort.Direction.ASC, "productInstance.deviceCode.displayName");
        final Pageable pageableWithSort = PaginationUtils.setSort(pageable, ORDER_BY_EFFECTIVE_DATE_DESC, ORDER_BY_SUPPLY_DEVICE);
        Page<MedicalEquipment> page = medicalEquipmentDao.listResidentsMedicalEquipmentWithoutDuplicates(residentIds, pageableWithSort);
        return page;
    }

    public MedicalEquipment getMedicalEquipment(Long medicalEquipmentId) {
        final MedicalEquipment medicalEquipment = medicalEquipmentDao.getOne(medicalEquipmentId);
        return medicalEquipment;
    }
}
