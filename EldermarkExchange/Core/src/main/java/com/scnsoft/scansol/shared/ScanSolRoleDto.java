package com.scnsoft.scansol.shared;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Date: 14.05.15
 * Time: 10:00
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolRoleDto {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

