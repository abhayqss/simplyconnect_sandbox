ALTER TABLE [dbo].[CE_CodedElement] ADD [alternate_identifier] [varchar](30)
GO
ALTER TABLE [dbo].[CE_CodedElement] ADD [alternate_text] [nvarchar](100);
GO
ALTER TABLE [dbo].[CE_CodedElement] ADD [name_of_alternate_coding_system] [varchar](60);
GO