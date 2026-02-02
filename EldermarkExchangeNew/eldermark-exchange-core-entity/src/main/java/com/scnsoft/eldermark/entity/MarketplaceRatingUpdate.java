package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "MarketplaceRatingUpdate")
public class MarketplaceRatingUpdate {

    @Id
    @Column(name = "modified_date", columnDefinition = "datetime2")
    private LocalDate modifiedDate;

    public MarketplaceRatingUpdate() {
    }

    public MarketplaceRatingUpdate(LocalDate modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public LocalDate getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDate modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
