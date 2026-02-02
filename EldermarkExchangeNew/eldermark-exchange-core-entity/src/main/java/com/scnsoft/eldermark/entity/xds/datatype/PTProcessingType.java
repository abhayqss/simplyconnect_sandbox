package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.*;

@Entity
@Table(name = "PT_ProcessingType")
public class PTProcessingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "processing_id")
    private String processingId;

    @Column(name = "processing_mode")
    private String processingMode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessingId() {
        return processingId;
    }

    public void setProcessingId(String processingId) {
        this.processingId = processingId;
    }

    public String getProcessingMode() {
        return processingMode;
    }

    public void setProcessingMode(String processingMode) {
        this.processingMode = processingMode;
    }
}
