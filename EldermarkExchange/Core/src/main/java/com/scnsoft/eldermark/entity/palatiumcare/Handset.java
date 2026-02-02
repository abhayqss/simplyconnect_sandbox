package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.*;
import java.util.List;

@Entity(name = "NotifyHandset")
@Table(name = "PalCare_Handset")
public class Handset extends BasicEntity {

    @Column(name = "handset_name", nullable = false)
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "handset_id")
    private String handsetId;

    @OneToMany(mappedBy = "handset", fetch = FetchType.LAZY)
    private List<HandsetHistory> history;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHandsetId() {
        return handsetId;
    }

    public void setHandsetId(String handsetId) {
        this.handsetId = handsetId;
    }

    public List<HandsetHistory> getHistory() {
        return history;
    }

    public void setHistory(List<HandsetHistory> history) {
        this.history = history;
    }


}
