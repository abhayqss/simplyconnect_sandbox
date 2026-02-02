package com.scnsoft.eldermark.services.phr;

import com.scnsoft.eldermark.dao.phr.AccessRightDao;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author phomal
 * Created on 5/15/2017
 */
@Service
@Transactional
public class AccessRightsService {

    @Autowired
    AccessRightDao accessRightDao;

    private final Map<AccessRight.Code, AccessRight> cache = new HashMap<AccessRight.Code, AccessRight>();

    @PostConstruct
    void postConstruct() {
        // cache access rights
        for (AccessRight.Code code : AccessRight.Code.values()) {
            AccessRight accessRight = accessRightDao.findByCode(code);
            cache.put(code, accessRight);
        }
    }


    public static Map<AccessRight.Code, Boolean> getAccessRights(ResidentCareTeamMember ctm) {
        Map<AccessRight.Code, Boolean> dto = new HashMap<AccessRight.Code, Boolean>();

        for (AccessRight accessRight : ctm.getAccessRights()) {
            dto.put(accessRight.getCode(), Boolean.TRUE);
        }
        for (AccessRight.Code accessRight : AccessRight.Code.values()) {
            if (!dto.containsKey(accessRight)) {
                dto.put(accessRight, Boolean.FALSE);
            }
        }

        return dto;
    }

    public AccessRight getAccessRight(AccessRight.Code code) {
        return cache.get(code);
    }

    public void updateAccessRights(ResidentCareTeamMember ctm, Map<AccessRight.Code, Boolean> accessRightsMap) {
        if (ctm.getAccessRights() == null) {
            ctm.setAccessRights(new HashSet<AccessRight>());
        }

        if (Boolean.TRUE.equals(accessRightsMap.get(AccessRight.Code.MEDICATIONS_LIST)) && !Boolean.TRUE.equals(accessRightsMap.get(AccessRight.Code.MY_PHR))) {
            // invalid combination of access rights
            return;
        }

        for (AccessRight.Code code : cache.keySet()) {
            AccessRight accessRight = cache.get(code);
            if (Boolean.TRUE.equals(accessRightsMap.get(code))) {
                if (!ctm.getAccessRights().contains(accessRight)) {
                    ctm.getAccessRights().add(accessRight);
                }
            } else {
                ctm.getAccessRights().remove(accessRight);
            }
        }
    }


    public Set<AccessRight> getDefaultAccessRights() {
        return new HashSet<AccessRight>(cache.values());
    }

    public boolean checkHasAccessRight(ResidentCareTeamMember ctm, AccessRight.Code accessRightCode) {
        if (ctm == null) {
            return false;
        }
        AccessRight accessRight = cache.get(accessRightCode);
        return ctm.getAccessRights().contains(accessRight);
    }
}
