package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.document.CcdCode;

import javax.persistence.*;

@Entity
@Table(name = "NationalDrugCode")
public class NationalDrugCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "national_drug_code", nullable = false)
    private String nationalDrugCode;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "dataset_version", nullable = false)
    private String datasetVersion;

    @ManyToOne
    @JoinColumn(name = "rxnorm_ccd_code_id")
    private CcdCode rxNormCcdCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNationalDrugCode() {
        return nationalDrugCode;
    }

    public void setNationalDrugCode(String nationalDrugCode) {
        this.nationalDrugCode = nationalDrugCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatasetVersion() {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion) {
        this.datasetVersion = datasetVersion;
    }

    public CcdCode getRxNormCcdCode() {
        return rxNormCcdCode;
    }

    public void setRxNormCcdCode(CcdCode rxNormCcdCode) {
        this.rxNormCcdCode = rxNormCcdCode;
    }
}
