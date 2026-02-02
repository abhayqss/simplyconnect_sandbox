package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public interface CcdEntitySecurityService {

    boolean canView(Long id);

    boolean canViewList();

    /**
     * This method checks that list of requested client's ccd entities can be displayed.
     * <p>
     * Although user might be able to see ccd data of given client's merged record so that requested list is not empty,
     * we want to disable access to ccd entity list through clients with disabled PHR flags
     *
     * @param filter
     * @return true if client == null of whether user can see client's ccd entity by permission and access flags
     * are enabled for this client
     *
     */
    boolean canViewOfClientIfPresent(ClientIdAware filter);
}
