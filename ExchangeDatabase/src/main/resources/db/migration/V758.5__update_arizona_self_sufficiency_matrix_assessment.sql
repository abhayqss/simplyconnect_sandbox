if '${profile}' = 'prod'
    begin
        declare @arizona_ssm_id bigint;
        select @arizona_ssm_id = id from Assessment where code = 'ARIZONA_SSM';

        declare @lss_nor_ca_id bigint;
        select @lss_nor_ca_id = id from SourceDatabase where alternative_id = 'LSS Nor Ca'

        declare @rba_id bigint;
        select @rba_id = id from SourceDatabase where alternative_id = 'rba'

        update Assessment set is_shared = 0 where id = @arizona_ssm_id

        insert into Assessment_SourceDatabase (assessment_id, database_id)
        values (@arizona_ssm_id, @lss_nor_ca_id),
               (@arizona_ssm_id, @rba_id)
    end
go
