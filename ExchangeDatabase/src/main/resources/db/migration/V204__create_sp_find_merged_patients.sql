SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[find_merged_patients]
	@ResidentId bigint
AS
BEGIN
	SET NOCOUNT ON;

	declare @search_registry_id as varchar(255)
	select top 1 @search_registry_id = registry_patient_id
		from [dbo].[MPI]
		where resident_id = @ResidentId

	-- child to parent relations as primary keys
	declare @MPI_tree table (
		child_registry_id varchar(255),
		parent_registry_id varchar(255)
	)

	insert into @MPI_tree
	select
		merged_patients.registry_patient_id as child_registry_id,
		survived_patients.registry_patient_id as parent_registry_id
	from [dbo].[MPI] merged_patients
	inner join [dbo].[MPI] survived_patients on
		(merged_patients.merged = 'Y' and
		 merged_patients.assigning_authority = survived_patients.assigning_authority and
		 merged_patients.surviving_patient_id = survived_patients.patient_id)

	-- all children with distance
	declare @MPI_tree_descendants table (
		parent_registry_id varchar(255),
		child_registry_id varchar(255),
		distance bigint
	)

	;with MPI_tree_descendants as (
		select parent_registry_id, child_registry_id as descendant, 1 as distance
			from @MPI_tree
		union all
		select d.parent_registry_id, s.child_registry_id, d.distance + 1
			from MPI_tree_descendants as d
			join @MPI_tree s on d.descendant = s.parent_registry_id
		)
	insert into @MPI_tree_descendants
		select * from MPI_tree_descendants

	-- root ancestor (main survivor of a group of merged patients)
	declare @root_registry_id as varchar(255)

	select top 1 @root_registry_id = parent_registry_id
		from @MPI_tree_descendants
		where child_registry_id = @search_registry_id
		order by distance desc

	if @root_registry_id is null begin
		set @root_registry_id = @search_registry_id;
	end;

	-- returns ids of all children of the root ancestor + his own id
	select resident_id
		from [dbo].[MPI]
		where registry_patient_id = @root_registry_id
	union all
	select p.resident_id
		from @MPI_tree_descendants gr
			inner join [dbo].[MPI] p on gr.child_registry_id = p.registry_patient_id
		where gr.parent_registry_id = @root_registry_id
END
GO