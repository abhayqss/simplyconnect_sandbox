package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(AllergyData.TABLE_NAME)
public class AllergyData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Allergies";
    public static final String ID_COLUMN = "Unique_ID";
    public static final String ALLERGY_CCDID = "Allergy_CCDID";
    public static final String ALLERGY_TYPE_CCDID = "Allergy_Type_CCDID";
    public static final String OBSERVATION_STATUS_CCDID = "Status_CCDID";
    public static final String SEVERITY_CCDID = "Severity_CCDID";

    @Id
    @Column(ID_COLUMN)
    private long id;

    @Column("Facility")
    private String facility;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Allergy")
    private String allergy;

    @Column(ALLERGY_CCDID)
    private Long allergyCcdId;
    
    @Column("Onset_Date")
    private Date onsetDate;
    
    @Column(ALLERGY_TYPE_CCDID)
    private Long allergyTypeCcdId;
    
    @Column("Allergy_Type")
    private String allergyType;
    
    @Column(OBSERVATION_STATUS_CCDID)
    private Long statusCcdId;

    @Column("Reaction")
    private String reaction;
    
    @Column("Severity")
    private String severity;
    
    @Column(SEVERITY_CCDID)
    private Long severityCcdId;
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public Long getAllergyCcdId() {
        return allergyCcdId;
    }

    public void setAllergyCcdId(Long allergyCcdId) {
        this.allergyCcdId = allergyCcdId;
    }

	public Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	public Long getAllergyTypeCcdId() {
		return allergyTypeCcdId;
	}

	public void setAllergyTypeCcdId(Long allergyTypeCcdId) {
		this.allergyTypeCcdId = allergyTypeCcdId;
	}

	public String getAllergyType() {
		return allergyType;
	}

	public void setAllergyType(String allergyType) {
		this.allergyType = allergyType;
	}

	public Long getStatusCcdId() {
		return statusCcdId;
	}

	public void setStatusCcdId(Long statusCcdId) {
		this.statusCcdId = statusCcdId;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Long getSeverityCcdId() {
		return severityCcdId;
	}

	public void setSeverityCcdId(Long severityCcdId) {
		this.severityCcdId = severityCcdId;
	}
    
}