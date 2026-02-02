if col_length('State', 'opt_in_out_policy') is not null
    begin
        alter table State
            drop column opt_in_out_policy
    end
go

if object_id('OrganizationOptInOutPolicy') is not null
    begin
        exec drop_fk_constraints 'OrganizationOptInOutPolicy'
        drop table OrganizationOptInOutPolicy
    end
go

if col_length('State', 'hie_consent_policy_type') is not null
    begin
        alter table State
            drop column hie_consent_policy_type
    end
go

if object_id('OrganizationHieConsentPolicy') is not null
    begin
        exec drop_fk_constraints 'OrganizationHieConsentPolicy'
        drop table OrganizationHieConsentPolicy
    end
go

alter table State
    add hie_consent_policy_type varchar(10) null
go

update State
set hie_consent_policy_type = 'OPT_OUT'
go

update State
set hie_consent_policy_type = 'OPT_IN'
where abbr in ('AL',
               'AK',
               'AZ',
               'AR',
               'CO',
               'CT',
               'DC',
               'GA',
               'HI',
               'ID',
               'IL',
               'IA',
               'KS',
               'KY',
               'ME',
               'MD',
               'MI',
               'MN',
               'MS',
               'MO',
               'MT',
               'NE',
               'NH',
               'NJ',
               'NM',
               'NY',
               'NC',
               'ND',
               'OH',
               'OK',
               'PA',
               'RI',
               'SC',
               'SD',
               'TN',
               'UT',
               'VT',
               'WV',
               'WI',
               'WY')
go

alter table State
    alter column hie_consent_policy_type varchar(10) not null
go

create table OrganizationHieConsentPolicy
(
    id                 bigint identity (1, 1) not null,
    chain_id           bigint                 null,
    archived           bit                    not null,
    status             varchar(50)            not null,
    organization_id    bigint                 not null,
    type               varchar(10)            not null,
    last_modified_date datetime2(7)           not null,
    creator_id         bigint                 null,
    constraint PK_OrganizationHieConsentPolicy primary key (id),
    constraint FK_OrganizationHieConsentPolicy_Organization foreign key (organization_id) references Organization (id)
)
go

insert into OrganizationHieConsentPolicy(archived, status, organization_id, type, last_modified_date)
select 0, 'CREATED', o.id, s.hie_consent_policy_type, getdate()
from Organization o
         inner join OrganizationAddress oa on o.id = oa.org_id
         inner join State s on s.abbr = oa.state
where o.legacy_table = 'Company'
go

insert into OrganizationHieConsentPolicy(archived, status, organization_id, type, last_modified_date)
select 0, 'CREATED', o.id, 'OPT_IN', getdate()
from Organization o
         inner join SourceDatabase sd on o.database_id = sd.id
where o.legacy_table = 'Company'
  and sd.alternative_id in ('Health_Partners', 'Health_Partners_Test')
go

update r
set r.hie_consent_policy_type                   = op.type,
    r.hie_consent_policy_obtained_from          = 'State Policy',
    r.hie_consent_policy_obtained_by            = null,
    r.hie_consent_policy_source                 = null,
    r.hie_consent_policy_updated_by_employee_id = null,
    r.hie_consent_policy_update_datetime        = null
from resident_enc r
         inner join Organization o on r.facility_id = o.id
         inner join OrganizationHieConsentPolicy op on o.id = op.organization_id and archived = 0
where r.database_id not in (select id from SourceDatabase where alternative_id in ('Health_Partners', 'Health_Partners_Test'))
go

update r
set r.hie_consent_policy_type                   = op.type,
    r.hie_consent_policy_obtained_from          = 'Contracted agreement in place between Consana and HealthPartners for access of client information using SimplyConnect',
    r.hie_consent_policy_obtained_by            = null,
    r.hie_consent_policy_source                 = null,
    r.hie_consent_policy_updated_by_employee_id = null,
    r.hie_consent_policy_update_datetime        = null
from resident_enc r
         inner join Organization o on r.facility_id = o.id
         inner join OrganizationHieConsentPolicy op on o.id = op.organization_id and archived = 0
where r.database_id in (select id from SourceDatabase where alternative_id in ('Health_Partners', 'Health_Partners_Test'))
go


