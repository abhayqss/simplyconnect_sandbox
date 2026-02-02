package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.dao.carecoordination.Responsibility;
import com.scnsoft.eldermark.entity.phr.User;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * @author phomal
 * @author pzhurba
 */
@Entity
@Table(name = "EventNotification")
public class EventNotification implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "responsibility", length = 50, nullable = false)
    private Responsibility responsibility;

    @Basic(optional = false)
    @Column(name = "created_datetime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDatetime;


    @JoinColumn(name = "care_team_role_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private CareTeamRole careTeamRole;
    @JoinColumn(name = "patient_user_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private User userPatient;

    @Basic(optional = true)
    @Column(name = "description", length = 50)
    private String description;

    @Column(name = "sent_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentDatetime;
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Event event;
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = true)
    @ManyToOne(optional = true)
    private Employee employee;

    @Column(name = "person_name")
    private String personName;

    @Column(name = "content")
    private String content;
    @Column(name = "destination", nullable = false)
    private String destination;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Responsibility getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(Responsibility responsibility) {
        this.responsibility = responsibility;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public CareTeamRole getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(CareTeamRole careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    public User getUserPatient() {
        return userPatient;
    }

    public void setUserPatient(User userPatient) {
        this.userPatient = userPatient;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Date sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
