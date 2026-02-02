package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ProspectCareTeamMemberDao;
import com.scnsoft.eldermark.entity.prospect.ProspectCareTeamMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProspectCareTeamMemberServiceImpl implements ProspectCareTeamMemberService {

    @Autowired
    private ProspectCareTeamMemberDao prospectCareTeamMemberDao;

    @Override
    public ProspectCareTeamMember findById(Long id) {
        return prospectCareTeamMemberDao.findById(id).orElseThrow();
    }
}
