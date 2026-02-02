  CREATE INDEX IX_MPI_merged_residents_surviving_resident  
    ON dbo.MPI_merged_residents (surviving_resident_id);   

  CREATE INDEX IX_MPI_merged_residents_merged_resident 
    ON dbo.MPI_merged_residents (merged_resident_id);  