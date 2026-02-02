package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(OrgReferralSourceFacilityData.TABLE_NAME)
public class OrgReferralSourceFacilityData extends IdentifiableSourceEntity<Long> {

	public static final String TABLE_NAME = "org_referralsource_facility";
    public static final String RECORD_ID = "Record_ID";

    @Id
    @Column(RECORD_ID)
    private long id;

    @Column("Facility")
    private String facility;

    @Column("Org_RefSource_ID")
    private Long orgRefSourceId;

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

    public Long getOrgRefSourceId() {
        return orgRefSourceId;
    }

    public void setOrgRefSourceId(Long orgRefSourceId) {
        this.orgRefSourceId = orgRefSourceId;
    }
    
}
