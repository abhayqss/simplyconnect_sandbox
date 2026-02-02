package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.PharmacyFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.PharmacyNameAware;

import java.util.List;

public interface ClientPharmacyFilterViewService {

    List<PharmacyNameAware> findClientPharmacyNames(PermissionFilter permissionFilter, PharmacyFilter pharmacyFilter);
}
