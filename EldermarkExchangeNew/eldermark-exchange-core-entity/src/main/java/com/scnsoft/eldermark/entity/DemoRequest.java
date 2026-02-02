package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "DemoRequest")
public class DemoRequest {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Employee author;

    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;

    @Column(name = "demo_title")
    private String demoTitle;

    @Column(name = "created_datetime")
    private Instant createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDemoTitle() {
        return demoTitle;
    }

    public void setDemoTitle(String demoTitle) {
        this.demoTitle = demoTitle;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}
