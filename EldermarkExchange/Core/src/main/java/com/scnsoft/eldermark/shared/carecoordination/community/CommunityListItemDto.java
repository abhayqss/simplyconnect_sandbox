package com.scnsoft.eldermark.shared.carecoordination.community;

import java.util.Date;

/**
 * Created by pzhurba on 27-Oct-15.
 */
public class CommunityListItemDto {
    private Long id;
    private String name;
    private String oid;
    private Boolean createdAutomatically;
    private Date lastModified;

    public CommunityListItemDto() {
    }

    public CommunityListItemDto(Long id, String name, String oid) {
        this.id = id;
        this.name = name;
        this.oid = oid;
    }

    public CommunityListItemDto(Long id, String name, String oid, Boolean createdAutomatically, Date lastModified) {
        this.id = id;
        this.name = name;
        this.oid = oid;
        this.createdAutomatically = createdAutomatically;
        this.lastModified = lastModified;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

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
}
