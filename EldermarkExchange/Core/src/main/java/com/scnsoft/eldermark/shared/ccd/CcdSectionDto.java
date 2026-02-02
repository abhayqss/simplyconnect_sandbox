package com.scnsoft.eldermark.shared.ccd;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class CcdSectionDto {
    @Id
    private Long id;

    @Column(name="resident_id")
    private Long residentId;

//    todo uncomment when all sections are implemented
//    @Column(name = "is_manual")
//    private Boolean isManual;

    @Transient
    private String dataSource;

    @Transient
    private String dataSourceOid;

    @Transient
    private String community;

    @Transient
    private String communityOid;

    @Transient
    private boolean editable;

    @Transient
    private boolean deletable;

    @Transient
    private boolean viewable;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDataSourceOid() {
        return dataSourceOid;
    }

    public void setDataSourceOid(String dataSourceOid) {
        this.dataSourceOid = dataSourceOid;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getCommunityOid() {
        return communityOid;
    }

    public void setCommunityOid(String communityOid) {
        this.communityOid = communityOid;
    }

    public Boolean getManual() {
//        return isManual;
        return false;
    }

//    public void setManual(Boolean manual) {
//        isManual = manual;
//    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isViewable() {
        return viewable;
    }

    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }
}
