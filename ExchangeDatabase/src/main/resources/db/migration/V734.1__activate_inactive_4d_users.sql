
UPDATE Employee_enc
SET [inactive]               = 0,
    [is_auto_status_changed] = 0,
    [deactivate_datetime]    = NULL
WHERE [is_auto_status_changed] = 1 AND [contact_4d] = 1
GO

