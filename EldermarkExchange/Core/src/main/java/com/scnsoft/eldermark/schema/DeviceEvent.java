package com.scnsoft.eldermark.schema;

import javax.xml.bind.annotation.*;

/**
 * <p>Java class for Event complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Event">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Organization" type="{}Organization"/>
 *         &lt;element name="Community" type="{}Community"/>
 *         &lt;element name="Patient" type="{}Patient"/>
 *         &lt;element name="FormAuthor" type="{}FormAuthor"/>
 *         &lt;element name="Manager" type="{}Manager" minOccurs="0"/>
 *         &lt;element name="RN" type="{}RN" minOccurs="0"/>
 *         &lt;element name="EventDetails" type="{}EventDetails"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Event", propOrder = {
        "organization",
        "community",
        "patient",
        "formAuthor",
        "manager",
        "rn",
        "eventDetails"
})
@XmlRootElement
public class DeviceEvent extends BasicEvent {
    @XmlElement(name = "Organization", required = true)
    protected Organization organization;
    @XmlElement(name = "Community", required = true)
    protected Community community;
    @XmlElement(name = "Patient", required = true)
    protected DevicePatient patient;
    @XmlElement(name = "FormAuthor", required = true)
    protected FormAuthor formAuthor;
    @XmlElement(name = "Manager")
    protected Manager manager;
    @XmlElement(name = "RN")
    protected RN rn;
    @XmlElement(name = "EventDetails", required = true)
    protected EventDetails eventDetails;

    /**
     * Gets the value of the organization property.
     *
     * @return
     *     possible object is
     *     {@link Organization }
     *
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     *
     * @param value
     *     allowed object is
     *     {@link Organization }
     *
     */
    public void setOrganization(Organization value) {
        this.organization = value;
    }

    /**
     * Gets the value of the community property.
     *
     * @return
     *     possible object is
     *     {@link Community }
     *
     */
    public Community getCommunity() {
        return community;
    }

    /**
     * Sets the value of the community property.
     *
     * @param value
     *     allowed object is
     *     {@link Community }
     *
     */
    public void setCommunity(Community value) {
        this.community = value;
    }

    /**
     * Gets the value of the patient property.
     *
     * @return
     *     possible object is
     *     {@link DevicePatient }
     *
     */
    public DevicePatient getPatient() {
        return patient;
    }

    /**
     * Sets the value of the patient property.
     *
     * @param value
     *     allowed object is
     *     {@link DevicePatient }
     *
     */
    public void setPatient(DevicePatient value) {
        this.patient = value;
    }

    /**
     * Gets the value of the formAuthor property.
     *
     * @return
     *     possible object is
     *     {@link FormAuthor }
     *
     */
    public FormAuthor getFormAuthor() {
        return formAuthor;
    }

    /**
     * Sets the value of the formAuthor property.
     *
     * @param value
     *     allowed object is
     *     {@link FormAuthor }
     *
     */
    public void setFormAuthor(FormAuthor value) {
        this.formAuthor = value;
    }

    /**
     * Gets the value of the manager property.
     *
     * @return
     *     possible object is
     *     {@link Manager }
     *
     */
    public Manager getManager() {
        return manager;
    }

    /**
     * Sets the value of the manager property.
     *
     * @param value
     *     allowed object is
     *     {@link Manager }
     *
     */
    public void setManager(Manager value) {
        this.manager = value;
    }

    /**
     * Gets the value of the rn property.
     *
     * @return
     *     possible object is
     *     {@link RN }
     *
     */
    public RN getRN() {
        return rn;
    }

    /**
     * Sets the value of the rn property.
     *
     * @param value
     *     allowed object is
     *     {@link RN }
     *
     */
    public void setRN(RN value) {
        this.rn = value;
    }

    /**
     * Gets the value of the eventDetails property.
     *
     * @return
     *     possible object is
     *     {@link EventDetails }
     *
     */
    public EventDetails getEventDetails() {
        return eventDetails;
    }

    /**
     * Sets the value of the eventDetails property.
     *
     * @param value
     *     allowed object is
     *     {@link EventDetails }
     *
     */
    public void setEventDetails(EventDetails value) {
        this.eventDetails = value;
    }
}
