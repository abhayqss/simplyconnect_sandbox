package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedCodedOrderedEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ReferralCategoryGroup")
public class ReferralCategoryGroup extends DisplayableNamedCodedOrderedEntity {

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "group")
    @OrderBy("order asc, displayName asc")
    List<ReferralCategory> categories;

    public List<ReferralCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ReferralCategory> categories) {
        this.categories = categories;
    }
}
