use [exchange];

SET XACT_ABORT ON
GO

declare @resident_id bigint;
set @resident_id = 433;
declare @person_id bigint;

set @person_id = (select top 1 person_id from resident_enc where id = @resident_id);

-- delete mobile user if exists

delete from [MPI] where resident_id = @resident_id;
delete from [MPI_merged_residents] where surviving_resident_id = @resident_id OR merged_resident_id = @resident_id;
delete top (1) from [resident_enc] where id = @resident_id;

delete top (1) from [name_enc] where person_id = @person_id;
delete top (1) from [PersonAddress_enc] where person_id = @person_id;
delete top (2) from [PersonTelecom_enc] where person_id = @person_id;
delete top (1) from [Person] where id = @person_id;

GO