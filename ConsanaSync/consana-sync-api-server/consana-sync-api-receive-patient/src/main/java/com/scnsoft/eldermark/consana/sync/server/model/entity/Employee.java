package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"legacy_id", "database_id"})
}, indexes = {
        @Index(name = "IX_employee_database", columnList = "database_id")
})
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
@Data
public class Employee extends StringLegacyIdAwareEntity implements Serializable {

    @Column(name = "login", columnDefinition = "nvarchar")
    private String login;

}
