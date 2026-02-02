package com.scnsoft.eldermark.web.security;

import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.web.entity.Token;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by averazub on 1/3/2017.
 */
public class PhrSecurityUtils {
    public static Token getUserDetails() {
        return (Token) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    public static Long getCurrentUserId() {
        return getUserDetails().getUserId();
    }

    /**
     * Check if user is trying to access his own data (self access).
     *
     * @param userId User ID
     * @return true, if current User ID is equal to the provided User ID
     */
    public static boolean checkAccessToUserInfo(Long userId) {
        if (userId==null) return false;
        Long currentUserId = getCurrentUserId();
        return userId.equals(currentUserId);
    }

    public static void checkAccessToUserInfoOrThrow(Long userId) {
        if (!checkAccessToUserInfo(userId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

}
