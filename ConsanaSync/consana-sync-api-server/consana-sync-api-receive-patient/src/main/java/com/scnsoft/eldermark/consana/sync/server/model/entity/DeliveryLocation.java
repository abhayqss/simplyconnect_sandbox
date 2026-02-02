package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DeliveryLocation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLocation extends BasicEntity {

    @Column
    private String name;

    @Column
    private String description;
}
