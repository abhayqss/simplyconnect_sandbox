
package com.scnsoft.eldermark.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for EventDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="Time" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *         &lt;element name="ResultedInInjury" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Type" type="{}EventType"/>
 *         &lt;element name="SituationNarrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BackgroundNarrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AssessmentNarrative" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FollowUp" type="{}FollowUp"/>
 *         &lt;element name="TreatingPhysician" type="{}TreatingPhysician" minOccurs="0"/>
 *         &lt;element name="TreatingHospital" type="{}TreatingHospital" minOccurs="0"/>
 *         &lt;element name="ERVisit" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="OvernightInPatient" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventDetails", propOrder = {
    "date",
    "time",
    "resultedInInjury",
    "location",
    "type",
    "situationNarrative",
    "backgroundNarrative",
    "assessmentNarrative",
    "followUp",
    "treatingPhysician",
    "treatingHospital",
    "erVisit",
    "overnightInPatient",
    "auxiliaryInfo"
})
public class EventDetails {

    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "Time")
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar time;
    @XmlElement(name = "ResultedInInjury")
    protected Boolean resultedInInjury;
    @XmlElement(name = "Location")
    protected String location;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "SituationNarrative")
    protected String situationNarrative;
    @XmlElement(name = "BackgroundNarrative")
    protected String backgroundNarrative;
    @XmlElement(name = "AssessmentNarrative")
    protected String assessmentNarrative;
    @XmlElement(name = "FollowUp", required = true)
    protected FollowUp followUp;
    @XmlElement(name = "TreatingPhysician")
    protected TreatingPhysician treatingPhysician;
    @XmlElement(name = "TreatingHospital")
    protected TreatingHospital treatingHospital;
    @XmlElement(name = "ERVisit")
    protected boolean erVisit;
    @XmlElement(name = "OvernightInPatient")
    protected boolean overnightInPatient;
    @XmlElement(name = "AuxiliaryInfo")
    protected String auxiliaryInfo;

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Gets the value of the resultedInInjury property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isResultedInInjury() {
        return resultedInInjury;
    }

    /**
     * Sets the value of the resultedInInjury property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setResultedInInjury(Boolean value) {
        this.resultedInInjury = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the situationNarrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSituationNarrative() {
        return situationNarrative;
    }

    /**
     * Sets the value of the situationNarrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSituationNarrative(String value) {
        this.situationNarrative = value;
    }

    /**
     * Gets the value of the backgroundNarrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackgroundNarrative() {
        return backgroundNarrative;
    }

    /**
     * Sets the value of the backgroundNarrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackgroundNarrative(String value) {
        this.backgroundNarrative = value;
    }

    /**
     * Gets the value of the assessmentNarrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssessmentNarrative() {
        return assessmentNarrative;
    }

    /**
     * Sets the value of the assessmentNarrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssessmentNarrative(String value) {
        this.assessmentNarrative = value;
    }

    /**
     * Gets the value of the followUp property.
     * 
     * @return
     *     possible object is
     *     {@link FollowUp }
     *     
     */
    public FollowUp getFollowUp() {
        return followUp;
    }

    /**
     * Sets the value of the followUp property.
     * 
     * @param value
     *     allowed object is
     *     {@link FollowUp }
     *     
     */
    public void setFollowUp(FollowUp value) {
        this.followUp = value;
    }

    /**
     * Gets the value of the treatingPhysician property.
     * 
     * @return
     *     possible object is
     *     {@link TreatingPhysician }
     *     
     */
    public TreatingPhysician getTreatingPhysician() {
        return treatingPhysician;
    }

    /**
     * Sets the value of the treatingPhysician property.
     * 
     * @param value
     *     allowed object is
     *     {@link TreatingPhysician }
     *     
     */
    public void setTreatingPhysician(TreatingPhysician value) {
        this.treatingPhysician = value;
    }

    /**
     * Gets the value of the treatingHospital property.
     * 
     * @return
     *     possible object is
     *     {@link TreatingHospital }
     *     
     */
    public TreatingHospital getTreatingHospital() {
        return treatingHospital;
    }

    /**
     * Sets the value of the treatingHospital property.
     * 
     * @param value
     *     allowed object is
     *     {@link TreatingHospital }
     *     
     */
    public void setTreatingHospital(TreatingHospital value) {
        this.treatingHospital = value;
    }

    /**
     * Gets the value of the erVisit property.
     * 
     */
    public boolean isERVisit() {
        return erVisit;
    }

    /**
     * Sets the value of the erVisit property.
     * 
     */
    public void setERVisit(boolean value) {
        this.erVisit = value;
    }

    /**
     * Gets the value of the overnightInPatient property.
     * 
     */
    public boolean isOvernightInPatient() {
        return overnightInPatient;
    }

    /**
     * Sets the value of the overnightInPatient property.
     * 
     */
    public void setOvernightInPatient(boolean value) {
        this.overnightInPatient = value;
    }

    /**
     * Gets the value of the auxiliaryInfo property.
     *
     */
    public String getAuxiliaryInfo() {
        return auxiliaryInfo;
    }

    /**
     * Sets the value of the auxiliaryInfo property.
     *
     */
    public void setAuxiliaryInfo(String value) {
        this.auxiliaryInfo = value;
    }
}
