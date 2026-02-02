insert into InNetworkInsurance(display_name, code) values ('Blue Cross Blue Shield of Michigan', 'BLUE_CROSS_BLUE_SHIELD_OF_MICHIGAN');
insert into InNetworkInsurance(display_name, code) values ('Constitution Life Insurance Company (Supplement)', 'CONSTITUTION_LIFE_INSURANCE_COMPANY_SUPPLEMENT');
insert into InNetworkInsurance(display_name, code) values ('ForeThought (Supplement)', 'FORETHOUGHT_SUPPLEMENT');
insert into InNetworkInsurance(display_name, code) values ('Golden Rule', 'GOLDEN_RULE');
insert into InNetworkInsurance(display_name, code) values ('Buckeye Health Plan', 'BUCKEYE_HEALTH_PLAN');
insert into InNetworkInsurance(display_name, code) values ('Americo', 'AMERICO');
insert into InNetworkInsurance(display_name, code) values ('Bankers Fidelity', 'BANKERS_FIDELITY');

update InNetworkInsurance set display_name = 'Anthem Blue Cross' where display_name = 'Anthem Blue Cross Blue Shield';