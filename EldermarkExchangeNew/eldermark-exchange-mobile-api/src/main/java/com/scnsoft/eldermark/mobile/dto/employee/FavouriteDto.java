package com.scnsoft.eldermark.mobile.dto.employee;

import javax.validation.constraints.NotNull;

public class FavouriteDto {

    @NotNull
    private Boolean favourite;

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}
