package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "MarketplacePartnerNetwork")
//todo use PartnerNetworkCommunity instead
public class MarketplacePartnerNetwork {

    @Id
    @Column(name = "partner_network_id")
    private long partnerNetworkId;

    @ManyToOne
    @JoinColumn(name = "marketplace_id")
    private Marketplace marketplace;

    public long getPartnerNetworkId() {
        return partnerNetworkId;
    }

    public void setPartnerNetworkId(long partnerNetworkId) {
        this.partnerNetworkId = partnerNetworkId;
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }
}
