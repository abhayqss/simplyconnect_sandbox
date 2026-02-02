SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[SectionUpdateRequest] (
  [id]            [BIGINT] IDENTITY (1, 1) NOT NULL,
  [patient_id]    [BIGINT]                 NOT NULL,
  [created_by_id] [BIGINT]                 NULL,
  [section]       [VARCHAR](50)            NOT NULL,
  [type]          [CHAR]                   NOT NULL,
  [comment]       [VARCHAR](255)           NULL,
  [sendToAll]     BIT                      NOT NULL,
  PRIMARY KEY ([id])
);

CREATE TABLE [dbo].[SectionUpdateRequestFile] (
  [id]                        [BIGINT] IDENTITY (1, 1) NOT NULL,
  [section_update_request_id] [BIGINT]                 NOT NULL,
  [file]                      [VARBINARY](MAX)         NOT NULL,
  [original_name]             [VARCHAR](255)           NOT NULL,
  [content_type]              [VARCHAR](255)           NOT NULL,
  PRIMARY KEY ([id])
);

CREATE TABLE [dbo].[SectionUpdateRequest_Organization] (
  [section_update_request_id] [BIGINT]                 NOT NULL,
  [organization_id]           [BIGINT]                 NOT NULL
);
GO

ALTER TABLE [dbo].[SectionUpdateRequest]
  WITH CHECK ADD CONSTRAINT [FK_SUR_UserMobile_patient] FOREIGN KEY ([patient_id])
REFERENCES [dbo].[UserMobile] ([id]) ON DELETE NO ACTION;
GO
ALTER TABLE [dbo].[SectionUpdateRequest]
  WITH CHECK ADD CONSTRAINT [FK_SUR_UserMobile_creator] FOREIGN KEY ([created_by_id])
REFERENCES [dbo].[UserMobile] ([id]) ON DELETE NO ACTION;
GO
ALTER TABLE [dbo].[SectionUpdateRequestFile]
  WITH CHECK ADD CONSTRAINT [FK_SURF_SectionUpdateRequest] FOREIGN KEY ([section_update_request_id])
REFERENCES [dbo].[SectionUpdateRequest] ([id]) ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[SectionUpdateRequest_Organization]
  ADD CONSTRAINT UQ_SectionUpdateRequest_Organization UNIQUE(section_update_request_id, organization_id);
GO
