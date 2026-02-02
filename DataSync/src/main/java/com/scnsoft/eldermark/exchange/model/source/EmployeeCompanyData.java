package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(EmployeeCompanyData.TABLE_NAME)
public class EmployeeCompanyData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "employee_companies";
    public static final String UNIQUE_ID = "Unique_ID";
    public static final String SEC_GROUP_IDS = "Security_group_IDs";

    @Id
    @Column(UNIQUE_ID)
    private long id;

    @Column("Employee_ID")
    private String employeeId;

    @Column("Company")
    private String company;

    @Column("Access_Marketing")
    private Boolean accessMarketing;

    @Column("Sec_ProcessesFeatures")
    private String secProcessFeatures;

    @Column(SEC_GROUP_IDS)
    private String secGroupIds;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean getAccessMarketing() {
        return accessMarketing;
    }

    public void setAccessMarketing(Boolean accessMarketing) {
        this.accessMarketing = accessMarketing;
    }

    public String getSecProcessFeatures() {
        return secProcessFeatures;
    }

    public void setSecProcessFeatures(String secProcessFeatures) {
        this.secProcessFeatures = secProcessFeatures;
    }

    public String getSecGroupIds() {
        return secGroupIds;
    }

    public void setSecGroupIds(String secGroupIds) {
        this.secGroupIds = secGroupIds;
    }
}
