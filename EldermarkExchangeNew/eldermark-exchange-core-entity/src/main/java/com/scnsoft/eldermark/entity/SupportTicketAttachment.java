package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "SupportTicketAttachment")
public class SupportTicketAttachment {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private SupportTicket ticket;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_name")
    private String fileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SupportTicket getTicket() {
        return ticket;
    }

    public void setTicket(SupportTicket ticket) {
        this.ticket = ticket;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String fileName) {
        this.originalFileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
