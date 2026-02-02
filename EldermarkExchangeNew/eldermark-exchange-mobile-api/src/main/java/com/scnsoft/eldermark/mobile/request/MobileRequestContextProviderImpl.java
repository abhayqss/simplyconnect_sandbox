package com.scnsoft.eldermark.mobile.request;

import org.springframework.stereotype.Component;

@Component
public class MobileRequestContextProviderImpl implements MobileRequestContextProvider {

    private ThreadLocal<MobileRequestContext> contextThreadLocal = new ThreadLocal<>();

    @Override
    public MobileRequestContext getRequestContext() {
        return contextThreadLocal.get();
    }

    @Override
    public void setRequestContext(MobileRequestContext context) {
        contextThreadLocal.set(context);
    }


}
