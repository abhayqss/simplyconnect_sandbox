package com.scnsoft.eldermark.shared.palatiumcare.resident;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;
import com.scnsoft.eldermark.shared.palatiumcare.location.PalCareLocationOutDto;

public class PalCareResidentOutDto {

    private Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long changeId;

    private String firstName;

    private String lastName;

    private PalCareLocationOutDto location;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EntityAction entityAction;

    public PalCareResidentOutDto() {}

    public PalCareResidentOutDto(Long id,  Long changeId, String firstName, String lastName,
                                 PalCareLocationOutDto location, EntityAction entityAction) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.entityAction = entityAction;
    }

    public PalCareResidentOutDto(Long id, String firstName, String lastName,
                                 PalCareLocationOutDto location) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
    }


    public PalCareResidentOutDto(Long id,  Long changeId, String firstName, String lastName,
                                 PalCareLocationOutDto location) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChangeId() {
        return changeId;
    }

    public void setChangeId(Long changeId) {
        this.changeId = changeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PalCareLocationOutDto getLocation() {
        return location;
    }

    public void setLocation(PalCareLocationOutDto location) {
        this.location = location;
    }

    public EntityAction getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(EntityAction entityAction) {
        this.entityAction = entityAction;
    }

    @Override
    public String toString() {
        return "PalCareResidentOutDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", location=" + location +
                ", entityAction=" + entityAction +
                '}';
    }

}
