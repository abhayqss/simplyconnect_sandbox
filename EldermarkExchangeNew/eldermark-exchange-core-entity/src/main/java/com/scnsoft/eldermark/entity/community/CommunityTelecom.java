package com.scnsoft.eldermark.entity.community;

import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.basic.Telecom;

import javax.persistence.*;

@Entity
@Table(name = "OrganizationTelecom")
public class CommunityTelecom extends StringLegacyTableAwareEntity implements Telecom {
    private static final long serialVersionUID = 1L;

    @Column(length = 15, name = "use_code")
    private String useCode;

    @Column(length = 256)
    private String value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Community community;

    public String getUseCode() {
        return useCode;
    }

    public void setUseCode(String useCode) {
        this.useCode = useCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

}
