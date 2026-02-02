package com.scnsoft.eldermark.dto.event;

import com.scnsoft.eldermark.dto.AddressDto;

import java.util.List;

public interface ClientSummaryViewData {
    String getFullName();

    void setFullName(String fullName);

    List<String> getAliases();

    void setAliases(List<String> aliases);

    List<String> getIdentifiers();

    void setIdentifiers(List<String> identifiers);

    String getSsn();

    void setSsn(String ssn);

    String getBirthDate();

    void setBirthDate(String birthDate);

    String getGender();

    void setGender(String gender);

    String getMaritalStatus();

    void setMaritalStatus(String maritalStatus);

    String getPrimaryLanguage();

    void setPrimaryLanguage(String primaryLanguage);

    String getClientAccountNumber();

    void setClientAccountNumber(String clientAccountNumber);

    String getRace();

    void setRace(String race);

    String getEthnicGroup();

    void setEthnicGroup(String ethnicGroup);

    String getNationality();

    void setNationality(String nationality);

    String getReligion();

    void setReligion(String religion);

    List<String> getCitizenships();

    void setCitizenships(List<String> citizenships);

    String getVeteranStatus();

    void setVeteranStatus(String veteranStatus);

    String getHomePhone();

    void setHomePhone(String homePhone);

    String getBusinessPhone();

    void setBusinessPhone(String businessPhone);

    AddressDto getAddress();

    void setAddress(AddressDto address);

    String getOrganizationTitle();

    void setOrganizationTitle(String organizationTitle);

    String getCommunityTitle();

    void setCommunityTitle(String communityTitle);

    Long getDeathDate();

    void setDeathDate(Long deathDate);

    Boolean getIsActive();

    void setIsActive(Boolean isActive);

    Boolean getActive();

    void setActive(Boolean active);

    String getMaidenName();

    void setMaidenName(String maidenName);

    List<String> getLanguages();

    void setLanguages(List<String> languages);

    String getPreferredName();

    void setPreferredName(String preferredName);

    String getPrefix();

    void setPrefix(String prefix);
}
