package com.scnsoft.eldermark.dto.client.appointment;

import java.util.List;

public class AppointmentTimeSlotUnavailabilityDto {
    private String client;
    private String creator;
    private List<String> serviceProviders;
    private List<TimeSlotDto> suggestedTimeSlots;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<String> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<String> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    public List<TimeSlotDto> getSuggestedTimeSlots() {
        return suggestedTimeSlots;
    }

    public void setSuggestedTimeSlots(List<TimeSlotDto> suggestedTimeSlots) {
        this.suggestedTimeSlots = suggestedTimeSlots;
    }
}
