package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResidentNotes;
import com.scnsoft.eldermark.framework.DatabaseInfo;

import java.util.List;

public interface ResidentNotesAssembler {
    List<ResidentNotes> getResidentNotes(ResidentData sourceResident, long residentNewId, long databaseId);
}
