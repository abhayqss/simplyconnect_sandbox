package com.scnsoft.eldermark.services.carecoordination;

import com.google.common.base.Function;
import com.scnsoft.eldermark.dao.carecoordination.CareTeamRoleDao;
import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.scnsoft.eldermark.dao.carecoordination.CareTeamRoleDao.ORDER_BY_POSITION;

/**
 * @author averazub
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 05-Oct-15.
 */
@Service
public class CareTeamRoleServiceImpl implements CareTeamRoleService {

    private final CareTeamRoleDao careTeamRoleDao;

    private final Map<Long, CareTeamRoleDto> careTeamRoleMapById = new LinkedHashMap<Long, CareTeamRoleDto>();
    private final Map<CareTeamRoleCode, CareTeamRoleDto> careTeamRoleMapByCode = new LinkedHashMap<CareTeamRoleCode, CareTeamRoleDto>();

    @Autowired
    public CareTeamRoleServiceImpl(CareTeamRoleDao careTeamRoleDao) {
        this.careTeamRoleDao = careTeamRoleDao;
    }

    @PostConstruct
    protected void postConstruct() {
        final List<CareTeamRole> roleList = careTeamRoleDao.findAll(new Sort(ORDER_BY_POSITION));

        for (CareTeamRole role : roleList){
            CareTeamRoleDto dto = new CareTeamRoleDto(role.getId(), role.getName(), role.getCode());
            if(role.getCode() != CareTeamRoleCode.ROLE_NOTIFY_USER) {
                careTeamRoleMapById.put(role.getId(), dto);
                careTeamRoleMapByCode.put(role.getCode(), dto);
            }
        }
    }

    @Override
    public List<CareTeamRoleDto> getAllCareTeamRoles() {
        return new ArrayList<CareTeamRoleDto>(careTeamRoleMapById.values());
    }

    @Override
    public CareTeamRoleDto get(Long id) {
        return careTeamRoleMapById.get(id);
    }

    @Override
    public CareTeamRoleDto get(CareTeamRoleCode code) {
        return careTeamRoleMapByCode.get(code);
    }

    @Override
    public Function<CareTeamRoleCode, CareTeamRoleDto> toDto() {
        return new Function<CareTeamRoleCode, CareTeamRoleDto>() {
            @Override
            public CareTeamRoleDto apply(CareTeamRoleCode code) {
                return CareTeamRoleServiceImpl.this.get(code);
            }
        };
    }

    @Override
    public List<CareTeamRoleDto> getNonAdminCareTeamRoles() {
        List<CareTeamRoleDto> result = new ArrayList<CareTeamRoleDto>();
        for (CareTeamRoleDto role : careTeamRoleMapById.values()) {
            if (CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR != role.getCode()
                    && CareTeamRoleCode.ROLE_ADMINISTRATOR != role.getCode()
                    && CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR != role.getCode()
                    && CareTeamRoleCode.ROLE_NOTIFY_USER != role.getCode()) {
                result.add(role);
            }
        }
        return result;
    }
}
