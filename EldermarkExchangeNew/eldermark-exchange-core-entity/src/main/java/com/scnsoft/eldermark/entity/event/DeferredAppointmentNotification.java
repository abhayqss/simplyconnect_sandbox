package com.scnsoft.eldermark.entity.event;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "DeferredAppointmentNotification")
public class DeferredAppointmentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private ClientAppointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private AppointmentNotificationType type;

    @Column(name = "dispatch_datetime")
    private Instant dispatchDatetime;

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

    public AppointmentNotificationType getType() {
        return type;
    }

    public void setType(AppointmentNotificationType type) {
        this.type = type;
    }

    public Instant getDispatchDatetime() {
        return dispatchDatetime;
    }

    public void setDispatchDatetime(Instant dispathDatetime) {
        this.dispatchDatetime = dispathDatetime;
    }
}
