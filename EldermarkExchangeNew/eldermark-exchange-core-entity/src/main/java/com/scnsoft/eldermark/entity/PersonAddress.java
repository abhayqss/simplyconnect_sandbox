package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table()
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class PersonAddress extends StringLegacyTableAwareEntity implements Address {
    
    private static final long serialVersionUID = 1L;

    @Column(length = 15, name = "use_code")
    private String postalAddressUse;

    @Column(length = 256, name = "street_address", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String streetAddress;

    @Column(length = 256, name = "city", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String city;

    @Column(length = 100, name = "state")
    private String state;

    @Column(length = 50, name = "postal_code")
    private String postalCode;

    @Column(length = 100, name = "country")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public String getPostalAddressUse() {
        return postalAddressUse;
    }

    public void setPostalAddressUse(String postalAddressUse) {
        this.postalAddressUse = postalAddressUse;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
	}

	public String getFullAddress() {
		return Stream.of(getStreetAddress(), getCity(), getState(), getPostalCode(), getCountry())
				.filter(StringUtils::isNotEmpty).collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonAddress that = (PersonAddress) o;

        if (getPostalAddressUse() != null ? !getPostalAddressUse().equals(that.getPostalAddressUse())
                : that.getPostalAddressUse() != null) {
            return false;
        }
        if (getStreetAddress() != null ? !getStreetAddress().equals(that.getStreetAddress())
                : that.getStreetAddress() != null) {
            return false;
        }
        if (getCity() != null ? !getCity().equals(that.getCity()) : that.getCity() != null) {
            return false;
        }
        if (getState() != null ? !getState().equals(that.getState()) : that.getState() != null) {
            return false;
        }
        if (getPostalCode() != null ? !getPostalCode().equals(that.getPostalCode()) : that.getPostalCode() != null) {
            return false;
        }
        if (getCountry() != null ? !getCountry().equals(that.getCountry()) : that.getCountry() != null) {
            return false;
        }
        return getPerson().equals(that.getPerson());
    }

}
