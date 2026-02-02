package com.scnsoft.eldermark.service;


import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ServiceCategoryDao;
import com.scnsoft.eldermark.dao.specification.ServiceCategorySpecificationGenerator;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory_;
import com.scnsoft.eldermark.service.internal.EntityListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    @Autowired
    private ServiceCategoryDao serviceCategoryDao;

    @Autowired
    private ServiceCategorySpecificationGenerator serviceCategorySpecificationGenerator;

    @Override
    public List<ServiceCategory> findAllSortByDisplayName(Boolean isAccessibleOnly, PermissionFilter permissionFilter) {
        var sort = Sort.by(Sort.Order.asc(ServiceCategory_.DISPLAY_NAME));
        List<ServiceCategory> serviceCategories;
        if (BooleanUtils.isTrue(isAccessibleOnly)) {
            var fromAccessibleCommunities = serviceCategorySpecificationGenerator.fromAccessibleCommunities(permissionFilter);
            serviceCategories = serviceCategoryDao.findAll(fromAccessibleCommunities, sort);
        } else {
            serviceCategories =  serviceCategoryDao.findAll(sort);
        }
        EntityListUtils.moveItemToEnd(serviceCategories, "Other");
        return serviceCategories;
    }

}
