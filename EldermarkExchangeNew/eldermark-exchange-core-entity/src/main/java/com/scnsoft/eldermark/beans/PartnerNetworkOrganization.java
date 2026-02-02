package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity_;

import javax.persistence.Tuple;
import java.util.List;

public class PartnerNetworkOrganization extends DisplayableNamedEntity {

    public PartnerNetworkOrganization(Long id, String displayName) {
        setId(id);
        setDisplayName(displayName);
    }

    public PartnerNetworkOrganization(Tuple tuple) {
        this(tuple.get(DisplayableNamedEntity_.ID, Long.class),
                tuple.get(DisplayableNamedEntity_.DISPLAY_NAME, String.class));
    }

    private List<DisplayableNamedEntity> communities;

    public List<DisplayableNamedEntity> getCommunities() {
        return communities;
    }

    public void setCommunities(List<DisplayableNamedEntity> communities) {
        this.communities = communities;
    }
}
