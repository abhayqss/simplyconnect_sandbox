package com.scnsoft.eldermark.entity;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(indexes = {
            @Index(name="PersonId_Index", columnList = "person_id")
    })
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class PersonAddress extends StringLegacyTableAwareEntity implements Address {
    @Column(length = 15, name = "use_code")
    private String postalAddressUse;

    @Column(length = 255, name = "street_address")
    private String streetAddress;

    @Nationalized
    @Column(length = 128, name = "city")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonAddress that = (PersonAddress) o;

        if (getPostalAddressUse() != null ? !getPostalAddressUse().equals(that.getPostalAddressUse()) : that.getPostalAddressUse() != null) {
            return false;
        }
        if (getStreetAddress() != null ? !getStreetAddress().equals(that.getStreetAddress()) : that.getStreetAddress() != null) {
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

    @Override
    public int hashCode() {
        int result = getPostalAddressUse() != null ? getPostalAddressUse().hashCode() : 0;
        result = 31 * result + (getStreetAddress() != null ? getStreetAddress().hashCode() : 0);
        result = 31 * result + (getCity() != null ? getCity().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        result = 31 * result + (getPostalCode() != null ? getPostalCode().hashCode() : 0);
        result = 31 * result + (getCountry() != null ? getCountry().hashCode() : 0);
        result = 31 * result + getPerson().hashCode();
        return result;
    }

    public static final class Builder {
        private String legacyId;
        private String legacyTable;
        private Long id;
        private Database database;
        private String postalAddressUse;
        private String streetAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private Person person;

        private Builder() {
        }

        public static Builder aPersonAddress() {
            return new Builder();
        }

        public Builder withLegacyId(String legacyId) {
            this.legacyId = legacyId;
            return this;
        }

        public Builder withLegacyTable(String legacyTable) {
            this.legacyTable = legacyTable;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withDatabase(Database database) {
            this.database = database;
            return this;
        }

        public Builder withPostalAddressUse(String postalAddressUse) {
            this.postalAddressUse = postalAddressUse;
            return this;
        }

        public Builder withStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withState(String state) {
            this.state = state;
            return this;
        }

        public Builder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder withPerson(Person person) {
            this.person = person;
            return this;
        }

        public PersonAddress build() {
            PersonAddress personAddress = new PersonAddress();
            personAddress.setLegacyId(legacyId);
            personAddress.setLegacyTable(legacyTable);
            personAddress.setId(id);
            personAddress.setDatabase(database);
            if (database != null) {
                personAddress.setDatabaseId(database.getId());
            }
            personAddress.setPostalAddressUse(postalAddressUse);
            personAddress.setStreetAddress(streetAddress);
            personAddress.setCity(city);
            personAddress.setState(state);
            personAddress.setPostalCode(postalCode);
            personAddress.setCountry(country);
            personAddress.setPerson(person);
            return personAddress;
        }
    }
    
	public String getFullAddress() {
		StringBuilder fullAddress = new StringBuilder();
		appendIfNotBlank(fullAddress,this.streetAddress);
		appendIfNotBlank(fullAddress,this.city);
		appendIfNotBlank(fullAddress,this.state);
		appendIfNotBlank(fullAddress,this.country);
		appendIfNotBlank(fullAddress,this.postalCode);
		if (StringUtils.isNotEmpty(fullAddress.toString())) {
			return fullAddress.toString();
		}
		return null;
	}
	
	private void appendIfNotBlank(StringBuilder sb, String value) {
		if(StringUtils.isNotEmpty(value)) {
			if (StringUtils.isNotEmpty(sb.toString())) {
				sb.append(", ");
			}
			sb.append(value);
		}
	}

}
