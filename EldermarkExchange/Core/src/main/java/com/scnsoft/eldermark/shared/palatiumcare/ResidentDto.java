package com.scnsoft.eldermark.shared.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;

public class ResidentDto {

    private String id;

    private String firstName;

    private String lastName;

    private NotifyLocationDto location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public NotifyLocationDto getLocation() {
        return location;
    }

    public void setLocation(NotifyLocationDto location) {
        this.location = location;
    }
}
