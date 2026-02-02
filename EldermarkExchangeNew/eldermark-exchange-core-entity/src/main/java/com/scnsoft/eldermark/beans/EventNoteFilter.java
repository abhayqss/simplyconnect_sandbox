package com.scnsoft.eldermark.beans;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public class EventNoteFilter extends BasicFilter {

    private Long eventTypeId;
    private Long noteTypeId;
    private Long fromDate;
    private Long toDate;
    private Boolean onlyEventsWithIR;
    private Boolean excludeEvents;
    private Boolean excludeNotes;

    public EventNoteFilter() {

    }

    public EventNoteFilter(Long organizationId, List<Long> communityIds, Long noteTypeId, Long eventTypeId,
                           Long clientId, Long fromDate, Long toDate, Boolean onlyEventsWithIR) {
        setOrganizationId(organizationId);
        setCommunityIds(communityIds);
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.noteTypeId = noteTypeId;
        this.eventTypeId = eventTypeId;
        setClientId(clientId);
        this.onlyEventsWithIR = onlyEventsWithIR;
    }

    public EventNoteEnum getItemType() {
        var typeByExcludeFlags = getItemTypeByExcludeFlags();
        if (BooleanUtils.isTrue(onlyEventsWithIR)) {
            switch (typeByExcludeFlags) {
                case ALL:
                    return EventNoteEnum.EVENT;
                case NOTE:
                    return EventNoteEnum.NONE;
            }
        }
        return typeByExcludeFlags;
    }

    private EventNoteEnum getItemTypeByExcludeFlags() {
        if (ObjectUtils.anyNotNull(this.excludeEvents, this.excludeNotes)) {
            if (BooleanUtils.isTrue(excludeEvents)) {
                return BooleanUtils.isTrue(excludeNotes) ? EventNoteEnum.NONE : EventNoteEnum.NOTE;
            } else {
                return BooleanUtils.isTrue(excludeNotes) ? EventNoteEnum.EVENT : EventNoteEnum.ALL;
            }
        } else {
            return EventNoteEnum.ALL;
        }
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

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public Boolean getOnlyEventsWithIR() {
        return onlyEventsWithIR;
    }

    public void setOnlyEventsWithIR(Boolean onlyEventsWithIR) {
        this.onlyEventsWithIR = onlyEventsWithIR;
    }



    public Boolean getExcludeEvents() {
        return excludeEvents;
    }

    public void setExcludeEvents(Boolean excludeEvents) {
        this.excludeEvents = excludeEvents;
    }

    public Boolean getExcludeNotes() {
        return excludeNotes;
    }

    public void setExcludeNotes(Boolean excludeNotes) {
        this.excludeNotes = excludeNotes;
    }

    public enum EventNoteEnum {
        NOTE("NOTE"), EVENT("EVENT"), ALL("ALL"), NONE("NONE");

        private String displayName;

        EventNoteEnum(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}
