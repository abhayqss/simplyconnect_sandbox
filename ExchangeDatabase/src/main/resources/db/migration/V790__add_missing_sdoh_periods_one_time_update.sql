if not exists(select 1
              from OneTimeUpdate
              where update_name = 'missing-sdoh-report-periods')
    insert into OneTimeUpdate (update_name)
    values ('missing-sdoh-report-periods')
GO
