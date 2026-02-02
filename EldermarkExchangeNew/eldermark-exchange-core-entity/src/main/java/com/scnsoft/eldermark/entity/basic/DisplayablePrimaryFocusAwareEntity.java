package com.scnsoft.eldermark.entity.basic;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Deprecated
@MappedSuperclass
public class DisplayablePrimaryFocusAwareEntity extends DisplayableNamedKeyEntity{
	
	@Column(name = "primary_focus_id")
    private Long primaryFocusId;

	public DisplayablePrimaryFocusAwareEntity() {
	}

	public DisplayablePrimaryFocusAwareEntity(Long id, String displayName, String key, Long primaryFocusId) {
		super(id, displayName, key);
		this.primaryFocusId = primaryFocusId;
	}

	public Long getPrimaryFocusId() {
		return primaryFocusId;
	}

	public void setPrimaryFocusId(Long primaryFocusId) {
		this.primaryFocusId = primaryFocusId;
	}

}
