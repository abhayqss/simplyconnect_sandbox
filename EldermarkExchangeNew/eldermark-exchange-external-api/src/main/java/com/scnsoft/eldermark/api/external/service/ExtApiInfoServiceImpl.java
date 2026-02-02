package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.shared.dto.CareTeamRoleDto;
import com.scnsoft.eldermark.api.shared.dto.StateDto;
import com.scnsoft.eldermark.api.shared.entity.VitalSignType;
import com.scnsoft.eldermark.dao.CareTeamRoleDao;
import com.scnsoft.eldermark.dao.StateDao;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.State_;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.util.StreamUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ExtApiInfoServiceImpl implements ExtApiInfoService {

    @Autowired
    private StateDao stateDao;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    @Autowired
    private DozerBeanMapper dozer;

    @Override
    public List<StateDto> getAllStates() {
        List<StateDto> dtos = new ArrayList<StateDto>();
        for (State state : stateDao.findAll(Sort.by(State_.NAME))) {
            StateDto dto = dozer.map(state, StateDto.class);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<CareTeamRoleDto> getAllCareTeamRoles() {
        return careTeamRoleDao.findAll().stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Map<VitalSignType, String> getVitalSignTypes() {
        return Stream.of(VitalSignType.values())
                .collect(StreamUtils.toMapOfUniqueKeysAndThen(
                        Function.identity(),
                        VitalSignType::displayName)
                );
    }

    private CareTeamRoleDto convert(CareTeamRole role) {
        return new CareTeamRoleDto(role.getId(), role.getDisplayName(), role.getCode());
    }
}
