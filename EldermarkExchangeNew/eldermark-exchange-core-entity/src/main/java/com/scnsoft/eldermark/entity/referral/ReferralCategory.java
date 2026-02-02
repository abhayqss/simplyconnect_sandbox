package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedCodedOrderedEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ReferralCategory")
public class ReferralCategory extends DisplayableNamedCodedOrderedEntity {

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ReferralCategoryGroup group;

    public ReferralCategoryGroup getGroup() {
        return group;
    }

    public void setGroup(ReferralCategoryGroup group) {
        this.group = group;
    }
}
