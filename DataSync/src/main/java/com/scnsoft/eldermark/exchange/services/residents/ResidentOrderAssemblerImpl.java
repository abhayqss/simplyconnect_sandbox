package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResidentOrder;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ResidentOrderAssemblerImpl implements ResidentOrderAssembler {
    private static Logger logger = LoggerFactory.getLogger(ResidentOrderAssemblerImpl.class);

    @Override
    public List<ResidentOrder> getResidentOrders(ResidentData sourceResident, long residentNewId, long databaseId) {
        if (sourceResident.getOrders() == null)
            return Collections.emptyList();

        List<ResidentOrder> residentOrders = new ArrayList<ResidentOrder>();

        try {
            for (String row: sourceResident.getOrders().split(Constants.CARRIAGE_RETURN_SEPARATOR)) {
                if (!Utils.isEmpty(row)) {
                    String[] columns = row.split(Constants.TAB_SEPARATOR);

                    if(columns.length == 0)
                        continue;

                    ResidentOrder order = new ResidentOrder();

                    order.setLegacyId(sourceResident.getId());
                    order.setDatabaseId(databaseId);
                    order.setResidentId(residentNewId);

                    order.setOrderName(columns[0]);

                    if(columns.length > 1) {
                        order.setStartDate(ExchangeUtils.parse4DDate(columns[1]));
                    }

                    if(columns.length > 2) {
                        order.setEndDate(ExchangeUtils.parse4DDate(columns[2]));
                    }

                    residentOrders.add(order);
                }
            }
        } catch (Exception e) {
            logger.error("ResidentOrders Parsing error", e);
        }

        return residentOrders;
    }
}
