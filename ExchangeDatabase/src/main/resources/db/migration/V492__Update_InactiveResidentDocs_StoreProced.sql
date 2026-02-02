IF EXISTS ( SELECT * 
			FROM   sysobjects 
			WHERE  id = object_id(N'[dbo].[update_inactive_resident_documents]') 
			and OBJECTPROPERTY(id, N'IsProcedure') = 1 )
BEGIN
	drop PROCEDURE [dbo].[update_inactive_resident_documents]
END
GO

CREATE PROCEDURE [dbo].[update_inactive_resident_documents](
	@keeper_legacy_id varchar(25),
	@tossed_legacy_id varchar(25),
	@database_id bigint)

AS
SET NOCOUNT ON

DECLARE @tossed_id bigint
SET @tossed_id = (SELECT id FROM resident_enc with(nolock) 
					WHERE legacy_id = @tossed_legacy_id 
					AND database_id = @database_id
					--AND active = 0
				)

DECLARE @count int = 0
IF isNull(@tossed_id,0) > 0
BEGIN
	DECLARE @keeper_id bigint 
	set @keeper_id = (SELECT id FROM resident_enc with(nolock) 
	WHERE legacy_id = @keeper_legacy_id 
	AND database_id = @database_id
	)
		
	IF isNull(@keeper_id,0) > 0 
	BEGIN
		UPDATE Document SET res_legacy_id = @keeper_legacy_id,
		document_title = '#' + @tossed_legacy_id + '_#_' + document_title
		WHERE res_legacy_id = @tossed_legacy_id 
		AND res_db_alt_id = (SELECT alternative_id FROM SourceDatabase WHERE id = @database_id)

		SELECT @count = @@ROWCOUNT
	END 
END

Return @count