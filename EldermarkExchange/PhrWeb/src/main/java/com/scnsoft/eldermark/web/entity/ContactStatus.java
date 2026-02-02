package com.scnsoft.eldermark.web.entity;

/**
 * This enum is used to represent a contact status right after invitation.
 * CREATED is returned for new contacts created during invitation (an invitation has been sent);
 * EXISTING_ACTIVE is returned for existing active contacts (they don't need an invitation);
 * EXISTING_PENDING is returned for existing pending contacts (an invitation has been sent before, but not accepted yet).
 *
 * @author phomal
 * Created on 11/17/2017.
 */
public enum ContactStatus {
    CREATED,
    EXISTING_PENDING,
    EXISTING_ACTIVE
}
