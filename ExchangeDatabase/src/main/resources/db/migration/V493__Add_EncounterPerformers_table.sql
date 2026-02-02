--create new table EncounterPerformer

CREATE TABLE [dbo].[EncounterPerformer](
[id] [bigint] IDENTITY(1,1) NOT NULL,
[encounter_id] [bigint] NOT NULL,
[provider_code_id] [bigint] NULL,
[database_id] [bigint] NULL,
[person_id] [bigint] NULL,
)


ALTER TABLE [dbo].[EncounterPerformer] WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])


ALTER TABLE [dbo].[EncounterPerformer] WITH CHECK ADD FOREIGN KEY([provider_code_id])
REFERENCES [dbo].[AnyCcdCode] ([id])


ALTER TABLE [dbo].[EncounterPerformer] WITH CHECK ADD CONSTRAINT [FK_EncounterPerformer_person_id] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])


ALTER TABLE [dbo].[EncounterPerformer] WITH CHECK ADD CONSTRAINT [FK_EncounterPerformer_encounter_id] FOREIGN KEY([encounter_id])
REFERENCES [dbo].[Encounter] ([id])

--copy the data from EncounterProviderCode table

insert into EncounterPerformer(encounter_id, provider_code_id, database_id, person_id)
select e.id as encounter_id, ep.provider_code_id, e.database_id, e.person_id 
from Encounter e
left join EncounterProviderCode ep on e.id = ep.encounter_id
