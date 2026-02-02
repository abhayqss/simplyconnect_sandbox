SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[UserAvatar] (
  [id]           BIGINT           NOT NULL IDENTITY PRIMARY KEY,
  [file]         [VARBINARY](MAX) NOT NULL,
  [content_type] [VARCHAR](255)   NULL,
  [user_id]      BIGINT           NOT NULL
    CONSTRAINT [FK_UserAvatar_UserMobile_id] FOREIGN KEY REFERENCES [dbo].[UserMobile] (id)
      ON DELETE CASCADE
);
GO

ALTER TABLE [dbo].[UserAvatar]
  WITH CHECK ADD CONSTRAINT [UQ_UserAvatar_UserMobile_id] UNIQUE (user_id);
GO
