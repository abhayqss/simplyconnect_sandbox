package com.scnsoft.eldermark.consana.sync.client.services.entities.impl;

import com.scnsoft.eldermark.consana.sync.client.dao.ResidentDao;
import com.scnsoft.eldermark.consana.sync.client.dao.specification.ResidentSpecificationGenerator;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.services.entities.ResidentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Stream;

@Service
@Transactional
public class ResidentServiceImpl implements ResidentService {

    private final Set<String> pharmacyNames = Set.of("Total Care Pharmacy");

    private final ResidentDao residentDao;

    private final ResidentSpecificationGenerator residentSpecificationGenerator;

    @Autowired
    public ResidentServiceImpl(ResidentDao residentDao, ResidentSpecificationGenerator residentSpecificationGenerator) {
        this.residentDao = residentDao;
        this.residentSpecificationGenerator = residentSpecificationGenerator;
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<Resident> getMergedResidents(Long residentId) {
        return residentDao.getMergedResidents(residentId).stream();
    }

    @Override
    @Transactional(readOnly = true)
    public Resident getOne(Long residentId) {
        return residentDao.getOne(residentId);
    }

    @Override
    public Resident updateXrefId(Resident resident) {
        if (StringUtils.isEmpty(resident.getConsanaXrefId())) {
            resident.setConsanaXrefId(resident.getId().toString());
            residentDao.updateXrefId(resident.getConsanaXrefId(), resident.getId());
        }
        return resident;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPharmacyNamesAndAdmittedDateCorrect(Resident resident) {
        var byId = residentSpecificationGenerator.byId(resident);
        var byPharmacyNames = residentSpecificationGenerator.byPharmacyNames(pharmacyNames);
        var isAdmitted = residentSpecificationGenerator.isAdmitted();
        return residentDao.count(byId.and(byPharmacyNames.and(isAdmitted))) > 0;
    }
}
