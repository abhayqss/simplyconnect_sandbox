package com.scnsoft.eldermark.schema;

import javax.xml.bind.annotation.*;

/**
 * <p>Java class for Patient complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Patient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{}PersonName"/>
 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="SSN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Address" type="{}Address" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Gender" type="{}Gender"/>
 *         &lt;element name="MaritalStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Patient", propOrder = {
        "deviceId"
})
public class DevicePatient {

    @XmlElement(name = "DeviceID")
    protected String deviceId;

    /**
     * Gets the value of the deviceId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the value of the deviceId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
