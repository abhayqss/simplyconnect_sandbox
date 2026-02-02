SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO


ALTER TABLE [dbo].[PR1_Procedures]
  ADD [procedure_functional_type_id] bigint,
  CONSTRAINT FK_PR1_Procedures_IS_CodedValueForUserDefinedTables_procedure_functional_type_id FOREIGN KEY ([procedure_functional_type_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);


-- PR1-6 0230: Procedure functional type table
exec addHL7Code 'A', 'Anesthesia', '0230', 'USER';
exec addHL7Code 'P', 'Procedure for treatment (therapeutic, including operations)', '0230', 'USER';
exec addHL7Code 'I', 'Invasive procedure not classified elsewhere (e.g., IV, catheter, etc.)', '0230', 'USER';
exec addHL7Code 'D', 'Diagnostic procedure', '0230', 'USER';
