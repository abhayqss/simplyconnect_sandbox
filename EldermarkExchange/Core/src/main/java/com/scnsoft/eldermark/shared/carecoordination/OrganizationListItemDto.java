package com.scnsoft.eldermark.shared.carecoordination;

import java.util.Date;
import java.util.List;

/**
 * Created by averazub on 3/21/2016.
 */
public class OrganizationListItemDto {
    private Long id;
    private String name;
    private Integer communityCount;
    private Boolean createdAutomatically;
    private Date lastModified;
    private Integer affilatedCount;

    private List<AffiliatedOrgItemDto> affiliatedOrgItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCommunityCount() {
        return communityCount;
    }

    public void setCommunityCount(Integer communityCount) {
        this.communityCount = communityCount;
    }

    public Boolean getCreatedAutomatically() {
        return createdAutomatically;
    }

    public void setCreatedAutomatically(Boolean createdAutomatically) {
        this.createdAutomatically = createdAutomatically;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getAffilatedCount() {
        return affilatedCount;
    }

    public void setAffilatedCount(Integer affilatedCount) {
        this.affilatedCount = affilatedCount;
    }

    public List<AffiliatedOrgItemDto> getAffiliatedOrgItems() {
        return affiliatedOrgItems;
    }

    public void setAffiliatedOrgItems(List<AffiliatedOrgItemDto> affiliatedOrgItems) {
        this.affiliatedOrgItems = affiliatedOrgItems;
    }
}
