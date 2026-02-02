IF OBJECT_ID ('dbo.OrganizationAddressUpdate','TR') IS NOT NULL
  DROP TRIGGER [dbo].[OrganizationAddressUpdate];
GO

CREATE TRIGGER [dbo].[OrganizationAddressUpdate] ON [dbo].[OrganizationAddress]
AFTER UPDATE
AS
  BEGIN
    UPDATE [dbo].[OrganizationAddress]
    set [locationUpToDate] = 0
    from inserted
    where [dbo].[OrganizationAddress].[id] = inserted.[id];
  END;
GO
--
-- UPDATE [dbo].[OrganizationAddress]
-- set city = city
-- where id = 2;
-- GO
--
-- select * from [dbo].[OrganizationAddress];