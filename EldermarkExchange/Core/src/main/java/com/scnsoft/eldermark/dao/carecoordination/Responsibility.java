package com.scnsoft.eldermark.dao.carecoordination;

/**
 * Event Notification receiver's Responsibility.
 *
 * @author knetkachou
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 28-Sep-15.
 */
public enum Responsibility {
    R("Responsible", false, false),
    /**
     * Responsibility: Accountable (also approver or final approving authority)
     */
    A("Accountable", true, true),
    C("Consulted", true, true),
    I("Informed", true, true),
    /**
     * Responsibility: Viewable.<br/>
     * Event Notification is created and persisted, but not sent to a contact.
     */
    V("Viewable", true, true),
    /**
     * Responsibility: Not Viewable.<br/>
     * Event Notification is nor created, not sent to a contact.
     */
    N("Not Viewable", false, true);

    private final String description;
    private final boolean changeable;
    private final boolean assignable;

    Responsibility(final String description, final boolean changeable, boolean assignable) {
        this.description = description;
        this.changeable = changeable;
        this.assignable = assignable;
    }

    public String getDescription() {
        return description;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public boolean isAssignable() {
        return assignable;
    }
}
