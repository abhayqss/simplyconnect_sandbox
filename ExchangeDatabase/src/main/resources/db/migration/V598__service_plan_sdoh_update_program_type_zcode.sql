declare @zcode_id bigint;
SELECT @zcode_id=[id] FROM [dbo].[ZCode] where code ='ZMSPF'

UPDATE [dbo].[ProgramSubType] SET [zcode_id] = @zcode_id WHERE code = 'MEDICARE_SAVINGS_PROGRAM_FULL'
GO