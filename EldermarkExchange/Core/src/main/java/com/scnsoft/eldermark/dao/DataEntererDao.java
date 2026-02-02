package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.DataEnterer;

public interface DataEntererDao extends ResidentAwareDao<DataEnterer> {
    DataEnterer getCcdDataEnterer(Long residentId);
}
