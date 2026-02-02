package com.scnsoft.eldermark.service;

import java.util.Optional;

import com.scnsoft.eldermark.entity.State;

public interface StateService {
    Optional<State> findById(Long id);

    State findByAbbr(String abbr);

    State findByAbbrOrFullName(String abbr, String name);
}
