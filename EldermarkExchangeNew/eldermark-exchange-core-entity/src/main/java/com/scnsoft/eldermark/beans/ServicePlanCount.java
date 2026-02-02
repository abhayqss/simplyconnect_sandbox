package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;

public class ServicePlanCount {

    private ServicePlanStatus status;

    private Long count;
    
    public ServicePlanCount(ServicePlanStatus status, Long count) {
        this.count = count;
        this.status = status;
    }

    public ServicePlanStatus getStatus() {
        return status;
    }

    public Long getCount() {
        return count;
    }

    public void setStatus(ServicePlanStatus status) {
        this.status = status;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
