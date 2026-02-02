create table Event_LabResearchOrder
(
  event_id            bigint not null
    constraint EventLabResearchOrder_Event_enc_id_fk
    references Event_enc,
  lab_research_order_id bigint not null
    constraint EventLabResearchOrder_LabResearchOrder_id_fk
    references LabResearchOrder
)
go

create unique index Event_LabResearchOrder_event_id_uindex
  on Event_LabResearchOrder (event_id)
go

create unique index Event_LabResearchOrder_labresearchorder_id_uindex
  on Event_LabResearchOrder (lab_research_order_id)
go