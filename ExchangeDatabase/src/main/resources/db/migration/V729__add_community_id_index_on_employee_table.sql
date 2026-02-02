exec drop_index_if_exists 'Employee_enc', 'IX_Employee_ccn_community_id'
CREATE NONCLUSTERED INDEX [IX_Employee_ccn_community_id] ON [dbo].[Employee_enc]
(
	[ccn_community_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO