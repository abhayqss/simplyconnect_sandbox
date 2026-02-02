package com.scnsoft.eldermark.dto;

@Deprecated
public class PrimaryFocusAwareKeyValueDto extends KeyValueDto<Long> {

	private Long primaryFocusId;

	public Long getPrimaryFocusId() {
		return primaryFocusId;
	}
	public void setPrimaryFocusId(Long primaryFocusId) {
		this.primaryFocusId = primaryFocusId;
	}

}
