package com.scnsoft.eldermark.entity.video;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.NotificationType;

import javax.persistence.*;

@Entity
@Table(name = "EmployeeDisabledConversationNotification")
public class EmployeeDisabledConversationNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationType channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private ConversationNotificationType type;

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

    public ConversationNotificationType getType() {
        return type;
    }

    public void setType(ConversationNotificationType type) {
        this.type = type;
    }
}
