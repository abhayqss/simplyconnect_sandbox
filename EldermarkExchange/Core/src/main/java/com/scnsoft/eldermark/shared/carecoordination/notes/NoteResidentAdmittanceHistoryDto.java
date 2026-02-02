package com.scnsoft.eldermark.shared.carecoordination.notes;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class NoteResidentAdmittanceHistoryDto {

    private Long id;

    @DateTimeFormat(pattern = "MM/dd/yyyy hh:mm a (Z)")
    private Date admitDate;

    public NoteResidentAdmittanceHistoryDto() {
    }

    public NoteResidentAdmittanceHistoryDto(Long id, Date admitDate) {
        this.id = id;
        this.admitDate = admitDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Date admitDate) {
        this.admitDate = admitDate;
    }
}
