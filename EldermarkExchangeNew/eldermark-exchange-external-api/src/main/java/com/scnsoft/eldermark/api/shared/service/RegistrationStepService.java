package com.scnsoft.eldermark.api.shared.service;

import com.scnsoft.eldermark.api.external.dao.RegistrationStepDao;
import com.scnsoft.eldermark.api.external.entity.RegistrationApplication;
import com.scnsoft.eldermark.api.external.entity.RegistrationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author phomal
 * Created on 1/19/2018.
 */
@Service
public class RegistrationStepService {

    @Autowired
    private RegistrationStepDao registrationStepDao;

    private Map<String, RegistrationStep> stepsByNameMap = null;

    @PostConstruct
    private void fillSteps() {
        if (stepsByNameMap == null) {
            stepsByNameMap = new HashMap<>();
            for (RegistrationStep step : registrationStepDao.findAll()) {
                stepsByNameMap.put(step.getName(), step);
            }
        }
    }

    public RegistrationStep findByName(String name) {
        return stepsByNameMap.get(name);
    }

    public RegistrationStep convert(RegistrationApplication.Step step) {
        return findByName(step.getNameDb());
    }

    public Collection<RegistrationStep> excludeCompleted() {
        return Collections.singleton(findByName(RegistrationApplication.Step.COMPLETED.getNameDb()));
    }

}
