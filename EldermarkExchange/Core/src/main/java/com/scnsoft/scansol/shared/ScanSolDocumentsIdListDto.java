package com.scnsoft.scansol.shared;

import java.util.List;

public class ScanSolDocumentsIdListDto {
	private Long employeeId;
	private List<Long> documentsIdList;
	
	
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public List<Long> getDocumentsIdList() {
		return documentsIdList;
	}
	public void setDocumentsIdList(List<Long> documentsIdList) {
		this.documentsIdList = documentsIdList;
	}
}
