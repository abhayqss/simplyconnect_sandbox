package com.scnsoft.eldermark.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

/**
 * This DTO is indended to represent US states
 */
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-06T14:30:19.122+03:00")
public class StateDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("abbr")
    private String abbr = null;


    /**
     * state id
     * minimum: 1
     *
     * @return id
     */
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

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

}

