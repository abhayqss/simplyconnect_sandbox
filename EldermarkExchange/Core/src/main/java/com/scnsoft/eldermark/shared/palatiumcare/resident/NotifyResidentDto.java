package com.scnsoft.eldermark.shared.palatiumcare.resident;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;

public class NotifyResidentDto {

    private Long id;

    private String firstName;

    private String lastName;

    private NotifyLocationDto location;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
