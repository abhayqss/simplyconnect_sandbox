package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.model.source.SettingsData;
import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.FunctionCallExpression;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.dao.source.operations.SourceEntityOperations;
import com.scnsoft.eldermark.framework.dao.source.operations.SourceEntityOperationsImpl;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
public class SettingsSourceDaoImpl implements SettingsSourceDao {
    private static final String SEMIPRIVATE_COUNT_AS_HALF_UNIT = "SemiPrivate_CountAsHalfUnit";
    private static final String MOVEOUTS_COUNT_ON_NEXT_DAY = "MoveOuts_CountOnNextDay";

    private static final String ONE_RECORD_TABLE = "OneRecordTable";

    private static final List<SelectExpression> SELECT_EXPRESSIONS = Arrays.<SelectExpression>asList(
            new FunctionCallExpression("{fn BB_GetSetting ('Setup_Marketing', 'SemiPrivate_CountAsHalfUnit' ) " +
                    "as VARCHAR} AS " + SEMIPRIVATE_COUNT_AS_HALF_UNIT),
            new FunctionCallExpression("{fn BB_GetSetting ('Setup_Marketing', 'MoveOuts_CountOnNextDay' ) " +
                    "as VARCHAR} AS " + MOVEOUTS_COUNT_ON_NEXT_DAY)
    );

    private static final ResultListParameters RESULT_LIST_PARAMETERS = new ResultListParameters(
            new ResultListParameters.Parameter(SEMIPRIVATE_COUNT_AS_HALF_UNIT, ColumnType4D.BOOLEAN),
            new ResultListParameters.Parameter(MOVEOUTS_COUNT_ON_NEXT_DAY, ColumnType4D.BOOLEAN)
    );

    private final SourceEntityOperations<SettingsData> sourceEntityOperations =
            new SourceEntityOperationsImpl<SettingsData>(ONE_RECORD_TABLE);

    @Override
    public SettingsData getSettings(Sql4DOperations sqlOperations) {
        List<SettingsData> settingsList = sourceEntityOperations.getEntities(sqlOperations, SELECT_EXPRESSIONS, RESULT_LIST_PARAMETERS,
                new RowMapper<SettingsData>() {
                    @Override
                    public SettingsData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        SettingsData settings = new SettingsData();
                        settings.setMoveOutsCountOnNextDay(rs.getString(MOVEOUTS_COUNT_ON_NEXT_DAY));
                        settings.setSemiPrivateCountAsHalfUnit(rs.getString(SEMIPRIVATE_COUNT_AS_HALF_UNIT));
                        return settings;
                    }
                });

        if (settingsList.size() != 1) {
            throw new IllegalStateException("Expected settingsList size is 1, but actual is " + settingsList.size());
        }
        return settingsList.get(0);
    }
}
