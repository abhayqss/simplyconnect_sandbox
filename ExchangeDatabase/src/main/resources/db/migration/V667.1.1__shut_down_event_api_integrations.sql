--fix for V667.2__shut_down_external_api_integrations.sql
--it fails during migration on clean DB saying that ANSI_PADDING is not correct,
--which is set to OFF in V665__add_aseessment_to_service_plan_tables.sql
--so switching it back to ON just before V667.2
SET ANSI_PADDING ON
GO