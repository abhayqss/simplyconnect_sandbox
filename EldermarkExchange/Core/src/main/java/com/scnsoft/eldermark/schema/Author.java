
package com.scnsoft.eldermark.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Author complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Author">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{}FormAuthorName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Author", propOrder = {
    "name"
})
public class Author {

    @XmlElement(name = "Name", required = true)
    protected FormAuthorName name;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link FormAuthorName }
     *     
     */
    public FormAuthorName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormAuthorName }
     *     
     */
    public void setName(FormAuthorName value) {
        this.name = value;
    }

}
