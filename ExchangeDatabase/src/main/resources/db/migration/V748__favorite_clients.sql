IF OBJECT_ID('Employee_FavouriteResident') is not null
    drop table [dbo].[Employee_FavouriteResident]
GO

CREATE TABLE [dbo].[Employee_FavouriteResident]
(
    [favourite_resident_id] [bigint] NOT NULL,
    CONSTRAINT [FK_Employee_FavouriteResident_Resident_enc] FOREIGN KEY ([favourite_resident_id])
        REFERENCES [dbo].[resident_enc] ([id]),

    [employee_id]           [bigint] NOT NULL,
    CONSTRAINT [FK_Employee_FavouriteResident_Employee_enc] FOREIGN KEY ([employee_id])
        REFERENCES [dbo].[Employee_enc] ([id])
)
