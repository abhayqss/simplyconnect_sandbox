package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "SupportTicket")
public class SupportTicket {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private SupportTicketType type;

    @Column(name = "created_datetime")
    private Instant createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Employee author;

    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;

    @Column(name = "message")
    private String message;

    @Column(name = "author_phone_number")
    private String authorPhoneNumber;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<SupportTicketAttachment> attachments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SupportTicketType getType() {
        return type;
    }

    public void setType(SupportTicketType type) {
        this.type = type;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant creationDate) {
        this.createdDate = creationDate;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorPhoneNumber() {
        return authorPhoneNumber;
    }

    public void setAuthorPhoneNumber(String authorPhoneNumber) {
        this.authorPhoneNumber = authorPhoneNumber;
    }

    public List<SupportTicketAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<SupportTicketAttachment> attachments) {
        this.attachments = attachments;
    }
}
