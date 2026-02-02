package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.AffiliatedRelationshipDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class AffiliatedRelationshipServiceImpl implements AffiliatedRelationshipService {

    @Autowired
    private AffiliatedRelationshipDao affiliatedRelationshipDao;

    @Override
    public boolean existsByPrimaryCommunityIdIn(Collection<Long> communityIds) {
        return affiliatedRelationshipDao.existsByPrimaryCommunityIdIn(communityIds);
    }
}
