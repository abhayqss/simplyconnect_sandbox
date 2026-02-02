alter table NoteSubType
  add [allowed_for_event_note] bit not null default 0
GO

update NoteSubType
set allowed_for_event_note = 1
where is_manual = 1 and code != 'SERVICE_STATUS_CHECK'
GO