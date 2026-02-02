package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.ResDiagnosisData;
import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.dao.source.operations.IdentifiableSourceEntityOperationsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository(value = "resDiagnosisSourceDao")
public class ResDiagnosisSourceDaoImpl extends StandardSourceDaoImpl<ResDiagnosisData, Long> {

    protected ResultListParameters createResultListParameters(List<String> keyList, Map<String, Field> columnsToFieldsMap) {
        ResultListParameters resultListParameters = new ResultListParameters(
                new ResultListParameters.Parameter("Mod_User", ColumnType4D.getTypeForClass(String.class)),
                new ResultListParameters.Parameter("Mod_Date", ColumnType4D.getTypeForClass(Date.class)),
                new ResultListParameters.Parameter("Create_Date", ColumnType4D.getTypeForClass(Date.class)),
                new ResultListParameters.Parameter("Rank", ColumnType4D.getTypeForClass(Integer.class)),
                new ResultListParameters.Parameter("Resolve_Date", ColumnType4D.getTypeForClass(Date.class)),
                new ResultListParameters.Parameter("Create_Time", ColumnType4D.getTypeForClass(Time.class)),
                new ResultListParameters.Parameter("Note", ColumnType4D.getTypeForClass(String.class)),
                new ResultListParameters.Parameter("Mod_Time", ColumnType4D.getTypeForClass(Time.class)),
                new ResultListParameters.Parameter("Is_Primary", ColumnType4D.getTypeForClass(Boolean.class)),
                new ResultListParameters.Parameter("Onset_Date", ColumnType4D.getTypeForClass(Date.class)),
                new ResultListParameters.Parameter("Resolve_Date_Future", ColumnType4D.getTypeForClass(Date.class)),
                new ResultListParameters.Parameter("Diagnosis", ColumnType4D.getTypeForClass(String.class)),
                new ResultListParameters.Parameter("Res_Number", ColumnType4D.getTypeForClass(Long.class)),
                new ResultListParameters.Parameter("Unique_ID", ColumnType4D.getTypeForClass(long.class)),
                new ResultListParameters.Parameter("Create_User", ColumnType4D.getTypeForClass(String.class)),
                new ResultListParameters.Parameter("Code_ICD9", ColumnType4D.getTypeForClass(String.class)),
                new ResultListParameters.Parameter("BirthDate", ColumnType4D.getTypeForClass(Date.class))
        );

        return resultListParameters;
    }

    public ResDiagnosisSourceDaoImpl() {
        super(ResDiagnosisData.class,
                new IdentifiableSourceEntityOperationsImpl<ResDiagnosisData, Long>(
                        ResDiagnosisData.TABLE_NAME, ResDiagnosisData.ID_COLUMN, ResDiagnosisData.TABLE_NAME + "." + Constants.SYNC_STATUS_COLUMN, Long.class) {

                    private final Logger logger = LoggerFactory.getLogger(IdentifiableSourceEntityOperationsImpl.class);

                    @Override
                    protected void appendSelectClause(StringBuilder sb, List<SelectExpression> selectExpressions) {
                        sb.append("SELECT ");
                        sb.append(" [Res_Diagnosis].[Mod_User] as [Mod_User], ");
                        sb.append(" [Res_Diagnosis].[Mod_Date] as [Mod_Date], ");
                        sb.append(" [Res_Diagnosis].[Create_Date] as [Create_Date], ");
                        sb.append(" [Res_Diagnosis].[Rank] as [Rank], ");
                        sb.append(" [Res_Diagnosis].[Resolve_Date] as [Resolve_Date], ");
                        sb.append(" [Res_Diagnosis].[Create_Time] as [Create_Time], ");
                        sb.append(" [Res_Diagnosis].[Note] as [Note], ");
                        sb.append(" [Res_Diagnosis].[Mod_Time] as [Mod_Time], ");
                        sb.append(" [Res_Diagnosis].[Is_Primary] as [Is_Primary], ");
                        sb.append(" [Res_Diagnosis].[Onset_Date] as [Onset_Date], ");
                        sb.append(" [Res_Diagnosis].[Resolve_Date_Future] as [Resolve_Date_Future], ");
                        sb.append(" [Res_Diagnosis].[Diagnosis] as [Diagnosis], ");
                        sb.append(" [Res_Diagnosis].[Res_Number] as [Res_Number], ");
                        sb.append(" [Res_Diagnosis].[Unique_ID] as [Unique_ID], ");
                        sb.append(" [Res_Diagnosis].[Create_User] as [Create_User], ");
                        sb.append(" [Res_Diagnosis].[Code_ICD9] as [Code_ICD9], ");
                        sb.append(" [Resident].[BirthDate] as [BirthDate] ");
                    }

                    @Override
                    protected void appendJoinSql(StringBuilder sb) {
                        sb.append(" LEFT JOIN [Resident] ON [Res_Diagnosis].[Res_Number] = [Resident].[Res_Number] ");
                    }
                });
    }


}
