package com.scnsoft.eldermark.entity.history;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Person_History")
public class PersonHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //updated on db level
    @Column(name = "updated_datetime", insertable = false, updatable = false)
    private Instant updatedDatetime;

    //updated on db level
    @Column(name = "deleted_datetime", insertable = false, updatable = false)
    private Instant deletedDatetime;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Instant updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public Instant getDeletedDatetime() {
        return deletedDatetime;
    }

    public void setDeletedDatetime(Instant deletedDatetime) {
        this.deletedDatetime = deletedDatetime;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}
