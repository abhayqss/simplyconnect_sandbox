
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
 *         &lt;element name="Patient" type="{}Patient"/>
 *         &lt;element name="FormAuthor" type="{}FormAuthor"/>
 *         &lt;element name="Manager" type="{}Manager"/>
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
    "patient",
    "formAuthor",
    "manager",
    "rn",
    "eventDetails"
})
@XmlRootElement
public class Event_Old {

    @XmlElement(name = "Patient", required = true)
    protected Patient patient;
    @XmlElement(name = "FormAuthor", required = true)
    protected FormAuthor formAuthor;
    @XmlElement(name = "Manager", required = true)
    protected Manager manager;
    @XmlElement(name = "RN")
    protected RN rn;
    @XmlElement(name = "EventDetails", required = true)
    protected EventDetails eventDetails;

    /**
     * Gets the value of the patient property.
     * 
     * @return
     *     possible object is
     *     {@link Patient }
     *     
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets the value of the patient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Patient }
     *     
     */
    public void setPatient(Patient value) {
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
