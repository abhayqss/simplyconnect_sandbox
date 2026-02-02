UPDATE allergies SET exchange_sync_status='O' WHERE facility='001';
UPDATE communications SET exchange_sync_status='O' WHERE communication_id=736;
UPDATE employee_companies SET exchange_sync_status='O' WHERE unique_id IN (62, 63, 66, 67, 68, 69, 291);
