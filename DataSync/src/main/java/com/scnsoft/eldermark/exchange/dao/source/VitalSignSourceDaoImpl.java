package com.scnsoft.eldermark.exchange.dao.source;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.model.source.VitalSignData;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDaoImpl;
import com.scnsoft.eldermark.framework.dao.source.operations.IdentifiableSourceEntityOperationsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository("vitalSignSourceDao")
public class VitalSignSourceDaoImpl extends StandardSourceDaoImpl<VitalSignData, Long> {

    public VitalSignSourceDaoImpl() {
        super(VitalSignData.class,
                new IdentifiableSourceEntityOperationsImpl<VitalSignData, Long> (
                        VitalSignData.TABLE_NAME, VitalSignData.RES_VITALS_ID, VitalSignData.TABLE_NAME + "." +Constants.SYNC_STATUS_COLUMN, Long.class) {

                    private final Logger logger = LoggerFactory.getLogger(IdentifiableSourceEntityOperationsImpl.class);

                    @Override
                    protected void appendJoinSql(StringBuilder sb) {
                        sb.append(" LEFT JOIN [Companies] ON [Res_Vitals].[Facility] = [Companies].[Code] ");
                    }
                });
    }
}
