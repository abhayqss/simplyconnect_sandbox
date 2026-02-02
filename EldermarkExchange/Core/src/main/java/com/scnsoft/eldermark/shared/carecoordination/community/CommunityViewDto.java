package com.scnsoft.eldermark.shared.carecoordination.community;

import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.SimpleDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pzhurba on 27-Oct-15.
 */
public class CommunityViewDto implements Serializable {
    private Long id;
    private String name;
    private String datasourceName;
    private AddressDto address;
    private String telecom;
    private String email;
    private String phone;
    private String oid;
    private String mainLogoPath;
//    private String affiliatedCommunities;
    private String affiliatedForCommunities;
    private Boolean hasAffiliated = false;
//    private Boolean affiliatedView = false;
    private Boolean copySettings = false;

    private List<SimpleDto> affiliatedCommunities;
    private List<SimpleDto> affiliatedDatabases;
    private List<SimpleDto> initialCommunities;
    private List<SimpleDto> initialDatabases;

    private MarketplaceDto marketplace = new MarketplaceDto();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getTelecom() {
        return telecom;
    }

    public void setTelecom(String telecom) {
        this.telecom = telecom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getMainLogoPath() {
        return mainLogoPath;
    }

    public void setMainLogoPath(String mainLogoPath) {
        this.mainLogoPath = mainLogoPath;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
//
//    public String getAffiliatedCommunities() {
//        return affiliatedCommunities;
//    }
//
//    public void setAffiliatedCommunities(String affiliatedCommunities) {
//        this.affiliatedCommunities = affiliatedCommunities;
//    }

    public String getAffiliatedForCommunities() {
        return affiliatedForCommunities;
    }

    public void setAffiliatedForCommunities(String affiliatedForCommunities) {
        this.affiliatedForCommunities = affiliatedForCommunities;
    }

    public Boolean getHasAffiliated() {
        return hasAffiliated;
    }

    public void setHasAffiliated(Boolean hasAffiliated) {
        this.hasAffiliated = hasAffiliated;
    }

//    public Boolean getAffiliatedView() {
//        return affiliatedView;
//    }
//
//    public void setAffiliatedView(Boolean affiliatedView) {
//        this.affiliatedView = affiliatedView;
//    }

    public List<SimpleDto> getAffiliatedCommunities() {
        return affiliatedCommunities;
    }

    public void setAffiliatedCommunities(List<SimpleDto> affiliatedCommunities) {
        this.affiliatedCommunities = affiliatedCommunities;
    }

    public List<SimpleDto> getAffiliatedDatabases() {
        return affiliatedDatabases;
    }

    public void setAffiliatedDatabases(List<SimpleDto> affiliatedDatabases) {
        this.affiliatedDatabases = affiliatedDatabases;
    }

    public List<SimpleDto> getInitialCommunities() {
        return initialCommunities;
    }

    public void setInitialCommunities(List<SimpleDto> initialCommunities) {
        this.initialCommunities = initialCommunities;
    }

    public List<SimpleDto> getInitialDatabases() {
        return initialDatabases;
    }

    public void setInitialDatabases(List<SimpleDto> initialDatabases) {
        this.initialDatabases = initialDatabases;
    }

    public Boolean getCopySettings() {
        return copySettings;
    }

    public void setCopySettings(Boolean copySettings) {
        this.copySettings = copySettings;
    }

    public MarketplaceDto getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(MarketplaceDto marketplace) {
        this.marketplace = marketplace;
    }
}
