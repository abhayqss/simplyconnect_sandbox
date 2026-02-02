package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergy_;

public class AllergyListItemDto {
    private Long id;

    @EntitySort(ClientAllergy_.PRODUCT_TEXT)
    private String substance;
    @EntitySort(ClientAllergy_.COMBINED_REACTION_TEXTS)
    private String reaction;
    @EntitySort(ClientAllergy_.EFFECTIVE_TIME_LOW)
    private Long identifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public Long getIdentifiedDate() {
        return identifiedDate;
    }

    public void setIdentifiedDate(Long identifiedDate) {
        this.identifiedDate = identifiedDate;
    }
}
