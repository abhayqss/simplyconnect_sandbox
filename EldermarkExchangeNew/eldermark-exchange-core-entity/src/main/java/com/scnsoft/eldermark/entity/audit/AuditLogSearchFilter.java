package com.scnsoft.eldermark.entity.audit;

import javax.persistence.*;

@Entity
@Table(name = "AuditLogSearchFilter")
public class AuditLogSearchFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "search_value")
    private String searchValue;

    @Column(name = "json")
    private String json;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
