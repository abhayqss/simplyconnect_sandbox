SET XACT_ABORT ON
GO

CREATE TABLE PushNotificationRegistration (
  id      BIGINT        NOT NULL IDENTITY PRIMARY KEY,
  reg_id  VARCHAR(4096) NOT NULL,
  service VARCHAR(4)    NOT NULL,
  user_id BIGINT        NOT NULL
    CONSTRAINT PushNotificationRegistration_UserMobile_id_fk FOREIGN KEY REFERENCES UserMobile (id)
      ON DELETE CASCADE
);
GO
