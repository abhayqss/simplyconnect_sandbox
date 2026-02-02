package com.scnsoft.eldermark.consana.sync.server.model.entity;

import com.scnsoft.eldermark.consana.sync.common.entity.HieConsentPolicyType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "OrganizationHieConsentPolicy")
@Data
@NoArgsConstructor
public class OrganizationHieConsentPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "archived", nullable = false)
    private Boolean archived;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization community;

    @Column(name = "organization_id", nullable = false, updatable = false, insertable = false)
    private Long communityId;

    @Column(name = "creator_id", updatable = false, insertable = false)
    private Long creatorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Employee creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private HieConsentPolicyType type;
}
