package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Person extends StringLegacyTableAwareEntity {

    @ManyToOne
    @JoinColumn(name = "type_code_id")
    private CcdCode code;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    private List<Name> names;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    private List<PersonAddress> addresses;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    private List<PersonTelecom> telecoms;

}
