package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Time;
import java.sql.Date;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResMedicationData.TABLE_NAME)
public class ResMedicationData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Res_Medications";
    public static final String RES_MED_ID = "Res_Med_ID";
    public static final String ROUTE_CCDID = "Route_CCDID";

    @Id
    @Column(RES_MED_ID)
    private long id;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Medication")
    private String medication;

    @Column("NDC")
    private String ndc;

    @Column("Prescription_End_Date")
    private Date prescriptionEndDate;

    @Column("Passing_Times")
    private String passingTimes;

    @Column(ROUTE_CCDID)
    private Long routeCcdId;

    @Column("Indicated_For")
    private String indicatedFor;

    @Column("Prescription_Number")
    private String prescriptionNumber;

    @Column("Instructions")
    private String instructions;

    @Column("Facility_Instruction_Note")
    private String facilityInstructionNote;

    @Column("Pharmacy_Code")
    private Long pharmacyId;

    @Column("Prescribed_by_Code")
    private Long medProfessionalId;
    
    @Column("Schedule")
    private String schedule;
    
    @Column("Dosage")
    private String dosage;
    
    @Column("Effective_Date")
    private Date effectiveDate;
    
    @Column("Origin")
    private String origin;
    
    @Column("Administer_by_nurse_only")
    private Boolean administerByNurseOnly;
    
    @Column("Effective_Time")
    private Time effectiveTime;
    
    @Column("Prescription_End_Time")
    private Time prescriptionEndTime;
    
    @Column("Recurring_Task_Data")
    private String recurringTaskData; 
    
    @Column("PRN_Scheduled")
    private Boolean prnScheduled;

    @Column("Pharmacy_Origin_Date")
    private Date pharmacyOriginDate;

    @Column("Pharm_RX_ID")
    private String pharmRxId;

    @Column("End_Date_Future")
    private Date endDateFuture;

    @Column("Dispensing_Pharmacy_Code")
    private Long dispensingPharmacyId;

    @Column("Refill_date")
    private Date refillDate;

    @Column("Last_Update")
    private String lastUpdate;

    @Column("Stop_Delivery_After_Date")
    private Date stopDeliveryAfterDate;
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }
    public Date getPrescriptionEndDate() {
        return prescriptionEndDate;
    }

    public void setPrescriptionEndDate(Date prescriptionEndDate) {
        this.prescriptionEndDate = prescriptionEndDate;
    }

    public String getPassingTimes() {
        return passingTimes;
    }

    public void setPassingTimes(String passingTimes) {
        this.passingTimes = passingTimes;
    }

    public Long getRouteCcdId() {
        return routeCcdId;
    }

    public void setRouteCcdId(Long routeCcdId) {
        this.routeCcdId = routeCcdId;
    }

    public String getIndicatedFor() {
        return indicatedFor;
    }

    public void setIndicatedFor(String indicatedFor) {
        this.indicatedFor = indicatedFor;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getFacilityInstructionNote() {
        return facilityInstructionNote;
    }

    public void setFacilityInstructionNote(String facilityInstructionNote) {
        this.facilityInstructionNote = facilityInstructionNote;
    }

    public Long getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(Long pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public Long getMedProfessionalId() {
        return medProfessionalId;
    }

    public void setMedProfessionalId(Long medProfessionalId) {
        this.medProfessionalId = medProfessionalId;
    }

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Boolean getAdministerByNurseOnly() {
		return administerByNurseOnly;
	}

	public void setAdministerByNurseOnly(Boolean administerByNurseOnly) {
		this.administerByNurseOnly = administerByNurseOnly;
	}

	public Time getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Time effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Time getPrescriptionEndTime() {
		return prescriptionEndTime;
	}

	public void setPrescriptionEndTime(Time prescriptionEndTime) {
		this.prescriptionEndTime = prescriptionEndTime;
	}

	public String getRecurringTaskData() {
		return recurringTaskData;
	}

	public void setRecurringTaskData(String recurringTaskData) {
		this.recurringTaskData = recurringTaskData;
	}

	public Boolean getPrnScheduled() {
		return prnScheduled;
	}

	public void setPrnScheduled(Boolean prnScheduled) {
		this.prnScheduled = prnScheduled;
	}

    public Date getPharmacyOriginDate() {
        return pharmacyOriginDate;
    }

    public void setPharmacyOriginDate(Date pharmacyOriginDate) {
        this.pharmacyOriginDate = pharmacyOriginDate;
    }

    public String getPharmRxId() {
        return pharmRxId;
    }

    public void setPharmRxId(String pharmRxId) {
        this.pharmRxId = pharmRxId;
    }

    public Date getEndDateFuture() {
        return endDateFuture;
    }

    public void setEndDateFuture(Date endDateFuture) {
        this.endDateFuture = endDateFuture;
    }

    public Long getDispensingPharmacyId() {
        return dispensingPharmacyId;
    }

    public void setDispensingPharmacyId(Long dispensingPharmacyId) {
        this.dispensingPharmacyId = dispensingPharmacyId;
    }

    public Date getRefillDate() {
        return refillDate;
    }

    public void setRefillDate(Date refillDate) {
        this.refillDate = refillDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getStopDeliveryAfterDate() {
        return stopDeliveryAfterDate;
    }

    public void setStopDeliveryAfterDate(Date stopDeliveryAfterDate) {
        this.stopDeliveryAfterDate = stopDeliveryAfterDate;
    }
}
