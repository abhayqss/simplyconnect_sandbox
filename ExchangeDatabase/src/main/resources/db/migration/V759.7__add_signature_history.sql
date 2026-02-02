create view DocumentSignatureHistoryView as
with history as (
    select d.id                   document_id,
           d.signature_request_id request_id
    from Document d
    union all
    select h.document_id,
           h.signature_request_id as request_id
    from DocumentSignatureHistory h
),
     roles as (select id client_role_id from CareTeamRole where code = 'ROLE_PERSON_RECEIVING_SERVICES')
select h.document_id,
       h.request_id,
       'DOCUMENT_SIGNATURE_REQUESTED' action,
       'Signature requested'          action_title,
       e.first_name                   actor_first_name,
       e.last_name                    actor_last_name,
       e.care_team_role_id            actor_role_id,
       r.requested_by_employee_id     employee_id,
       null                           client_id,
       r.date_created                 date
from history h
         join DocumentSignatureRequest r on h.request_id = r.id
         join Employee e on e.id = r.requested_by_employee_id
union all
select h.document_id,
       h.request_id,
       'DOCUMENT_SIGNATURE_REQUEST_CANCELED' action,
       'Request canceled'                    action_title,
       e.first_name                          actor_first_name,
       e.last_name                           actor_last_name,
       e.care_team_role_id                   actor_role_id,
       r.canceled_by_id                      employee_id,
       null                                  client_id,
       r.date_canceled                       date
from history h
         join DocumentSignatureRequest r on h.request_id = r.id
         join Employee e on e.id = r.canceled_by_id
union all
select h.document_id,
       h.request_id,
       'DOCUMENT_SIGNATURE_REQUEST_EXPIRED' action,
       'Request expired'                    action_title,
       null                                 actor_first_name,
       null                                 actor_last_name,
       null                                 actor_role_id,
       null                                 employee_id,
       null                                 client_id,
       r.date_expires                       date
from history h
         join DocumentSignatureRequest r on h.request_id = r.id
where r.status = 'EXPIRED'
union all
select h.document_id,
       h.request_id,
       'DOCUMENT_SIGNED'                                            action,
       'Document signed'                                            action_title,
       iif(e.id is null, c.first_name, e.first_name)                actor_first_name,
       iif(e.id is null, c.last_name, e.last_name)                  actor_last_name,
       iif(e.id is null, roles.client_role_id, e.care_team_role_id) actor_role_id,
       e.id                                                         employee_id,
       c.id                                                         client_id,
       r.date_signed                                                date
from roles,
     history h
         join DocumentSignatureRequest r on h.request_id = r.id
         left join Employee e on e.id = r.requested_from_employee_id
         left join resident c on c.id = r.requested_from_resident_id
where r.status = 'SIGNED'
union all
select h.document_id,
       h.request_id,
       'DOCUMENT_SIGNATURE_FAILED'                                  action,
       'Signature failed'                                           action_title,
       iif(e.id is null, c.first_name, e.first_name)                actor_first_name,
       iif(e.id is null, c.last_name, e.last_name)                  actor_last_name,
       iif(e.id is null, roles.client_role_id, e.care_team_role_id) actor_role_id,
       e.id                                                         employee_id,
       c.id                                                         client_id,
       r.pdcflow_error_datetime                                     date
from roles,
     history h
         join DocumentSignatureRequest r on h.request_id = r.id
         left join Employee e on e.id = r.requested_from_employee_id
         left join resident c on c.id = r.requested_from_resident_id
where r.status = 'SIGNATURE_FAILED'
union all
select h.document_id,
       h.request_id,
       'DOCUMENT_SIGNATURE_FAILED'                  action,
       'Signature failed'                           action_title,
       e.first_name                                 actor_first_name,
       e.last_name                                  actor_last_name,
       iif(e.id is null, null, e.care_team_role_id) actor_role_id,
       e.id                                         employee_id,
       null                                         client_id,
       r.pdcflow_error_datetime                     date
from history h
         join DocumentSignatureRequest r on h.request_id = r.id
         left join Employee e on e.id = r.requested_by_employee_id
where r.status = 'REQUEST_FAILED'
go
