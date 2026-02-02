package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.Constants;
import com.scnsoft.eldermark.exchange.ExchangeUtils;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResidentNotes;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ResidentNotesAssemblerImpl implements ResidentNotesAssembler {
    private static Logger logger = LoggerFactory.getLogger(ResidentNotesAssemblerImpl.class);

    @Override
    public List<ResidentNotes> getResidentNotes(ResidentData sourceResident, long residentNewId, long databaseId) {
        if (sourceResident.getNoteAlert() == null)
            return Collections.emptyList();

        List<ResidentNotes> residentNotes = new ArrayList<ResidentNotes>();

        try {
            for (String row: sourceResident.getNoteAlert().split(Constants.CARRIAGE_RETURN_SEPARATOR)) {
                if (!Utils.isEmpty(row)) {
                    String[] columns = row.split(Constants.TAB_SEPARATOR);

                    if(columns.length == 0)
                        continue;

                    ResidentNotes note = new ResidentNotes();

                    note.setLegacyId(sourceResident.getId());
                    note.setDatabaseId(databaseId);
                    note.setResidentId(residentNewId);

                    note.setNote(columns[0]);

                    if(columns.length > 1) {
                        note.setStartDate(ExchangeUtils.parse4DDate(columns[1]));
                    }

                    if(columns.length > 2) {
                        note.setEndDate(ExchangeUtils.parse4DDate(columns[2]));
                    }
                    residentNotes.add(note);
                }
            }
        } catch (Exception e) {
            logger.error("ResidentNotes Parsing error", e);
        }

        return residentNotes;
    }
}
