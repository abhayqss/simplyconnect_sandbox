SELECT count(DISTINCT r.id) as [resident count], db.id AS [database id], db.name FROM resident r
JOIN SourceDatabase db ON r.database_id = db.id
GROUP BY db.name, db.id
ORDER BY count(DISTINCT r.id) DESC;