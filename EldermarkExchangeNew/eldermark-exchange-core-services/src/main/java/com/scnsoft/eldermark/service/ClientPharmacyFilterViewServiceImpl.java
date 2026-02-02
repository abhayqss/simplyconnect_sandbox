package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.PharmacyFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.PharmacyNameAware;
import com.scnsoft.eldermark.dao.ClientPharmacyFilterViewDao;
import com.scnsoft.eldermark.dao.specification.ClientPharmacyFilterViewSpecificationGenerator;
import com.scnsoft.eldermark.entity.client.ClientPharmacyFilterView_;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientPharmacyFilterViewServiceImpl implements ClientPharmacyFilterViewService {

    @Autowired
    private ClientPharmacyFilterViewSpecificationGenerator clientPharmacyFilterViewSpecificationGenerator;

    @Autowired
    private ClientPharmacyFilterViewDao clientPharmacyFilterViewDao;

    @Override
    public List<PharmacyNameAware> findClientPharmacyNames(PermissionFilter permissionFilter, PharmacyFilter pharmacyFilter) {
        var hasAccess = clientPharmacyFilterViewSpecificationGenerator.hasAccess(permissionFilter);
        var byFilter = clientPharmacyFilterViewSpecificationGenerator.byFilter(pharmacyFilter);
        var distinct = clientPharmacyFilterViewSpecificationGenerator.distinct();
        return clientPharmacyFilterViewDao.findAll(hasAccess.and(byFilter.and(distinct)), PharmacyNameAware.class, Sort.by(ClientPharmacyFilterView_.PHARMACY_NAME));
    }
}
