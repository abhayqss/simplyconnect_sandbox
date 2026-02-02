package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FacilityNotFoundException extends LocalizedException {
    public FacilityNotFoundException () {
    }

    public FacilityNotFoundException(Object ...params) {
        super(params);
    }

    public FacilityNotFoundException (Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "error.facility.not.found";
    }
}
