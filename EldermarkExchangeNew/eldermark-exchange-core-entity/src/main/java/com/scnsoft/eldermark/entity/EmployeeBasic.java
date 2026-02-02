package com.scnsoft.eldermark.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class EmployeeBasic extends BaseEmployeeSecurityEntity {

    private static final long serialVersionUID = 1L;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "avatar_id")
    private Long avatarId;

    @Column(name = "last_session_datetime")
    private Instant lastSessionDateTime;

    public String getFullName() {
        return Stream.of(getFirstName(), getLastName()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Instant getLastSessionDateTime() {
        return lastSessionDateTime;
    }

    public void setLastSessionDateTime(Instant lastSessionDateTime) {
        this.lastSessionDateTime = lastSessionDateTime;
    }
}
