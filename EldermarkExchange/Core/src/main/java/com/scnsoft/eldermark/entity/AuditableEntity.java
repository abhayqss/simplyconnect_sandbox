package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "archived", nullable = false)
    private Boolean archived;

    @Column(name = "chain_id")
    private Long chainId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuditableEntityStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

//    @JoinColumn(name = "modified_by", referencedColumnName = "id", nullable = false)
//    @ManyToOne(optional = false)
//    private Employee modifiedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public AuditableEntityStatus getStatus() {
        return status;
    }

    public void setStatus(AuditableEntityStatus status) {
        this.status = status;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

//    public Employee getModifiedBy() {
//        return modifiedBy;
//    }
//
//    public void setModifiedBy(Employee modifiedBy) {
//        this.modifiedBy = modifiedBy;
//    }
}
