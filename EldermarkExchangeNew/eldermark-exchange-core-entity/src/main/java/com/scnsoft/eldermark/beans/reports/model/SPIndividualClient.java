package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class SPIndividualClient {

    private String name;

    private Long id;

    private List<SPIndividualDomain> spIndividualDomains;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SPIndividualDomain> getSpIndividualDomains() {
        return spIndividualDomains;
    }

    public void setSpIndividualDomains(List<SPIndividualDomain> spIndividualDomains) {
        this.spIndividualDomains = spIndividualDomains;
    }
}
