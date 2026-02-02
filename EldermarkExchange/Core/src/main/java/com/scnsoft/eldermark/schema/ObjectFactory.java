
package com.scnsoft.eldermark.schema;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.scnsoft.eldermark.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.scnsoft.eldermark.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Events }
     * 
     */
    public Events createEvents() {
        return new Events();
    }

    /**
     * Create an instance of {@link Event }
     * 
     */
    public Event createEvent() {
        return new Event();
    }

    /**
     * Create an instance of {@link FormAuthor }
     * 
     */
    public FormAuthor createFormAuthor() {
        return new FormAuthor();
    }

    /**
     * Create an instance of {@link PersonName }
     * 
     */
    public PersonName createPersonName() {
        return new PersonName();
    }

    /**
     * Create an instance of {@link RN }
     * 
     */
    public RN createRN() {
        return new RN();
    }

    /**
     * Create an instance of {@link TreatingPhysician }
     * 
     */
    public TreatingPhysician createTreatingPhysician() {
        return new TreatingPhysician();
    }

    /**
     * Create an instance of {@link FollowUp }
     * 
     */
    public FollowUp createFollowUp() {
        return new FollowUp();
    }

    /**
     * Create an instance of {@link EventDetails }
     * 
     */
    public EventDetails createEventDetails() {
        return new EventDetails();
    }

    /**
     * Create an instance of {@link Manager }
     * 
     */
    public Manager createManager() {
        return new Manager();
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link Patient }
     * 
     */
    public Patient createPatient() {
        return new Patient();
    }

    /**
     * Create an instance of {@link Person }
     * 
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link TreatingHospital }
     * 
     */
    public TreatingHospital createTreatingHospital() {
        return new TreatingHospital();
    }

}
