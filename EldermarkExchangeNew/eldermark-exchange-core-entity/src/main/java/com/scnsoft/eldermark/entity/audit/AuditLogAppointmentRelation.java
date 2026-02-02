package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_Appointment")
public class AuditLogAppointmentRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "appointment_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ClientAppointment appointment;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    public ClientAppointment getAppointment() {
        return appointment;
    }

    public void setAppointment(ClientAppointment appointment) {
        this.appointment = appointment;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(appointmentId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.APPOINTMENT;
    }
}
