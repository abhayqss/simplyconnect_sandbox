package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "NotifyHandsetHistory")
@Table(name = "handset_history")
public class HandsetHistory extends Handset {

    @Column(name = "change_time")
    private Timestamp changeTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "chain_id", foreignKey = @ForeignKey(name = "fk_handset_history"))
    private Handset handset;

    public HandsetHistory(){}

    public HandsetHistory(Handset handset, Timestamp changeTime) {
        this.changeTime = changeTime;
        this.handset = handset;
    }

    public Timestamp getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Timestamp changeTime) {
        this.changeTime = changeTime;
    }

    public Handset getHandset() {
        return handset;
    }

    public void setHandset(Handset handset) {
        this.handset = handset;
    }
}
