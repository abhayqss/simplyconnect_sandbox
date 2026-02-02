update ServicePlanGoal
set provider_address = (select TOP 1 REPLACE(REPLACE(
                                                 REPLACE(CONCAT(isnull(oa.street_address, ''), '|', isnull(oa.city, ''),
                                                                '|',
                                                                isnull(oa.state, ''), '|',
                                                                isnull(oa.postal_code, '')), '|||', ' '), '||', ' '),
                                             '|', ' ')
                        from Organization o
                          left join OrganizationAddress oa on o.id = oa.org_id
                        where
                          ServicePlanGoal.provider_name = o.name
                          AND
                          ServicePlanGoal.email = isnull(o.email, (select top (1) [value]
                                                                   from OrganizationTelecom ot
                                                                   where ot.use_code = 'EMAIL' and
                                                                         ot.organization_id = o.id))
                          and
                          ServicePlanGoal.phone =
                          isnull(o.phone, (select top (1) [value]
                                           from OrganizationTelecom ot
                                           where ot.use_code = 'WP' and ot.organization_id = o.id))
)
where provider_address is null;
