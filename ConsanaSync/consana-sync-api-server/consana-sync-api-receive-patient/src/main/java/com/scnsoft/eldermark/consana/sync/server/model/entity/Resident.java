package com.scnsoft.eldermark.consana.sync.server.model.entity;

import com.scnsoft.eldermark.consana.sync.common.entity.HieConsentPolicyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "resident")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resident extends StringLegacyTableAwareEntity {

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Organization facility;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "admit_date")
    private Instant admitDate;

    @Column(name = "death_date")
    private Instant deathDate;

    @Column(name = "death_indicator")
    private Boolean deathIndicator;

    @ManyToOne
    @JoinColumn
    private CcdCode gender;

    @ManyToOne
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @ManyToOne
    @JoinColumn(name = "ethnic_group_id")
    private CcdCode ethnicGroup;

    @ManyToOne
    @JoinColumn(name = "religion_id")
    private CcdCode religion;

    @Column(name = "created_by_id", insertable = false, updatable = false)
    private Long createdById;

    @Column(name = "ssn", length = 11)
    private String socialSecurity;

    @Column(name = "opt_out")
    private Boolean isOptOut;

    @ManyToOne
    @JoinColumn
    private CcdCode race;

    @Column(name = "citizenship", length = 100)
    private String citizenship;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "medicare_number")
    private String medicareNumber; //MC identifier

    @Column(name = "medicaid_number")
    private String medicaidNumber; //MA identifier

    @Column(name = "medical_record_number")
    private String medicalRecordNumber;//MR identifier

    @Column(name = "member_number")
    private String memberNumber; //MB identifier

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "in_network_insurance_id")
    private InNetworkInsurance inNetworkInsurance;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident")
//    private List<Language> languages;

    @Column(name = "date_created")
    private Instant dateCreated;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Column(name = "intake_date")
    private Instant intakeDate;

    // ============ parse first and middle from given name

    @Column(name = "first_name", length = 150)
    private String firstName;

    @Column(name = "last_name", length = 150)
    private String lastName;

    @Column(name = "middle_name", length = 150)
    private String middleName;

    @Column(name = "consana_xref_id", length = 40)
    private String consanaXrefId;

    @Column(name = "hie_consent_policy_obtained_from")
    private String hieConsentPolicyObtainedFrom;

    @Column(name = "hie_consent_policy_update_datetime")
    private Instant hieConsentPolicyUpdateDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "hie_consent_policy_type", nullable = false)
    private HieConsentPolicyType hieConsentPolicyType;

}
