SET XACT_ABORT ON
  GO

BEGIN TRANSACTION TransactionWithGos;
GO

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

---------------------------------------------- UTILITIES --------------------------------------------------------------
IF (OBJECT_ID('[dbo].[ParseJson]') IS NOT NULL)
  DROP FUNCTION [dbo].[ParseJson]
  GO

  CREATE FUNCTION [dbo].[ParseJson](@json varchar(max), @key varchar(max)) RETURNS varchar(max) AS
BEGIN

  declare @keyWrapped varchar(max)
  set @keyWrapped = '"' + @key + '":'

  declare @keyStart int
  set @keyStart = CHARINDEX(@keyWrapped, @json)

  declare @keyEnd int
  set @keyEnd = @keyStart + LEN(@keyWrapped)

  declare @valueStart int
  set @valueStart = CHARINDEX('"', @json, @keyEnd) + 1

  declare @valueEnd int
  set @valueEnd = CHARINDEX('"', @json, @valueStart)

  return SUBSTRING(@json, @valueStart, @valueEnd - @valueStart)

END
GO
-----------------------------------------------------------------------------------------------------------------------

declare @assessmentResultsTable table(resident_assessment_result_id bigint, resident_id bigint, json_result varchar(MAX));

-- 1. Populate @residentsWithComprehensiveAssessments
INSERT INTO @assessmentResultsTable
select distinct id, resident_id, json_result from ResidentAssessmentResult where
    assessment_id = (SELECT id from Assessment WHERE code = 'COMPREHENSIVE')
    and archived = 0;

-- 2. Iterate over @assessmentResultsTable
--    Parse primary care physician from comprehensive assessment
--    if it is not null - update ResidentComprehensiveAssessment

declare @comprehensiveAssessmentsSize int
        SET @comprehensiveAssessmentsSize = (select Count(*) From @assessmentResultsTable)

  declare @counter int = 0

        While @counter <= @comprehensiveAssessmentsSize

BEGIN

  declare @residentId bigint;
  SET @residentId =   (Select resident_id
    From
    (
      Select Row_Number() Over (Order By resident_id) As RowNum, *
      From @assessmentResultsTable
    ) t2
    where RowNum = @counter);

  declare @jsonResult varchar(max);
  SET @jsonResult =   (Select json_result
    From
    (
      Select
      Row_Number() Over (Order By resident_id) As RowNum, *
      From @assessmentResultsTable
    ) t2
    where RowNum = @counter);

  declare @residentAssessmentResultId varchar(max);
  SET @residentAssessmentResultId =   (Select resident_assessment_result_id
                       From
                         (
                           Select
                             Row_Number() Over (Order By resident_id) As RowNum, *
                           From @assessmentResultsTable
                         ) t2
                       where RowNum = @counter);

    IF CHARINDEX('"First name2":',@jsonResult) > 0 OR CHARINDEX('"Last Name2":',@jsonResult) > 0
  BEGIN
    declare @pcpFirstName varchar(MAX)= dbo.ParseJson(@jsonResult, 'First name2');
      declare @pcpLastName varchar(MAX)= dbo.ParseJson(@jsonResult, 'Last Name2');

    IF @pcpFirstName IS NOT NULL OR @pcpLastName IS NOT NULL
    BEGIN

      IF @pcpFirstName = ':'
      begin
          SET @pcpFirstName = null
      end

      IF @pcpLastName = ':'
      begin
          SET @pcpLastName = null
      end

    INSERT INTO ResidentComprehensiveAssessment(resident_id, resident_assessment_result_id, primary_care_physician_first_name, primary_care_physician_last_name)
          VALUES (@residentId, @residentAssessmentResultId, @pcpFirstName, @pcpLastName);
    END
    END

    SET @counter = @counter + 1
  END

COMMIT TRANSACTION TransactionWithGos;
GO