package com.scnsoft.eldermark.converter.dto2entity.prospect;

import com.scnsoft.eldermark.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactNotificationMethod;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactType;
import com.scnsoft.eldermark.entity.prospect.ProspectPrimaryContact;
import com.scnsoft.eldermark.service.ProspectCareTeamMemberService;
import com.scnsoft.eldermark.service.ProspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class ProspectPrimaryContactEntityConverter implements Converter<ProspectDto, ProspectPrimaryContact> {

    @Autowired
    private ProspectCareTeamMemberService prospectCareTeamMemberService;

    @Autowired
    private ProspectService prospectService;

    @Override
    public ProspectPrimaryContact convert(ProspectDto source) {
        var sourcePrimaryContact = source.getPrimaryContact();
        ProspectPrimaryContact targetPrimaryContact = null;
        if (sourcePrimaryContact != null) {
            if (source.getId() != null) {
                targetPrimaryContact = prospectService.findById(source.getId()).getPrimaryContact();
            }
            if (targetPrimaryContact == null) {
                targetPrimaryContact = new ProspectPrimaryContact();
            }
            targetPrimaryContact.setType(ClientPrimaryContactType.valueOf(sourcePrimaryContact.getTypeName()));
            targetPrimaryContact.setNotificationMethod(ClientPrimaryContactNotificationMethod.valueOf(sourcePrimaryContact.getNotificationMethodName()));
            if (sourcePrimaryContact.getCareTeamMemberId() != null) {
                targetPrimaryContact.setProspectCareTeamMember(prospectCareTeamMemberService.findById(sourcePrimaryContact.getCareTeamMemberId()));
            } else {
                targetPrimaryContact.setProspectCareTeamMember(null);
            }
        }
        return targetPrimaryContact;
    }
}