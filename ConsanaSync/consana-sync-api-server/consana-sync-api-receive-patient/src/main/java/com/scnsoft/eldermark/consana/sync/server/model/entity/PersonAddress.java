package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(indexes = {
            @Index(name="PersonId_Index", columnList = "person_id")
    })
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
@Data
@NoArgsConstructor
public class PersonAddress extends StringLegacyTableAwareEntity implements Address {
    @Column(length = 15, name = "use_code")
    private String postalAddressUse;

    @Column(length = 255, name = "street_address", columnDefinition = "nvarchar(255)")
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
    @ToString.Exclude
    private Person person;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonAddress that = (PersonAddress) o;
        return Objects.equals(postalAddressUse, that.postalAddressUse) &&
                Objects.equals(streetAddress, that.streetAddress) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(country, that.country) &&
                Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postalAddressUse, streetAddress, city, state, postalCode, country, person);
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
//            if (database != null) {
//                personAddress.setDatabaseId(database.getId());
//            }
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

}
