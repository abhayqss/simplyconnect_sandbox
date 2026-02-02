SET XACT_ABORT ON
GO
BULK
INSERT CcdCode
FROM '${project.basedir}\src\main\resources\db\migration\CCD_Data_corrected_separator.csv'
WITH
(
FIRSTROW = 2,
KEEPIDENTITY,
FIELDTERMINATOR = '|',
ROWTERMINATOR = '\n'
)