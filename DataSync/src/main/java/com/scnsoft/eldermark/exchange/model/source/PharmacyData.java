package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(PharmacyData.TABLE_NAME)
public class PharmacyData extends IdentifiableSourceEntity<Long> {
    public static final String TABLE_NAME = "Pharmacy";
    public static final String CODE = "Code";

    @Id
    @Column(CODE)
    private long code;

    @Column("Name")
    private String name;

    @Column("Address1")
    private String streetAddress;

    @Column("City")
    private String city;

    @Column("State")
    private String state;

    @Column("Zip")
    private String zip;

    @Override
    public Long getId() {
        return code;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
