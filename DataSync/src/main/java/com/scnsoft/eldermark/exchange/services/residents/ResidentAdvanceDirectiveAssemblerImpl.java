package com.scnsoft.eldermark.exchange.services.residents;

import java.util.ArrayList;
import java.util.List;

import com.scnsoft.eldermark.exchange.model.target.AdvanceDirectiveType;
import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.AdvanceDirective;
import com.scnsoft.eldermark.exchange.model.target.AdvanceDirective.Updatable;
import com.scnsoft.eldermark.exchange.model.vo.OrganizationAdvanceDirective;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;

@Component
public class ResidentAdvanceDirectiveAssemblerImpl implements ResidentAdvanceDirectiveAssembler {

	@Override
	public List<AdvanceDirective> createAdvanceDirectivesForResident(
			ResidentData resident, OrganizationAdvanceDirective organization,
			long residentNewId, long databaseId) {
		List<AdvanceDirective> advanceDirectives = new ArrayList<AdvanceDirective>();
		if (organization == null) {
			return advanceDirectives;
		}
		if (resident.getResuscitate() != null && resident.getResuscitate().equalsIgnoreCase("Yes") && !Utils.isNullOrZero(organization.getResResuscitateCodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResResuscitateCodeId(), AdvanceDirectiveType.CODE_STATUS));
		}
		if (resident.getAdvancedDirective1() != null && resident.getAdvancedDirective1() && !Utils.isNullOrZero(organization.getResAdvDir1CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResAdvDir1CodeId(), AdvanceDirectiveType.ADVANCE_DIRECTIVE));
		}
		if (resident.getAdvancedDirective2() != null && resident.getAdvancedDirective2() && !Utils.isNullOrZero(organization.getResAdvDir2CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResAdvDir2CodeId(), AdvanceDirectiveType.ADVANCE_DIRECTIVE));
		}
		if (resident.getAdvancedDirective3() != null && resident.getAdvancedDirective3() && !Utils.isNullOrZero(organization.getResAdvDir3CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResAdvDir3CodeId(), AdvanceDirectiveType.ADVANCE_DIRECTIVE));
		}
		if (resident.getAdvancedDirective4() != null && resident.getAdvancedDirective4() && !Utils.isNullOrZero(organization.getResAdvDir4CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResAdvDir4CodeId(), AdvanceDirectiveType.ADVANCE_DIRECTIVE));
		}
		if (resident.getCodeStatus1() != null && resident.getCodeStatus1() && !Utils.isNullOrZero(organization.getResCodeStat1CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResCodeStat1CodeId(), AdvanceDirectiveType.CODE_STATUS));
		}
		if (resident.getCodeStatus2() != null && resident.getCodeStatus2() && !Utils.isNullOrZero(organization.getResCodeStat2CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResCodeStat2CodeId(), AdvanceDirectiveType.CODE_STATUS));
		}
		if (resident.getCodeStatus3() != null && resident.getCodeStatus3() && !Utils.isNullOrZero(organization.getResCodeStat3CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResCodeStat3CodeId(), AdvanceDirectiveType.CODE_STATUS));
		}
		if (resident.getCodeStatus4() != null && resident.getCodeStatus4() && !Utils.isNullOrZero(organization.getResCodeStat4CodeId())) {
			advanceDirectives.add(createAdvanceDirective(resident, residentNewId, databaseId, organization.getResCodeStat4CodeId(), AdvanceDirectiveType.CODE_STATUS));
		}
		return advanceDirectives;
	}
	
	@Override
    public AdvanceDirective createAdvanceDirective(ResidentData resident, long residentNewId, long databaseId, Long codeId, AdvanceDirectiveType source) {
        AdvanceDirective advanceDirective = new AdvanceDirective();
        advanceDirective.setUpdatable(createAdvanceDirectiveUpdatable(resident, codeId));
        advanceDirective.setResidentId(residentNewId);
        advanceDirective.setDatabaseId(databaseId);
        advanceDirective.setLegacyId(resident.getId());
        advanceDirective.setLegacyTable(source.getTableName());
        return advanceDirective;
    }

	@Override
	public Updatable createAdvanceDirectiveUpdatable(ResidentData resident,
			Long codeId) {
		AdvanceDirective.Updatable updatable = new AdvanceDirective.Updatable();
        if (!Utils.isEmpty(resident.getAdvanceDirectives())) {
            updatable.setTextType(resident.getAdvanceDirectives());
        }
        updatable.setTypeId(codeId);
        return updatable;
	}
}
