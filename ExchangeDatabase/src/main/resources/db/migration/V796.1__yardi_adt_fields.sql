IF COL_LENGTH('EVN_EventTypeSegment', 'event_facility_id') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[EVN_EventTypeSegment]
            DROP CONSTRAINT [FK_EVN_EventTypeSegment_HD_HierarchicDesignator_event_facility_id];
        ALTER TABLE [dbo].[EVN_EventTypeSegment]
            DROP COLUMN [event_facility_id];
    END
GO

alter table EVN_EventTypeSegment
    add event_facility_id bigint,
        constraint FK_EVN_EventTypeSegment_HD_HierarchicDesignator_event_facility_id foreign key (event_facility_id)
            references HD_HierarchicDesignator (id)
GO

IF COL_LENGTH('IN1_Insurance', 'policy_number') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[IN1_Insurance]
            DROP COLUMN [policy_number];
    END
GO

alter table IN1_Insurance
    add policy_number varchar(30)
GO
