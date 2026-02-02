package com.scnsoft.eldermark.entity.marketplace;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@MappedSuperclass
public class DisplayablePrimaryFocusAwareEntity extends DisplayableNamedKeyEntity implements Serializable{
	
	@Column(name = "primary_focus_id", nullable = false)
	private Long primaryFocusId;
	
	public Long getPrimaryFocusId() {
		return primaryFocusId;
	}

	public void setPrimaryFocusId(Long primaryFocusId) {
		this.primaryFocusId = primaryFocusId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DisplayablePrimaryFocusAwareEntity that = (DisplayablePrimaryFocusAwareEntity) o;

		return new EqualsBuilder().append(getId(), that.getId()).append(getDisplayName(), that.getDisplayName())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getDisplayName()).toHashCode();
	}
}
