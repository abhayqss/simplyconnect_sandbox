package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ProfessionalContactData.TABLE_NAME)
public class ProfessionalContactData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "professional_contacts";
    public static final String PROF_CONTACT_ID = "Prof_Contact_ID";

    @Id
    @Column(PROF_CONTACT_ID)
    private long id;

    @Column("Contact_First_Name")
    private String contactFirstName;

    @Column("Contact_Last_Name")
    private String contactLastName;

    @Column("Org_RefSource_ID")
    private Long orgRefSourceId;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public Long getOrgRefSourceId() {
        return orgRefSourceId;
    }

    public void setOrgRefSourceId(Long orgRefSourceId) {
        this.orgRefSourceId = orgRefSourceId;
    }
}
