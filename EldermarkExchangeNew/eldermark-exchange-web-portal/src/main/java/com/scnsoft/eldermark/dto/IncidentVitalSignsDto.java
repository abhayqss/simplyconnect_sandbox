package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentVitalSignsDto {

    private Long id;
    @Size(max = 256)
    private String bloodPressure;
    @Size(max = 256)
    private String pulse;
    @Size(max = 256)
    private String respirationRate;
    @Size(max = 256)
    private String temperature;
    @Size(max = 256)
    private String O2Saturation;
    @Size(max = 256)
    private String bloodSugar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getRespirationRate() {
        return respirationRate;
    }

    public void setRespirationRate(String respirationRate) {
        this.respirationRate = respirationRate;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getO2Saturation() {
        return O2Saturation;
    }

    public void setO2Saturation(String o2Saturation) {
        O2Saturation = o2Saturation;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }
}
