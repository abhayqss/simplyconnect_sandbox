package com.scnsoft.eldermark.mobile.dto.medication;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SearchMedicationFilter {

    @Size(min = 1)
    private String name;

    @Pattern(regexp = "\\d{5}-\\d{4}-\\d{2}")
    private String ndcCode;

    private Integer page;
    
    private Integer size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNdcCode() {
        return ndcCode;
    }

    public void setNdcCode(String ndcCode) {
        this.ndcCode = ndcCode;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
