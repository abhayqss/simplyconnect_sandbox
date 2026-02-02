SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

-- delete duplicate rows (by keeping original)
WITH CTE AS (
    SELECT [RN] = ROW_NUMBER()
    OVER ( PARTITION BY [nucleus_id], [resident_id]
      ORDER BY [id] )
    FROM [dbo].[NucleusDevice]
    WHERE [resident_id] IS NOT NULL
)
DELETE FROM CTE
WHERE [RN] <> 1;

WITH CTE AS (
    SELECT [RN] = ROW_NUMBER()
    OVER ( PARTITION BY [nucleus_id], [employee_id]
      ORDER BY [id] )
    FROM [dbo].[NucleusDevice]
    WHERE [employee_id] IS NOT NULL
)
DELETE FROM CTE
WHERE [RN] <> 1;
GO

-- create constraints
CREATE UNIQUE NONCLUSTERED INDEX [IDX_ND_resident_nucleus_id]
  ON [dbo].[NucleusDevice] ([nucleus_id], [resident_id])
  WHERE [resident_id] IS NOT NULL;

CREATE UNIQUE NONCLUSTERED INDEX [IDX_ND_employee_nucleus_id]
  ON [dbo].[NucleusDevice] ([nucleus_id], [employee_id])
  WHERE [employee_id] IS NOT NULL;
GO
