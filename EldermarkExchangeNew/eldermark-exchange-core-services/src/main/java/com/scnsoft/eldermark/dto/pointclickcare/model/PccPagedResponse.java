package com.scnsoft.eldermark.dto.pointclickcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccPagedResponse<T> {

    private List<T> data;
    private PCCPagingResponseByPage paging;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public PCCPagingResponseByPage getPaging() {
        return paging;
    }

    public void setPaging(PCCPagingResponseByPage paging) {
        this.paging = paging;
    }
}
