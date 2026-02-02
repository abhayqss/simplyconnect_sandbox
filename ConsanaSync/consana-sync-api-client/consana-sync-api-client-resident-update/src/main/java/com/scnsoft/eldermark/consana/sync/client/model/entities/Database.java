package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "SourceDatabase")
@Cacheable
public class Database extends BaseReadOnlyEntity {

    @Column(name = "consana_xowning_id")
    private String consanaXOwningId;

    public Database() {
    }

    public Database(Long id, String consanaXOwningId) {
        super(id);
        this.consanaXOwningId = consanaXOwningId;
    }

    public String getConsanaXOwningId() {
        return consanaXOwningId;
    }

    public void setConsanaXOwningId(String consanaXOwningId) {
        this.consanaXOwningId = consanaXOwningId;
    }
}
