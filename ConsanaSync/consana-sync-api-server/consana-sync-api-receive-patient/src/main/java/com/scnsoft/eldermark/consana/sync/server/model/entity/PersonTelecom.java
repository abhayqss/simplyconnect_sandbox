package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AttributeOverride(name="legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
@Data
@NoArgsConstructor
public class PersonTelecom extends StringLegacyTableAwareEntity implements Telecom {

    /**
     * The {@code useCode} attribute indicates the type of telecom
     * @see PersonTelecomCode#name
     */
    @Column(length = 15, name = "use_code")
    private String useCode;

    @Column(length = 150, name = "value")
    private String value;

    @Column(name = "value_normalized_hash", insertable = false, updatable = false, columnDefinition = "int)")
    private Long valueHash;

    @Column(length = 150, name = "value_normalized", insertable = false, updatable = false)
    private String valueNormalized;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    /**
     * @see PersonTelecomCode#code
     */
    @Column(name = "sync_qualifier", nullable = false)
    private int syncQualifier;

}
