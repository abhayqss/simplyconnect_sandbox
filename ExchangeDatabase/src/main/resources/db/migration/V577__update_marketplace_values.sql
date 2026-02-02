insert into ServicesTreatmentApproach (display_name, code, primary_focus_id) values
  ('Burial and Cremation Services', dbo.build_code_from_name('Burial and Cremation Services'), (select id
                                                                                                from PrimaryFocus
                                                                                                where
                                                                                                  PrimaryFocus.code =
                                                                                                  'Home_and_Community_Based_Services_Social')),
  ('Podiatry', dbo.build_code_from_name('Podiatry'), (select id
                                                      from PrimaryFocus
                                                      where PrimaryFocus.code =
                                                            'Home_and_Community_Based_Services_Health')),
  ('Eye care', dbo.build_code_from_name('Eye care'), (select id
                                                      from PrimaryFocus
                                                      where
                                                        PrimaryFocus.code = 'Home_and_Community_Based_Services_Health'))


insert into CommunityType (display_name, code, primary_focus_id) values
  ('Eye care', dbo.build_code_from_name('Eye care'), (select id
                                                      from PrimaryFocus
                                                      where
                                                        PrimaryFocus.code = 'Home_and_Community_Based_Services_Health'))


delete from ServicesTreatmentApproach
where code = 'Adult_Day_Programs_8'