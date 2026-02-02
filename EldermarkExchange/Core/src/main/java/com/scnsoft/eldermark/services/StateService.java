package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.shared.StateDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;

import java.util.List;

/**
 * @author averazub
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 05-Oct-15.
 */
public interface StateService {

    List<KeyValueDto> getStates();

    List<StateDto> getAllStates();

    public State findByAbbr(String abbr);

    public State findByAbbrOrFullName(String fullName);
    public State get(Long id);
}
