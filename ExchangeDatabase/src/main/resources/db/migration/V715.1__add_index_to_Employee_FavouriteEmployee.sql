exec drop_index_if_exists 'Employee_FavouriteEmployee', 'IX_Employee_FavouriteEmployee_favourite_employee_id'
CREATE CLUSTERED INDEX [IX_Employee_FavouriteEmployee_favourite_employee_id] ON [dbo].[Employee_FavouriteEmployee]
(
	[favourite_employee_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO