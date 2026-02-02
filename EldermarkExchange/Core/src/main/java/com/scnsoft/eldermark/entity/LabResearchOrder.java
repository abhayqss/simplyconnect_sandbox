package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "LabResearchOrder")
public class LabResearchOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LabResearchOrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "labResearchOrder")
    private List<Document> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LabResearchOrderStatus getStatus() {
        return status;
    }

    public void setStatus(LabResearchOrderStatus status) {
        this.status = status;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
