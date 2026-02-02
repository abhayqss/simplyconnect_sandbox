package com.scnsoft.eldermark.exchange.model.source;

import java.sql.Date;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(OrgReferralSourceData.TABLE_NAME)
public class OrgReferralSourceData extends IdentifiableSourceEntity<Long>{
	public static final String TABLE_NAME = "org_referral_source";
    public static final String ORG_REF_SOURCE_ID = "Org_RefSource_ID";
 
    @Id
    @Column(ORG_REF_SOURCE_ID)
    private long id;

    @Column("Name")
    private String name;
    
    @Column("Create_Date")
    private Date createDate;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
