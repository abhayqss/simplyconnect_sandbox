if OBJECT_ID('delete_user_third_party_if_exists') is not null
  drop procedure delete_user_third_party_if_exists
GO

create procedure delete_user_third_party_if_exists
    @app_name varchar(50)
AS
  begin
    declare @user_id bigint;
    select @user_id = id
    from UserThirdPartyApplication
    where name = @app_name;

    IF @user_id IS NULL
      BEGIN
        print 'Application (@app_name = ' + @app_name + ') not found'
        RETURN
      END

    delete from AuthToken
    where user_app_id = @user_id;
    delete from UserThirdPartyApplication_Privilege
    where user_app_id = @user_id;
    delete from UserMobileRegistrationApplication
    where user_app_id = @user_id;

    delete from UserThirdPartyApplication
    where id = @user_id
  end
go

exec delete_user_third_party_if_exists 'NucleusCare'
exec delete_user_third_party_if_exists 'NucleusLife QA User'
exec delete_user_third_party_if_exists 'Nucleus'
