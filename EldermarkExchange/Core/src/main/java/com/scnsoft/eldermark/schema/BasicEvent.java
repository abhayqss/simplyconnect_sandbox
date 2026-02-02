package com.scnsoft.eldermark.schema;

public abstract class BasicEvent {
    /**
     * Gets the value of the organization property.
     *
     * @return
     *     possible object is
     *     {@link Organization }
     *
     */
    public abstract Organization getOrganization();

    /**
     * Gets the value of the community property.
     *
     * @return
     *     possible object is
     *     {@link Community }
     *
     */
    public abstract Community getCommunity();

    /**
     * Gets the value of the formAuthor property.
     *
     * @return
     *     possible object is
     *     {@link FormAuthor }
     *
     */
    public abstract FormAuthor getFormAuthor();

    /**
     * Gets the value of the manager property.
     *
     * @return
     *     possible object is
     *     {@link Manager }
     *
     */
    public abstract Manager getManager();

    /**
     * Gets the value of the rn property.
     *
     * @return
     *     possible object is
     *     {@link RN }
     *
     */
    public abstract RN getRN();

    /**
     * Gets the value of the eventDetails property.
     *
     * @return
     *     possible object is
     *     {@link EventDetails }
     *
     */
    public abstract EventDetails getEventDetails();

}
