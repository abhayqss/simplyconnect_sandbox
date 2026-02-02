create function [dbo].[isOnePrimaryProblemForResident](@primary bit, @id bigint)
  returns bit
as
  begin
    IF (@Primary is null or @Primary = 'false')
      return 'true';
    declare @residentId bigint;
    set @residentId = (select p.resident_id
                       from ProblemObservation po
                         join Problem p on po.problem_id = p.id
                       where po.id = @id)

    declare @count smallint;
    set @count = (select count(po.id)
                  from ProblemObservation po
                    join Problem p on po.problem_id = p.id
                  where p.resident_id = @residentId and po.is_primary = 1 and po.id != @id)
    if (@count = 0)
      return 'true';
    return 'false'
  end;
go

alter table ProblemObservation
  add constraint CHK_Primary_Across_Residents check (dbo.isOnePrimaryProblemForResident(is_primary, id) = 'true')
go