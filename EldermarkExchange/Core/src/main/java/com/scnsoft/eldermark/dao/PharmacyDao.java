package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.ResidentPharmacy;

import java.util.Collection;
import java.util.List;

public interface PharmacyDao extends ResidentAwareDao<ResidentPharmacy> {
    List<Organization> listPharmaciesAsOrganization(Long residentId);
    List<Organization> listPharmaciesAsOrganization(Collection<Long> residentIds);
}
