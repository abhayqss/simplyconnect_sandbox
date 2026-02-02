package com.scnsoft.eldermark.mobile.request;

/**
 * Use if there are API changes for backward mobile app versions compatibility
 */
public interface MobileRequestContextProvider {

    MobileRequestContext getRequestContext();

    void setRequestContext(MobileRequestContext context);
}
