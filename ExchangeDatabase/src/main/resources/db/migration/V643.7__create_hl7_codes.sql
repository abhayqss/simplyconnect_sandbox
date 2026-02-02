UPDATE ID_CodedValuesForHL7Tables
set hl7_defined_code_table_id = null
WHERE hl7_defined_code_table_id IN (
  select id
  from HL7CodeTable
  where table_number in ('0105', '0085')
)

UPDATE IS_CodedValueForUserDefinedTables
set hl7_user_defined_code_table_id = null
WHERE hl7_user_defined_code_table_id IN (
  select id
  from HL7CodeTable
  where table_number in ('0078')
)

delete from HL7DefinedCodeTable
where id in (
  select id
  from HL7CodeTable
  where table_number in ('0105', '0085')
)

delete from HL7UserDefinedCodeTable
where id in (
  select id
  from HL7CodeTable
  where table_number in ('0078')
)

delete from HL7CodeTable
where table_number in ('0105', '0078', '0085')

exec addHL7Code 'L', 'Ancillary (filler) department is source of comment', '0105', 'HL7';
exec addHL7Code 'O', 'Other system is source of comment', '0105', 'HL7';
exec addHL7Code 'P', 'Orderer (placer) is source of comment', '0105', 'HL7';

exec addHL7Code '<', 'Below absolute low-off instrument scale', '0078', 'USER';
exec addHL7Code '>', 'Above absolute high-off instrument scale', '0078', 'USER';
exec addHL7Code 'A', 'Abnormal (applies to non-numeric results)', '0078', 'USER';
exec addHL7Code 'AA', 'Very abnormal (applies to non-numeric units, analogous to panic limits for numeric units)',
                '0078', 'USER';
exec addHL7Code 'B', 'Better--use when direction not relevant', '0078', 'USER';
exec addHL7Code 'D', 'Significant change down', '0078', 'USER';
exec addHL7Code 'H', 'Above high normal', '0078', 'USER';
exec addHL7Code 'HH', 'Above upper panic limits', '0078', 'USER';
exec addHL7Code 'I', 'Intermediate. Indicates for microbiology susceptibilities only.', '0078', 'USER';
exec addHL7Code 'L	', 'Below low normal', '0078', 'USER';
exec addHL7Code 'LL', 'Below lower panic limits', '0078', 'USER';
exec addHL7Code 'MS', 'Moderately susceptible. Indicates for microbiology susceptibilities only.', '0078', 'USER';
exec addHL7Code 'N	', 'Normal (applies to non-numeric results)', '0078', 'USER';
exec addHL7Code 'null', 'No range defined, or normal ranges don''t apply', '0078', 'USER';
exec addHL7Code 'R', 'Resistant. Indicates for microbiology susceptibilities only.', '0078', 'USER';
exec addHL7Code 'S', 'Susceptible. Indicates for microbiology susceptibilities only.', '0078', 'USER';
exec addHL7Code 'U', 'Significant change up', '0078', 'USER';
exec addHL7Code 'VS', 'Very susceptible. Indicates for microbiology susceptibilities only.', '0078', 'USER';
exec addHL7Code 'W', 'Worse - use when direction not relevant', '0078', 'USER';

exec addHL7Code 'C', 'Record coming over is a correction and thus replaces a final result', '0085', 'HL7';
exec addHL7Code 'D', 'Deletes the OBX record', '0085', 'HL7';
exec addHL7Code 'F', 'Final results; Can only be changed with a corrected result.', '0085', 'HL7';
exec addHL7Code 'I', 'Specimen in lab; results pending', '0085', 'HL7';
exec addHL7Code 'N',
                'Not asked; used to affirmatively document that the observation identified in the OBX was not sought when the universal service ID in OBR-4 implies that it would be sought.',
                '0085', 'HL7';
exec addHL7Code 'O', 'Order detail description only (no result)', '0085', 'HL7';
exec addHL7Code 'P', 'Preliminary results', '0085', 'HL7';
exec addHL7Code 'R', 'Results entered -- not verified', '0085', 'HL7';
exec addHL7Code 'S', 'Partial results', '0085', 'HL7';
exec addHL7Code 'U',
                'Results status change to final without retransmitting results already sent as ‘preliminary.’  E.g., radiology changes status from preliminary to final',
                '0085', 'HL7';
exec addHL7Code 'W', 'Post original as wrong, e.g., transmitted for wrong patient', '0085', 'HL7';
exec addHL7Code 'X', 'Results cannot be obtained for this observation', '0085', 'HL7';

CREATE INDEX IX_LabResearchOrder_requsition_number
  ON LabResearchOrder (requisition_number);

