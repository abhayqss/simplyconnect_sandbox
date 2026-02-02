update SourceDatabase
set dont_auto_merge_residents = 1
where alternative_id in ('Health_Partners', 'Health_Partners_Test')
