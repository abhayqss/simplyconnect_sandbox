package com.scnsoft.eldermark.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    @Lazy
    private Validator validator;

    @Override
    public void validate(Object object, Class<?>... groups) {
        if (validator != null) {
            var violations = validator.validate(object, groups);
            if (CollectionUtils.isNotEmpty(violations)) {
                throw new ConstraintViolationException(violations);
            }
        }
    }
}
