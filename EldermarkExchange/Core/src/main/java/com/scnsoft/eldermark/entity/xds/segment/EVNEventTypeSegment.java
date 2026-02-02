package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0062EventReason;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "EVN_EventTypeSegment")
public class EVNEventTypeSegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_type_code")
    private String eventTypeCode;

    @Column(name = "recorded_datetime")
    private Date recordedDatetime;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "event_reason_code_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0062EventReason> eventReasonCode;

    @Column(name = "event_occurred")
    private Date eventOccurred;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public Date getRecordedDatetime() {
        return recordedDatetime;
    }

    public void setRecordedDatetime(Date recordedDatetime) {
        this.recordedDatetime = recordedDatetime;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0062EventReason> getEventReasonCode() {
        return eventReasonCode;
    }

    public void setEventReasonCode(ISCodedValueForUserDefinedTables<HL7CodeTable0062EventReason> eventReasonCode) {
        this.eventReasonCode = eventReasonCode;
    }

    public Date getEventOccurred() {
        return eventOccurred;
    }

    public void setEventOccurred(Date eventOccurred) {
        this.eventOccurred = eventOccurred;
    }
}
