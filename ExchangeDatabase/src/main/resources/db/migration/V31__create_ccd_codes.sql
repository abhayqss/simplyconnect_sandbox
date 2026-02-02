SET XACT_ABORT ON
GO

create table [dbo].[CcdCode]
([id] [bigint] IDENTITY(1,1) NOT NULL,
value_set_name varchar(50),
value_set varchar(40),
code varchar(25) NOT NULL,
code_system varchar(40) NOT NULL,
display_name  [varchar](max) NOT NULL,
inactive [bit],
code_system_name varchar(255),
PRIMARY KEY ([id]));