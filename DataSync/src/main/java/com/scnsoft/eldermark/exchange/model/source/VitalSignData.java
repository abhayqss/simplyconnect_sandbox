package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Table(VitalSignData.TABLE_NAME)
public class VitalSignData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Res_Vitals";
    public static final String RES_VITALS_ID = "Res_Vitals_ID";
    public static final String JOINED_WEIGHT_UNIT = "Weight_Measure_Units";
    public static final String JOINED_HEIGHT_UNIT = "Height_Measure_Units";

    @Id
    @Column(RES_VITALS_ID)
    private long id;

    @Column("res_vitals_uuid")
    private String uuid;

    @Column("Date")
    private Date date;

    @Column("Time")
    private Time time;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Facility")
    private String facility;

    @Column(JOINED_HEIGHT_UNIT)
    private String heightMeasureUnit;

    @Column(JOINED_WEIGHT_UNIT)
    private String weightMeasureUnit;

    @Column("Weight")
    private BigDecimal weight;

    @Column("Height")
    private BigDecimal height;

    @Column("Blood_Pressure")
    private String bloodPressure;

    @Column("Pulse")
    private String pulse;

    @Column("Respiration")
    private String respiration;

    @Column("Temperature")
    private String temperature;

    @Column("Blood_Sugar")
    private String bloodSugar;

    @Column("O2_Saturation")
    private String o2Saturation;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
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

    public String getRespiration() {
        return respiration;
    }

    public void setRespiration(String respiration) {
        this.respiration = respiration;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getO2Saturation() {
        return o2Saturation;
    }

    public void setO2Saturation(String o2Saturation) {
        this.o2Saturation = o2Saturation;
    }

    public String getHeightMeasureUnit() {
        return heightMeasureUnit;
    }

    public void setHeightMeasureUnit(String heightMeasureUnit) {
        this.heightMeasureUnit = heightMeasureUnit;
    }

    public String getWeightMeasureUnit() {
        return weightMeasureUnit;
    }

    public void setWeightMeasureUnit(String weightMeasureUnit) {
        this.weightMeasureUnit = weightMeasureUnit;
    }
}
