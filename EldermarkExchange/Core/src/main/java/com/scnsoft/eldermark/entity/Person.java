package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;


@Entity
/*
@Cacheable
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE, region="person")
*/
@AttributeOverride(name="legacyTable", column = @Column(name = "legacy_table", nullable = false, length = 25))
public class Person extends StringLegacyTableAwareEntity {
    @ManyToOne
    @JoinColumn(name = "type_code_id")
    private CcdCode code;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "person")
    private List<Name> names;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "person")
    private List<PersonAddress> addresses;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "person")
    private List<PersonTelecom> telecoms;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public List<PersonAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PersonAddress> addresses) {
        this.addresses = addresses;
    }

    public List<PersonTelecom> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<PersonTelecom> telecoms) {
        this.telecoms = telecoms;
    }

    public static final class Builder {
        private String legacyId;
        private String legacyTable;
        private Long id;
        private CcdCode code;
        private Database database;
        private List<Name> names;
        private List<PersonAddress> addresses;
        private List<PersonTelecom> telecoms;

        private Builder() {
        }

        public static Builder aPerson() {
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

        public Builder withCode(CcdCode code) {
            this.code = code;
            return this;
        }

        public Builder withDatabase(Database database) {
            this.database = database;
            return this;
        }

        public Builder withNames(List<Name> names) {
            this.names = names;
            return this;
        }

        public Builder withAddresses(List<PersonAddress> addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder withTelecoms(List<PersonTelecom> telecoms) {
            this.telecoms = telecoms;
            return this;
        }

        public Person build() {
            Person person = new Person();
            person.setLegacyId(legacyId);
            person.setLegacyTable(legacyTable);
            person.setId(id);
            person.setCode(code);
            person.setDatabase(database);
            if (database != null) {
                person.setDatabaseId(database.getId());
            }
            person.setNames(names);
            person.setAddresses(addresses);
            person.setTelecoms(telecoms);
            return person;
        }
    }

}
