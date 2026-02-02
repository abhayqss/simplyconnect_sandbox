if OBJECT_ID('EmployeeDisabledConversationNotification') is not null
    drop table EmployeeDisabledConversationNotification
GO

create table EmployeeDisabledConversationNotification
(
    id                bigint identity not null,
    constraint PK_EmployeeDisabledConversationNotification PRIMARY KEY (id),

    employee_id       bigint          not null,
    constraint FK_EmployeeDisabledConversationNotification_Employee_employeeId
        FOREIGN KEY (employee_id) REFERENCES Employee_enc (id),

    notification_type varchar(15)     not null,
    channel           varchar(18)     NOT NULL,
)
GO
