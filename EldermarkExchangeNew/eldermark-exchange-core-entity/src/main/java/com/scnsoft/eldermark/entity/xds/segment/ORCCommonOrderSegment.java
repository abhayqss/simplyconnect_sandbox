package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.EIEntityIdentifier;
import com.scnsoft.eldermark.entity.xds.datatype.PLPatientLocation;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ORC_CommonOrderSegment")
public class ORCCommonOrderSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_control")
    private String orderControl;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "place_order_number_id")
    private EIEntityIdentifier placeOrderNumber;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "filler_order_number_id")
    private EIEntityIdentifier fillerOrderNumber;

    @Column(name = "datetime_of_transaction")
    private Instant datetimeOfTransaction;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "enterer_location_id")
    private PLPatientLocation entererLocation;

    @Column(name = "order_effective_datetime")
    private Instant orderEffectiveDatetime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entering_organization_id")
    private CECodedElement enteringOrganization;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ORC_XCN_ordering_provider",
            joinColumns = @JoinColumn(name = "orc_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "ordering_provider_id", nullable = false)
    )
    private List<XCNExtendedCompositeIdNumberAndNameForPersons> orderingProviders;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ORC_XCN_action_by",
            joinColumns = @JoinColumn(name = "orc_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "action_by_id", nullable = false)
    )
    private List<XCNExtendedCompositeIdNumberAndNameForPersons> actionByList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderControl() {
        return orderControl;
    }

    public void setOrderControl(String orderControl) {
        this.orderControl = orderControl;
    }

    public EIEntityIdentifier getPlaceOrderNumber() {
        return placeOrderNumber;
    }

    public void setPlaceOrderNumber(EIEntityIdentifier placeOrderNumber) {
        this.placeOrderNumber = placeOrderNumber;
    }

    public EIEntityIdentifier getFillerOrderNumber() {
        return fillerOrderNumber;
    }

    public void setFillerOrderNumber(EIEntityIdentifier fillerOrderNumber) {
        this.fillerOrderNumber = fillerOrderNumber;
    }

    public Instant getDatetimeOfTransaction() {
        return datetimeOfTransaction;
    }

    public void setDatetimeOfTransaction(Instant datetimeOfTransaction) {
        this.datetimeOfTransaction = datetimeOfTransaction;
    }

    public PLPatientLocation getEntererLocation() {
        return entererLocation;
    }

    public void setEntererLocation(PLPatientLocation entererLocation) {
        this.entererLocation = entererLocation;
    }

    public Instant getOrderEffectiveDatetime() {
        return orderEffectiveDatetime;
    }

    public void setOrderEffectiveDatetime(Instant orderEffectiveDatetime) {
        this.orderEffectiveDatetime = orderEffectiveDatetime;
    }

    public CECodedElement getEnteringOrganization() {
        return enteringOrganization;
    }

    public void setEnteringOrganization(CECodedElement enteringOrganization) {
        this.enteringOrganization = enteringOrganization;
    }

    public List<XCNExtendedCompositeIdNumberAndNameForPersons> getOrderingProviders() {
        return orderingProviders;
    }

    public void setOrderingProviders(List<XCNExtendedCompositeIdNumberAndNameForPersons> orderingProviders) {
        this.orderingProviders = orderingProviders;
    }

    public List<XCNExtendedCompositeIdNumberAndNameForPersons> getActionByList() {
        return actionByList;
    }

    public void setActionByList(List<XCNExtendedCompositeIdNumberAndNameForPersons> actionByList) {
        this.actionByList = actionByList;
    }
}
