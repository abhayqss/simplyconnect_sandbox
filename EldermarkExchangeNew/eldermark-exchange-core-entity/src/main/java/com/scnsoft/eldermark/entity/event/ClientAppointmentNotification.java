package com.scnsoft.eldermark.entity.event;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentNotificationMethod;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ClientAppointmentNotification")
public class ClientAppointmentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private ClientAppointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", nullable = false)
    private ClientAppointmentNotificationMethod notificationMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private AppointmentNotificationType notificationType;

    @Column(name = "created_datetime", nullable = false)
    private Instant createdDatetime;

    @Column(name = "sent_datetime")
    private Instant sentDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientAppointment getAppointment() {
        return appointment;
    }

    public void setAppointment(ClientAppointment appointment) {
        this.appointment = appointment;
    }

    public ClientAppointmentNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(ClientAppointmentNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public AppointmentNotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(AppointmentNotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Instant getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Instant createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Instant getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Instant sentDatetime) {
        this.sentDatetime = sentDatetime;
    }
}
