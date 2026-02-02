package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "MedicationInformation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationInformation extends LongLegacyIdAwareEntity{

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    @Lob
    @Column(name = "product_name_text")
    private String productNameText;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "product_name_code_id")
    private CcdCode productNameCode;

}
