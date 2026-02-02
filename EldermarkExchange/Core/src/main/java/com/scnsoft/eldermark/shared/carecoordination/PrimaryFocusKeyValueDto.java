package com.scnsoft.eldermark.shared.carecoordination;

public class PrimaryFocusKeyValueDto extends KeyValueDto{
	
	private Long primaryFocusId;

	public PrimaryFocusKeyValueDto(Long id, String label, Long primaryFocusId) {
		super(id, label);
		this.primaryFocusId = primaryFocusId;
	}

	public Long getPrimaryFocusId() {
		return primaryFocusId;
	}

	public void setPrimaryFocusId(Long primaryFocusId) {
		this.primaryFocusId = primaryFocusId;
	}
	
}
