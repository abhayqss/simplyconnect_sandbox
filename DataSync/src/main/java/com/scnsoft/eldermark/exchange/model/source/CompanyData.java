package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(CompanyData.TABLE_NAME)
public class CompanyData extends IdentifiableSourceEntity<String> {
    public static final String TABLE_NAME = "Companies";
    public static final String CODE = "Code";
    public static final String RES_RESUSCITATE_CCDID = "FieldName_Res_Resuscitate_CCDID";
    public static final String RES_ADVDIR_1_CCDID = "FieldName_Res_AdvDir_1_CCDID";
    public static final String RES_ADVDIR_2_CCDID = "FieldName_Res_AdvDir_2_CCDID";
    public static final String RES_ADVDIR_3_CCDID = "FieldName_Res_AdvDir_3_CCDID";
    public static final String RES_ADVDIR_4_CCDID = "FieldName_Res_AdvDir_4_CCDID";
    public static final String RES_CODESTAT_1_CCDID = "FieldName_Res_CodeStat_1_CCDID";
    public static final String RES_CODESTAT_2_CCDID = "FieldName_Res_CodeStat_2_CCDID";
    public static final String RES_CODESTAT_3_CCDID = "FieldName_Res_CodeStat_3_CCDID";
    public static final String RES_CODESTAT_4_CCDID = "FieldName_Res_CodeStat_4_CCDID";

    @Id
    @Column(CODE)
    private String code;

    @Column("Name")
    private String name;

    @Column("Provider_NPI")
    private String providerNpi;

    @Column("Address_1")
    private String address1;

    @Column("City")
    private String city;

    @Column("State_Abbr")
    private String state;

    @Column("Zip")
    private String zip;

    @Column("Phone_Number")
    private String phoneNumber;

    @Column("Logo_Pict_ID")
    private Long logoPictId;

    @Column("Sales_Region")
    private String salesRegion;

    @Column("Testing_Training_Facility")
    private Boolean testingTrainingFacility;

    @Column("Inactive")
    private Boolean inactive;

    @Column("module_Hie")
    private Boolean moduleHie;

    @Column("Module_Cloud_Storage")
    private Boolean moduleCloudStorage;

    @Column("Height_Measure_Units")
    private String heightMeasureUnit;

    @Column("Weight_Measure_Units")
    private String weightMeasureUnit;

    @Column(RES_RESUSCITATE_CCDID)
    private Long resResuscitateCcdId;
    
    @Column(RES_ADVDIR_1_CCDID)
    private Long resAdvDir1CcdId;
    
    @Column(RES_ADVDIR_2_CCDID)
    private Long resAdvDir2CcdId;
    
    @Column(RES_ADVDIR_3_CCDID)
    private Long resAdvDir3CcdId;
    
    @Column(RES_ADVDIR_4_CCDID)
    private Long resAdvDir4CcdId;
    
    @Column(RES_CODESTAT_1_CCDID)
    private Long resCodeStat1CcdId;
    
    @Column(RES_CODESTAT_2_CCDID)
    private Long resCodeStat2CcdId;
    
    @Column(RES_CODESTAT_3_CCDID)
    private Long resCodeStat3CcdId;
    
    @Column(RES_CODESTAT_4_CCDID)
    private Long resCodeStat4CcdId;
    
    @Override
    public String getId() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null) {
            throw new NullPointerException("code must not be null");
        }
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getProviderNpi() {
        return providerNpi;
    }

    public void setProviderNpi(String providerNpi) {
        this.providerNpi = providerNpi;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getLogoPictId() {
        return logoPictId;
    }

    public void setLogoPictId(Long logoPictId) {
        this.logoPictId = logoPictId;
    }

    public String getSalesRegion() {
        return salesRegion;
    }

    public void setSalesRegion(String salesRegion) {
        this.salesRegion = salesRegion;
    }

    public Boolean getTestingTrainingFacility() {
        return testingTrainingFacility;
    }

    public void setTestingTrainingFacility(Boolean testingTrainingFacility) {
        this.testingTrainingFacility = testingTrainingFacility;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Boolean getModuleHie() {
        return moduleHie;
    }

    public void setModuleHie(Boolean moduleHie) {
        this.moduleHie = moduleHie;
    }

    public Boolean getModuleCloudStorage() {
        return moduleCloudStorage;
    }

    public void setModuleCloudStorage(Boolean moduleCloudStorage) {
        this.moduleCloudStorage = moduleCloudStorage;
    }

    public Long getResResuscitateCcdId() {
		return resResuscitateCcdId;
	}

	public void setResResuscitateCcdId(Long resResuscitateCcdId) {
		this.resResuscitateCcdId = resResuscitateCcdId;
	}

	public Long getResAdvDir1CcdId() {
		return resAdvDir1CcdId;
	}

	public void setResAdvDir1CcdId(Long resAdvDir1CcdId) {
		this.resAdvDir1CcdId = resAdvDir1CcdId;
	}

	public Long getResAdvDir2CcdId() {
		return resAdvDir2CcdId;
	}

	public void setResAdvDir2CcdId(Long resAdvDir2CcdId) {
		this.resAdvDir2CcdId = resAdvDir2CcdId;
	}

	public Long getResAdvDir3CcdId() {
		return resAdvDir3CcdId;
	}

	public void setResAdvDir3CcdId(Long resAdvDir3CcdId) {
		this.resAdvDir3CcdId = resAdvDir3CcdId;
	}

	public Long getResAdvDir4CcdId() {
		return resAdvDir4CcdId;
	}

	public void setResAdvDir4CcdId(Long resAdvDir4CcdId) {
		this.resAdvDir4CcdId = resAdvDir4CcdId;
	}

	public Long getResCodeStat1CcdId() {
		return resCodeStat1CcdId;
	}

	public void setResCodeStat1CcdId(Long resCodeStat1CcdId) {
		this.resCodeStat1CcdId = resCodeStat1CcdId;
	}

	public Long getResCodeStat2CcdId() {
		return resCodeStat2CcdId;
	}

	public void setResCodeStat2CcdId(Long resCodeStat2CcdId) {
		this.resCodeStat2CcdId = resCodeStat2CcdId;
	}

	public Long getResCodeStat3CcdId() {
		return resCodeStat3CcdId;
	}

	public void setResCodeStat3CcdId(Long resCodeStat3CcdId) {
		this.resCodeStat3CcdId = resCodeStat3CcdId;
	}

	public Long getResCodeStat4CcdId() {
		return resCodeStat4CcdId;
	}

	public void setResCodeStat4CcdId(Long resCodeStat4CcdId) {
		this.resCodeStat4CcdId = resCodeStat4CcdId;
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
