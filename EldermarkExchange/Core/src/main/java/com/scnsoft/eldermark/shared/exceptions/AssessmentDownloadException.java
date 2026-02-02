package com.scnsoft.eldermark.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AssessmentDownloadException extends LocalizedException {

    public AssessmentDownloadException() {
        super();
    }

    public AssessmentDownloadException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "assessment.download.failure";
    }
}
