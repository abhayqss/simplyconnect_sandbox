/****** Object:  StoredProcedure [dbo].[load_ccd_advance_directives_count]    Script Date: 05/06/2015 17:35:01 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_advance_directives_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
	DECLARE @T TABLE (
		ad_id bigint,
		ad_type varchar(MAX),
		ad_prefix nvarchar(100),
		ad_given nvarchar(100),
		ad_family nvarchar(100),
		ad_date datetime2(7),
		ad_prt_name_id bigint,
		ad_doc_url varchar(255)
	)

	-- select data
	INSERT INTO @T 
		SELECT 
			ad.id, 
			cc.display_name,			 
			n.given, 
			n.family, 
			n.prefix, 
			ad.effective_time_low,
			min(n.id),
			ad_doc.url
		FROM AdvanceDirective ad 
			LEFT OUTER JOIN AdvanceDirectiveDocument ad_doc ON ad.id = ad_doc.advance_directive_id 
			LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id 
			LEFT OUTER JOIN CcdCode cc ON ad.advance_directive_type_id = cc.id 
			LEFT OUTER JOIN Participant prt ON adv.verifier_id = prt.id 
			LEFT OUTER JOIN Person p ON prt.person_id = p.id 
			LEFT OUTER JOIN Name n ON p.id = n.person_id
		WHERE ad.resident_id = @ResidentId
		group by 
			n.person_id, 
			ad.id, 	
			ad.effective_time_low, 
			cc.display_name, 
			n.given, 
			n.family, 
			n.prefix, 
			ad_doc.url

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.ad_id, t.ad_type,
			STUFF (
				(SELECT '|' + COALESCE(t.ad_prefix + ' ', '') + COALESCE(t.ad_given + ' ', '') + COALESCE(t.ad_family + ' ', '') + COALESCE(', ' + CAST(t.ad_date AS VARCHAR), '')
				 FROM @T as s WHERE s.ad_id = t.ad_id FOR XML PATH('')), 1, 1, ''
			) AS ad_verifiers,
			STUFF (
				(SELECT '|' + t.ad_doc_url
				 FROM @T as s WHERE s.ad_id = t.ad_id FOR XML PATH('')), 1, 1, ''
			) AS ad_ref_docs
		FROM @T AS t
		GROUP BY t.ad_id, t.ad_date, t.ad_type, t.ad_prefix, t.ad_given, t.ad_family, t.ad_doc_url
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END
GO