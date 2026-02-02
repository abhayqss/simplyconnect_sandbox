SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[RXNCONSO]
(
   RXCUI             varchar (8) NOT NULL,
   LAT               varchar (3) DEFAULT 'ENG' NOT NULL,
   TS                varchar (1),
   LUI               varchar (8),
   STT               varchar (3),
   SUI               varchar (8),
   ISPREF            varchar (1),
   RXAUI             varchar (8) NOT NULL,
   SAUI              varchar (50),
   SCUI              varchar (50),
   SDUI              varchar (50),
   SAB               varchar (20) NOT NULL,
   TTY               varchar (20) NOT NULL,
   CODE              varchar (50) NOT NULL,
   STR               varchar (3000) NOT NULL,
   SRL               varchar (10),
   SUPPRESS          varchar (1),
   CVF               varchar (50)
);


BULK INSERT [dbo].[RXNCONSO]
FROM  '${project.basedir}\src\main\resources\db\migration\RXNCONSO_corrected_separator.csv'
WITH
(
  FIELDTERMINATOR = '|',
  ROWTERMINATOR = '\n'
);


INSERT
  INTO [dbo].[CcdCode](code, code_system, display_name, code_system_name, inactive)
SELECT
	[CODE],
	'2.16.840.1.113883.6.88',
	[STR],
	'RxNorm',
  CASE SUPPRESS WHEN 'Y' THEN 1 ELSE 0 END
FROM(
	SELECT
    ROW_NUMBER() OVER
		  (PARTITION BY CODE
			  ORDER BY
          CASE TTY
            WHEN 'SCD' THEN 0
            WHEN 'SBD' THEN 1
            WHEN 'BN' THEN 2
            WHEN 'SBDC' THEN 3
            ELSE 4
          END
		    ASC) AS 'RowNumber'
		,[CODE]
		,[STR]
		,[SUPPRESS]
    FROM [dbo].[RXNCONSO]
    WHERE SAB='RXNORM') dt
WHERE RowNumber = 1
AND CODE NOT IN (SELECT code FROM [dbo].[CcdCode] WHERE code_system='2.16.840.1.113883.6.88')


DROP TABLE [dbo].[RXNCONSO]