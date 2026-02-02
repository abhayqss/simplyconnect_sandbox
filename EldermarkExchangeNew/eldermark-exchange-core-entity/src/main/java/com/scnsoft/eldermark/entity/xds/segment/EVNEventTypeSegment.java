package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.HDHierarchicDesignator;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0062EventReason;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "EVN_EventTypeSegment")
public class EVNEventTypeSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_type_code")
    private String eventTypeCode;

    @Column(name = "recorded_datetime")
    private Instant recordedDatetime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_reason_code_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0062EventReason> eventReasonCode;

    @Column(name = "event_occurred")
    private Instant eventOccurred;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_facility_id")
    private HDHierarchicDesignator eventFacility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(String eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public Instant getRecordedDatetime() {
        return recordedDatetime;
    }

    public void setRecordedDatetime(Instant recordedDatetime) {
        this.recordedDatetime = recordedDatetime;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0062EventReason> getEventReasonCode() {
        return eventReasonCode;
    }

    public void setEventReasonCode(ISCodedValueForUserDefinedTables<HL7CodeTable0062EventReason> eventReasonCode) {
        this.eventReasonCode = eventReasonCode;
    }

    public Instant getEventOccurred() {
        return eventOccurred;
    }

    public void setEventOccurred(Instant eventOccurred) {
        this.eventOccurred = eventOccurred;
    }

    public HDHierarchicDesignator getEventFacility() {
        return eventFacility;
    }

    public void setEventFacility(HDHierarchicDesignator eventFacility) {
        this.eventFacility = eventFacility;
    }
}
