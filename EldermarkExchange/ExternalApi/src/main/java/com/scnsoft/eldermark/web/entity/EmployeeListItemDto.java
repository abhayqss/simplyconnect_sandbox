package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T13:55:39.161+03:00")
public class EmployeeListItemDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;


    /**
     * employee id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "employee id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * First name
     *
     * @return firstName
     */
    @Size(max = 128)
    @ApiModelProperty(example = "Donald", value = "First name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Last name
     *
     * @return lastName
     */
    @ApiModelProperty(example = "Duck", value = "Last name")
    @Size(max = 128)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
