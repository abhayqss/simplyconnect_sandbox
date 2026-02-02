package com.scnsoft.eldermark.dto.events;

import java.util.Date;

public class EventFilterDto {

	private Long userId;
	private Long eventTypeId;
	private Long noteTypeId;
	private Date dateFrom;
	private Date dateTo;

	public EventFilterDto(Long userId, Long eventTypeId, Long noteTypeId, Date dateFrom, Date dateTo) {
		this.userId = userId;
		this.eventTypeId = eventTypeId;
		this.noteTypeId = noteTypeId;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(Long eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public Long getNoteTypeId() {
		return noteTypeId;
	}

	public void setNoteTypeId(Long noteTypeId) {
		this.noteTypeId = noteTypeId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}
}
