if not exists(select 1
              from OneTimeUpdate
              where update_name = 'update-webhooks-url')
    insert into OneTimeUpdate (update_name)
    values ('update-webhooks-url')
GO
