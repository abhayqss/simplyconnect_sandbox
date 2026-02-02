package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "name")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Name extends StringLegacyTableAwareEntity implements LegacyTableAwareEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @ToString.Exclude
    private Person person;

    @Column(name = "use_code")
    private String nameUse;

    @Column(name = "prefix", columnDefinition = "nvarchar(50)")
    private String prefix;

    @Column(name = "given", columnDefinition = "nvarchar(100)")
    private String given;

    @Column(name = "given_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long givenHash;

    @Column(name = "given_normalized", columnDefinition = "nvarchar(100)", insertable = false, updatable = false)
    private String givenNormalized;

    @Column(name = "middle", columnDefinition = "nvarchar(100)")
    private String middle;

    @Column(name = "middle_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long middleHash;

    @Column(name = "middle_normalized", columnDefinition = "nvarchar(100)", insertable = false, updatable = false)
    private String middleNormalized;

    @Column(name = "family", columnDefinition = "nvarchar(100)")
    private String family;

    @Column(name = "family_hash", insertable = false, updatable = false, columnDefinition = "int")
    private Long familyHash;

    @Column(name = "family_normalized", columnDefinition = "nvarchar(100)", insertable = false, updatable = false)
    private String familyNormalized;

    @Column(name = "suffix", columnDefinition = "nvarchar(50)")
    private String suffix;

    @Column(length=30, name = "prefix_qualifier")
    private String prefixQualifier;

    @Column(length=30, name = "given_qualifier")
    private String givenQualifier;

    @Column(length=30, name = "middle_qualifier")
    private String middleQualifier;

    @Column(length=30, name = "family_qualifier")
    private String familyQualifier;

    @Column(length=30, name = "suffix_qualifier")
    private String suffixQualifier;

    @Column(length=35, name = "call_me")
    private String preferredName;

    @Column(length = 100, name = "degree")
    private String degree;

    @Column(name = "full_name")
    private String fullName;

}
