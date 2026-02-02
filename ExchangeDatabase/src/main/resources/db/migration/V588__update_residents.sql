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

declare @residentsWithEmptyGender table (id int);
declare @residentsWithEmptyMaritalStatus table (id int);

-- 1. Populate @residentsWithEmptyGender and @residentsWithEmptyMaritalStatus
insert into @residentsWithEmptyGender (id)
select id from Resident where gender_id is null

insert into @residentsWithEmptyMaritalStatus (id)
select id from Resident where marital_status_id is null

-- 2. Populate Gender
declare @residentsWithEmptyGenderSize int
        SET @residentsWithEmptyGenderSize = (select Count(*) From @residentsWithEmptyGender)

  declare @counter int = 0
  declare @assessmentResultsTable table(resident_id bigint, json_result varchar(MAX));
  DECLARE  @assessmentCount int ;

        While @counter <= @residentsWithEmptyGenderSize
BEGIN

  declare @residentId bigint;
  SET @residentId =   (Select id
    From
  (
  Select
  Row_Number() Over (Order By id) As RowNum, *
  From @residentsWithEmptyGender
    ) t2
  where RowNum = @counter)


  INSERT INTO @assessmentResultsTable
  SELECT resident_id, json_result from ResidentAssessmentResult where resident_id = @residentId
    and assessment_id = (SELECT id from Assessment WHERE code = 'COMPREHENSIVE')
    and archived = 0;

    SET  @assessmentCount = (SELECT count(*) from @assessmentResultsTable)
  IF @assessmentCount > 0
  BEGIN
    declare @asmCounter int = 1
            while @asmCounter <= @assessmentCount
    BEGIN
      declare @jsonRes varchar(MAX)= (Select json_result
                From
                (
                Select
                Row_Number() Over (Order By resident_id) As RowNum, *
                From @assessmentResultsTable
                ) t2
                where RowNum = @asmCounter)

        if CHARINDEX('"Gender":',@jsonRes) > 0
      BEGIN
        declare @genderFromAssessment varchar(MAX)= dbo.ParseJson(@jsonRes, 'Gender')

        UPDATE Resident SET gender_id = (SELECT id from CcdCode where code_system = '2.16.840.1.113883.5.1' and display_name = @genderFromAssessment)
        WHERE id = @residentId

          BREAK
      END

      SET  @asmCounter = @asmCounter + 1
    END

  END


  DELETE from @assessmentResultsTable where 1 = 1
    SET @counter = @counter + 1

END

-- 3. Populate MaritalStatus ------------------------------------------------------------------------------
declare @residentsWithEmptyMaritalStatusSize int
  SET @residentsWithEmptyMaritalStatusSize = (select Count(*) From @residentsWithEmptyMaritalStatus)

  SET @counter = 1
  SET @assessmentCount = 0

While @counter <= @residentsWithEmptyMaritalStatusSize
BEGIN

    SET @residentId =   (Select id
                         From
                           (
                             Select
                               Row_Number() Over (Order By id) As RowNum, *
                             From @residentsWithEmptyMaritalStatus
                           ) t2
                           where RowNum = @counter)

  INSERT INTO @assessmentResultsTable
  SELECT resident_id, json_result from ResidentAssessmentResult where resident_id = @residentId
    and assessment_id = (SELECT id from Assessment WHERE code = 'COMPREHENSIVE')
    and archived = 0;

    SET  @assessmentCount = (SELECT count(*) from @assessmentResultsTable)
  IF @assessmentCount > 0
  BEGIN
      SET @asmCounter = 1
    while @asmCounter <= @assessmentCount
    BEGIN
        set @jsonRes = (Select json_result
                        From
                          (
                            Select
                              Row_Number() Over (Order By resident_id) As RowNum, *
                            From @assessmentResultsTable
                          ) t2
                          where RowNum = @asmCounter)

      if CHARINDEX('"Marital status":', @jsonRes) > 0
      BEGIN
        declare @maritalStatusFromAssessment varchar(MAX)= dbo.ParseJson(@jsonRes, 'Marital status')

        UPDATE Resident SET  marital_status_id = (SELECT id from CcdCode where code_system = '2.16.840.1.113993.5.2' and display_name = @maritalStatusFromAssessment)
        WHERE id = @residentId
          BREAK
      END

      SET  @asmCounter = @asmCounter + 1
    END

  END


  DELETE from @assessmentResultsTable where 1 = 1
    SET @counter = @counter + 1

END

commit TRANSACTION TransactionWithGos;
GO



