package com.scnsoft.eldermark.dto.serviceplan;

import com.scnsoft.eldermark.dto.TypeDto;

public class ClientServicePlanListItemDto {
	private Long id;
	private TypeDto status;
	private Long dateCreated;
	private Long dateCompleted;
	private Integer scoring;
	private String author;
	private Long clientId;
	private String clientName;
	private boolean canEdit;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TypeDto getStatus() {
		return status;
	}

	public void setStatus(TypeDto status) {
		this.status = status;
	}

	public Long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Long getDateCompleted() {
		return dateCompleted;
	}

	public void setDateCompleted(Long dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	public Integer getScoring() {
		return scoring;
	}

	public void setScoring(Integer scoring) {
		this.scoring = scoring;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public boolean getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
}