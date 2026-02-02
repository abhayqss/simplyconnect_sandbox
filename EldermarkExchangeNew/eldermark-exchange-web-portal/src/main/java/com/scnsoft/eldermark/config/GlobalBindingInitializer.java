package com.scnsoft.eldermark.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class GlobalBindingInitializer {

    @Value("${request.collection.limit}")
    private int requestCollectionLimit;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setAutoGrowCollectionLimit(requestCollectionLimit);
    }
}
