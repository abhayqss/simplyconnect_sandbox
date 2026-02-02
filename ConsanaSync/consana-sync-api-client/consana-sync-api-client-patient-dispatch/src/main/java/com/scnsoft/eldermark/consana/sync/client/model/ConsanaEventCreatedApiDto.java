package com.scnsoft.eldermark.consana.sync.client.model;

import java.util.List;

public class ConsanaEventCreatedApiDto {

    private List<ConsanaEntryDto> entry;

    public List<ConsanaEntryDto> getEntry() {
        return entry;
    }

    public void setEntry(List<ConsanaEntryDto> entry) {
        this.entry = entry;
    }
}
