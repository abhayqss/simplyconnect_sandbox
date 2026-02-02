package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(SecurityGroupData.TABLE_NAME)
public class SecurityGroupData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Security_Group";
    public static final String ID = "Security_Group_Id";

    @Id
    @Column(ID)
    private long id;

    @Column("Sec_ProcessesFeatures")
    private String secProcessFeatures;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSecProcessFeatures() {
        return secProcessFeatures;
    }

    public void setSecProcessFeatures(String secProcessFeatures) {
        this.secProcessFeatures = secProcessFeatures;
    }
}
