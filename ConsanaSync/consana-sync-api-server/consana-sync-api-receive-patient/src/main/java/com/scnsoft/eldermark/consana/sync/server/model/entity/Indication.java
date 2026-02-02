package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Indication")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Indication extends LongLegacyTableAwareEntity {

    @ManyToOne
    @JoinColumn(name = "value_code_id")
    private CcdCode value;
}
