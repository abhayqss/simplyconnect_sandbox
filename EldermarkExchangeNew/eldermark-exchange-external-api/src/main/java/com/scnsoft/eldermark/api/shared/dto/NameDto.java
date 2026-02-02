package com.scnsoft.eldermark.api.shared.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by pzhurba on 05-Oct-15.
 */
public class NameDto {
    private String firstName;
    private String lastName;
    private String middleName;

    @NotNull
    @Size(min = 2, max = 128)
    @ApiModelProperty(value = "First name", example = "Donald", required = true)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotNull
    @Size(min = 2, max = 128)
    @ApiModelProperty(value = "Last name", example = "Duck", required = true)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Display name. May be `null` in POST call when creating a new Event.
     */
    @ApiModelProperty(example = "Donald Duck", value = "Display name. May be `null` in POST call when creating a new Event.")
    public String getDisplayName(){
        return (getFirstName()==null?"":getFirstName()) + " " + (getLastName()==null?"":getLastName()) ;
    }
    public void setDisplayName(final String displayName){}

    @ApiModelProperty(value = "Middle name. Nullable.")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
}
