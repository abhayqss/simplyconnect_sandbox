package com.scnsoft.eldermark.web.security;

import com.scnsoft.eldermark.shared.web.entity.Token;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by phomal on 1/18/2018.
 */
public class ApiSecurityUtils {

    public static Token getUserDetails() {
        return (Token) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    public static Long getCurrentUserId() {
        return getUserDetails().getUserId();
    }

}
