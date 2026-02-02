package com.scnsoft.eldermark.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * This DTO is intended to represent submitted marketplace appointment
 */
public class MarketplaceCommunityAppointmentDto {

    private String name;
    private List<Long> serviceIds = new ArrayList<>();
    private Long appointmentDate;
    private Integer timezoneOffset;
    private Long createdDate;
    private String phone;
    private String email;
    private Boolean isUrgentCare;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Long appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

	public List<Long> getServiceIds() {
		return serviceIds;
	}

	public void setServiceIds(List<Long> serviceIds) {
		this.serviceIds = serviceIds;
	}

	public Boolean getIsUrgentCare() {
		return isUrgentCare;
	}

	public void setIsUrgentCare(Boolean isUrgentCare) {
		this.isUrgentCare = isUrgentCare;
	}
}
