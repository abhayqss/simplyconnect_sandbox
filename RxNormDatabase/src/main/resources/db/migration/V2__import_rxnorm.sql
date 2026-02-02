SET XACT_ABORT ON;

BULK
INSERT RXNSAT
FROM '${project.basedir}\src\main\resources\db\migration\RXNSAT_corrected_separator.csv'
WITH
(
FIELDTERMINATOR = '|',
ROWTERMINATOR = '\n'
);