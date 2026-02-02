package com.scnsoft.eldermark.entity.xds.datatype;

import com.scnsoft.eldermark.entity.xds.segment.OBXObservationResult;

import javax.persistence.*;

@Entity
@Table(name = "OBX_Observation_Result_value")
public class OBXValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "obx_id", nullable = false)
    private OBXObservationResult obx;

    @Column(name = "obsv_value")
    private String obsvValue;

    public OBXValue() {
    }

    public OBXValue(OBXObservationResult obx, String obsvValue) {
        this.obx = obx;
        this.obsvValue = obsvValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OBXObservationResult getObx() {
        return obx;
    }

    public void setObx(OBXObservationResult obx) {
        this.obx = obx;
    }

    public String getObsvValue() {
        return obsvValue;
    }

    public void setObsvValue(String obsvValue) {
        this.obsvValue = obsvValue;
    }
}

