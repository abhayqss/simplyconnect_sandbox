package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory;

import java.util.List;

public interface ServiceCategoryService {
    List<ServiceCategory> findAllSortByDisplayName(Boolean isAccessibleOnly, PermissionFilter permissionFilter);
}
