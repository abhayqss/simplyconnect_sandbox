package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dao.CommunityCareTeamMemberDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dto.CareTeamMemberDto;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommunityCareTeamMemberDtoToEntityConverter extends CareTeamMemberEntityConverter<CommunityCareTeamMember> implements Converter<CareTeamMemberDto, CommunityCareTeamMember> {

    @Autowired
    private CommunityCareTeamMemberDao communityCareTeamMemberDao;

    @Autowired
    private CommunityDao communityDao;

    @Override
    public CommunityCareTeamMember convert(CareTeamMemberDto source) {
        CommunityCareTeamMember target;
        if (source.getId() != null) {
            target = communityCareTeamMemberDao.findById(source.getId()).orElseThrow();
        } else {
            target = new CommunityCareTeamMember();
            target.setCommunity(communityDao.getOne(source.getCommunityId()));
        }
        target = setCommonFields(source, target);
        target.setCommunityId(source.getCommunityId());
        return target;
    }

}
