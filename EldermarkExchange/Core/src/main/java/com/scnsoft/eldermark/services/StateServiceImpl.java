package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.StateDao;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.shared.StateDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author averazub
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 05-Oct-15.
 */
@Service
public class StateServiceImpl implements StateService {

    List<KeyValueDto> statesList = null;
    Map<Long, State> statesByIdMap = null;
    Map<String, State> statesByAbbrMap = null;
    Map<String, State> statesByNameMap = null;

    @Autowired
    StateDao stateDao;

    @Autowired
    DozerBeanMapper dozer;

    @Override
    public List<KeyValueDto> getStates() {
        fillStates();
        return statesList;
    }

    @Override
    public List<StateDto> getAllStates() {
        List<StateDto> dtos = new ArrayList<StateDto>();
        for (State state : stateDao.list("name")) {
            StateDto dto = dozer.map(state, StateDto.class);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public State findByAbbr(String abbr) {
        fillStates();
        return statesByAbbrMap.get(abbr);
    }

    @Override
    public State findByAbbrOrFullName(String nameOrAbbr) {
        fillStates();
        State state1 = statesByAbbrMap.get(nameOrAbbr);
        State state2 = statesByNameMap.get(nameOrAbbr);
        if ((state1 != null) && (state2 != null) && (state1 != state2)) {
            throw new RuntimeException("There are more that 1 state with name " + nameOrAbbr);
        } else if (state1 != null) {
            return state1;
        } else return state2;
    }

    @Override
    public State get(Long id) {
        fillStates();
        return statesByIdMap.get(id);
    }

    private void fillStates() {
        // FIXME? this check is not thread-safe
        if (statesList == null) {
            statesList = new ArrayList<KeyValueDto>();
            statesByIdMap = new HashMap<Long, State>();
            statesByAbbrMap = new HashMap<String, State>();
            statesByNameMap = new HashMap<String, State>();

            for (State state : stateDao.list("name")) {
                statesList.add(CareCoordinationUtils.createKeyValueDto(state));
                statesByIdMap.put(state.getId(), state);
                statesByAbbrMap.put(state.getAbbr(), state);
                statesByNameMap.put(state.getName(), state);
            }
        }

    }

}
