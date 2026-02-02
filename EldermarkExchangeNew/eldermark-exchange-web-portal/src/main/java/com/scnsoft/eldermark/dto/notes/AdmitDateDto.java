package com.scnsoft.eldermark.dto.notes;

import java.util.List;

public class AdmitDateDto {
    private Long id;
    private Long date;
    private List<Long> takenNoteTypeIds;

    public AdmitDateDto(Long id, Long date, List<Long> takenNoteTypeIds) {
        this.id = id;
        this.date = date;
        this.takenNoteTypeIds = takenNoteTypeIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<Long> getTakenNoteTypeIds() {
        return takenNoteTypeIds;
    }

    public void setTakenNoteTypeIds(List<Long> takenNoteTypeIds) {
        this.takenNoteTypeIds = takenNoteTypeIds;
    }
}
