package com.scnsoft.eldermark.entity.video;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.NotificationType;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ConversationNotification")
public class ConversationNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "employee_id", insertable = false, updatable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationType channel;

    @Column(name = "destination")
    private String destination;

    @Column(name = "sent_datetime")
    private Instant sentDatetime;

    @Column(name = "created_datetime")
    private Instant createdDatetime;

    @Column(name = "twilio_conversation_sid")
    private String twilioConversationSid;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private ConversationNotificationType type;

    @Column(name = "twilio_identity")
    private String twilioIdentity;

    @Column(name = "is_fail")
    private boolean isFail;

    @Column(name = "twilio_message_sid")
    private String twilioMessageSid;

    @Column(name = "twilio_room_sid")
    private String twilioRoomSid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public NotificationType getChannel() {
        return channel;
    }

    public void setChannel(NotificationType channel) {
        this.channel = channel;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Instant getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Instant sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

    public String getTwilioConversationSid() {
        return twilioConversationSid;
    }

    public void setTwilioConversationSid(String twilioConversationSid) {
        this.twilioConversationSid = twilioConversationSid;
    }

    public ConversationNotificationType getType() {
        return type;
    }

    public void setType(ConversationNotificationType type) {
        this.type = type;
    }

    public Instant getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Instant createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public String getTwilioIdentity() {
        return twilioIdentity;
    }

    public void setTwilioIdentity(String twilioIdentity) {
        this.twilioIdentity = twilioIdentity;
    }

    public boolean isFail() {
        return isFail;
    }

    public void setFail(boolean fail) {
        isFail = fail;
    }

    public String getTwilioMessageSid() {
        return twilioMessageSid;
    }

    public void setTwilioMessageSid(String twilioMessageSid) {
        this.twilioMessageSid = twilioMessageSid;
    }

    public String getTwilioRoomSid() {
        return twilioRoomSid;
    }

    public void setTwilioRoomSid(String twilioRoomSid) {
        this.twilioRoomSid = twilioRoomSid;
    }
}
