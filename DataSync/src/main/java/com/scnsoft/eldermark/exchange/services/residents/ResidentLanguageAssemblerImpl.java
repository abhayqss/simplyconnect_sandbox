package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.Language;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;
import org.springframework.stereotype.Component;

@Component
public class ResidentLanguageAssemblerImpl implements ResidentLanguageAssembler {

    @Override
    public Language.Updatable createLanguageUpdatable(ResidentData sourceResident, ResidentForeignKeys foreignKeys) {
        Language.Updatable updatable = new Language.Updatable();
        updatable.setCodeId(foreignKeys.getPrimaryLanguageId());
        updatable.setPreferenceInd(true);
        updatable.setAbilityModeId(null);
        updatable.setAbilityProficiencyId(null);
        return updatable;
    }

    @Override
    public Language createLanguage(ResidentData sourceResident, ResidentForeignKeys foreignKeys, long residentNewId,
                                   long databaseId) {
        Language language = new Language();
        language.setResidentId(residentNewId);
        language.setUpdatable(createLanguageUpdatable(sourceResident, foreignKeys));
        language.setDatabaseId(databaseId);
        language.setLegacyId(sourceResident.getId());
        return language;
    }
}
