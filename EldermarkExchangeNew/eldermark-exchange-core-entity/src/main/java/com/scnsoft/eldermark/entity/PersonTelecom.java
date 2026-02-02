package com.scnsoft.eldermark.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.basic.Telecom;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"person_id", "sync_qualifier"}),
indexes = {
        @Index(name = "PersonId_Index", columnList = "person_id")
})
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class PersonTelecom extends StringLegacyTableAwareEntity implements Telecom {

	private static final long serialVersionUID = 1L;

	@Column(length = 15, name = "use_code")
	private String useCode;

	@Column(length = 256, name = "value")
	private String value;

	@Column(length = 256, name = "value_normalized", insertable = false, updatable = false)
	private String normalized;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", nullable = false)
	private Person person;

	@Column(name = "sync_qualifier", nullable = false)
	private int syncQualifier;

	public String getUseCode() {
		return useCode;
	}

	public void setUseCode(String useCode) {
		this.useCode = useCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getNormalized() {
		return normalized;
	}

	public void setNormalized(String normalized) {
		this.normalized = normalized;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public int getSyncQualifier() {
		return syncQualifier;
	}

	public void setSyncQualifier(int syncQualifier) {
		this.syncQualifier = syncQualifier;
	}
}