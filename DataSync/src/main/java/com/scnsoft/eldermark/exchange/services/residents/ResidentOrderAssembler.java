package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResidentOrder;
import com.scnsoft.eldermark.framework.DatabaseInfo;

import java.util.List;

public interface ResidentOrderAssembler {
    List<ResidentOrder> getResidentOrders(ResidentData sourceResident, long residentNewId, long databaseId);
}
