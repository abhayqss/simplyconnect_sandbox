if EXISTS(SELECT *
          FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS
          WHERE CONSTRAINT_NAME = 'CHK_manual_resident_unique_ssn_within_community')
  alter table resident_enc
    drop constraint CHK_manual_resident_unique_ssn_within_community
GO

if OBJECT_ID('isSsnUniqueInCommunity') is not null
  drop function [dbo].[isSsnUniqueInCommunity]
GO

IF COL_LENGTH('resident_enc', 'dont_validate_ssn') IS NOT NULL
  BEGIN
    alter table resident_enc
      drop column dont_validate_ssn;
  END
GO

create function [dbo].[isSsnUniqueInCommunity](
  @ssn_enc    varbinary(max),
  @residentId bigint,
  @facilityId bigint
)
  returns bit
as
  begin
    -- symmetric key should be already open for correct processing
    if (@facilityId is null OR @ssn_enc is null)
      return 'true';

    declare @ssn varchar(11)
    select @ssn = CONVERT(varchar(11), DecryptByKey(@ssn_enc))

    declare @count smallint;
    set @count = (select count(id)
                  from resident_enc
                  where facility_id = @facilityId
                        and ssn is not null
                        and @ssn = CONVERT(varchar(11), DecryptByKey(ssn))
                        and id != @residentId
    )

    if (@count > 0)
      return 'false'
    return 'true';
  end;
go

alter table resident_enc add dont_validate_ssn bit
go

exec update_resident_view
go

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

--disable validation for existing clients with duplicate SSNs
;with dupSsn as (
    select facility_id, ssn, count(id) as c from resident where
      ssn is not null group by facility_id, ssn having count(id) > 1
), ids as (
    select r.id
    from resident r
      join dupSsn on r.ssn = dupSsn.ssn and r.facility_id = dupSsn.facility_id
    where created_by_id is not null
)
update resident_enc set dont_validate_ssn = 1 where id in (select id from ids)
GO

alter table resident_enc
  add constraint CHK_manual_resident_unique_ssn_within_community
check ((dont_validate_ssn is not null and dont_validate_ssn = 1)    --don't validate ssns for existing residents with duplicate ssns created by users
       OR created_by_id is null --resident is created in SC portal
       OR ssn is null           --datasync can create residents with empty ssn
       OR dbo.isSsnUniqueInCommunity(ssn, id, facility_id) = 'true')
go
