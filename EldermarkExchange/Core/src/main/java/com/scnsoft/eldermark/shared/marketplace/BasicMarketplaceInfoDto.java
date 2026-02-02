package com.scnsoft.eldermark.shared.marketplace;

import java.util.ArrayList;
import java.util.List;

public class BasicMarketplaceInfoDto {
    private Long id = null;
    private String communityName = null;
    private String organizationName = null;
    private List<String> communityTypes = new ArrayList<String>();
    private String address = null;
//    private String phoneNumber = null;
    private LocationDto location = null;
    private Integer markerCount =1;
    private boolean addMarker=true;
    private List<Long> sameAddrIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

    }

    public void initSameAddrIds() {
        sameAddrIds=new ArrayList<Long>();
        sameAddrIds.add(this.id);
    }

    /**
     * Community Name. It contains a single value.
     * @return communityName
     */
    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    /**
     * Organization name. It contains a single value.
     * @return organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public BasicMarketplaceInfoDto addCommunityTypesItem(String communityTypesItem) {
        this.communityTypes.add(communityTypesItem);
        return this;
    }

    public void addSameAddrId(Long id) {
        if (markerCount<6) {
            markerCount++;
        }
//        if (sameAddrIds==null) {
//        }
        sameAddrIds.add(id);
    }

    /**
     * Community Types. It contains a list of values.
     * @return communityTypes
     */
    public List<String> getCommunityTypes() {
        return communityTypes;
    }

    public void setCommunityTypes(List<String> communityTypes) {
        this.communityTypes = communityTypes;
    }

    /**
     * Community address. It contains a single value.
     * @return address
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Location of object.
     * @return location
     */
    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }

    public Integer getMarkerCount() {
        return markerCount;
    }

    public void setMarkerCount(Integer markerCount) {
        this.markerCount = markerCount;
    }

    public boolean isAddMarker() {
        return addMarker;
    }

    public void setAddMarker(boolean addMarker) {
        this.addMarker = addMarker;
    }

    public List<Long> getSameAddrIds() {
        return sameAddrIds;
    }

    public void setSameAddrIds(List<Long> sameAddrIds) {
        this.sameAddrIds = sameAddrIds;
    }
}
