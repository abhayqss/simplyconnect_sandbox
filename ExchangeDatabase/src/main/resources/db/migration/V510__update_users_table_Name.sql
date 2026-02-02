
UPDATE u set u.name = RTrim(Coalesce(first_name + ' ','') + Coalesce(last_name + ' ','') ) FROM users u inner JOIN  UserMobile um   on um.id = u.notifyuserid