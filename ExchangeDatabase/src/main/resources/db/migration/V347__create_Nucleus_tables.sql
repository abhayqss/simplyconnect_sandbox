SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[NucleusInfo] (
  [id]              BIGINT      NOT NULL IDENTITY (1, 1),
  [nucleus_user_id] VARCHAR(50) NOT NULL,
  [family_ctm_id]   BIGINT      NULL,
  [resident_id]     BIGINT      NULL
    CONSTRAINT [FK_NI_resident] FOREIGN KEY REFERENCES [dbo].[resident_enc] ([id]),
  [employee_id]     BIGINT      NULL
    CONSTRAINT [FK_NI_employee] FOREIGN KEY REFERENCES [dbo].[Employee_enc] ([id]),
  CONSTRAINT [PK_NucleusInfo] PRIMARY KEY CLUSTERED ([id])
    WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
);

CREATE TABLE [dbo].[NucleusDevice] (
  [id]          BIGINT       NOT NULL IDENTITY (1, 1),
  [nucleus_id]  VARCHAR(50)  NOT NULL,
  [location]    VARCHAR(255) NULL,
  [type]        VARCHAR(255) NULL,
  [resident_id] BIGINT       NULL
    CONSTRAINT [FK_ND_resident] FOREIGN KEY REFERENCES [dbo].[resident_enc] ([id]),
  [employee_id] BIGINT       NULL
    CONSTRAINT [FK_ND_employee] FOREIGN KEY REFERENCES [dbo].[Employee_enc] ([id]),
  CONSTRAINT [PK_NucleusDevice] PRIMARY KEY CLUSTERED ([id])
    WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
);
GO

CREATE UNIQUE NONCLUSTERED INDEX [IDX_NI_resident]
  ON [dbo].[NucleusInfo] ([resident_id])
  WHERE [resident_id] IS NOT NULL;

CREATE UNIQUE NONCLUSTERED INDEX [IDX_NI_employee]
  ON [dbo].[NucleusInfo] ([employee_id])
  WHERE [employee_id] IS NOT NULL;
GO
