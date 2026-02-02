package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.AdvanceDirective;
import com.scnsoft.eldermark.exchange.model.target.AdvanceDirectiveType;
import com.scnsoft.eldermark.exchange.model.vo.OrganizationAdvanceDirective;
import com.scnsoft.eldermark.framework.DatabaseInfo;

import java.util.List;

public interface ResidentAdvanceDirectiveAssembler {
    
    List<AdvanceDirective> createAdvanceDirectivesForResident(ResidentData resident, OrganizationAdvanceDirective organization, long residentNewId, long databaseId);
    
    AdvanceDirective.Updatable createAdvanceDirectiveUpdatable(ResidentData resident, Long codeId);

	AdvanceDirective createAdvanceDirective(ResidentData resident, long residentNewId, long databaseId, Long codeId, AdvanceDirectiveType legacyType);
    
}
