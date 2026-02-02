package com.scnsoft.eldermark.entity.palatiumcare;

import com.scnsoft.eldermark.entity.phr.User;

import javax.persistence.*;

@Entity(name = "NotifyAlert")
@Table(name = "PalCare_Alert")
public class Alert extends BasicEntity {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "pc_event_id")
    private PCEvent event;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "responder_id", foreignKey = @ForeignKey(name = "FK_Alert_Responder"))
    private User responder;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_status")
    private AlertStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type")
    private AlertType alertType;

    public PCEvent getEvent() {
        return event;
    }

    public void setEvent(PCEvent event) {
        this.event = event;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus satus) {
        this.status = satus;
    }

    public User getResponder() {
        return responder;
    }

    public void setResponder(User responder) {
        this.responder = responder;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }


    @Override
    public String toString() {
        return "Alert{" +
                "event=" + event +
                ", responder=" + responder +
                ", status=" + status +
                ", alertType=" + alertType +
                '}';
    }
}
