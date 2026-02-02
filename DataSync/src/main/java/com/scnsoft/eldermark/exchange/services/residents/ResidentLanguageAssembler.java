package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.Language;
import com.scnsoft.eldermark.framework.DatabaseInfo;

public interface ResidentLanguageAssembler {
    Language createLanguage(ResidentData sourceResident, ResidentForeignKeys foreignKeys, long residentNewId,
                            long databaseId);

    Language.Updatable createLanguageUpdatable(ResidentData sourceResident, ResidentForeignKeys foreignKeys);
}
