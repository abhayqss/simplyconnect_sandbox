use [exchange];

SET XACT_ABORT ON
GO

declare @resident_id bigint;
set @resident_id = 189241;
declare @person_id bigint;
declare @user_id bigint;

set @person_id = (select top 1 person_id from resident_enc where id = @resident_id);
set @user_id = (select top 1 id from UserMobile where resident_id = @resident_id);

-- delete mobile user if exists
delete from [UserMobileRegistrationApplication] where user_id = @user_id;
delete from [UserPasswordSecurity] where user_id = @user_id;
delete from [UserMobile] where id = @user_id;

delete from [MPI] where resident_id = @resident_id;
delete from [MPI_merged_residents] where surviving_resident_id = @resident_id OR merged_resident_id = @resident_id;
delete from [AuditLog_Residents] where resident_id = @resident_id;
delete top (1) from [resident_enc] where id = @resident_id;

delete from [name_enc] where person_id = @person_id;
delete from [PersonAddress_enc] where person_id = @person_id;
delete from [PersonTelecom_enc] where person_id = @person_id;
delete top (1) from [Person] where id = @person_id;

GO