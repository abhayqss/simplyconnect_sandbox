package com.scnsoft.eldermark.api.external.utils;

import com.scnsoft.eldermark.api.shared.web.dto.Token;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApiSecurityUtils {

    public static Token getUserDetails() {
        return (Token) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    public static Long getCurrentUserId() {
        return getUserDetails().getUserId();
    }

}
