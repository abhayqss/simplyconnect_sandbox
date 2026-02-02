package com.scnsoft.eldermark.config;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by averazub on 1/3/2017.
 */
@Component
public class DozerConfig {

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @PostConstruct
    public void postConstruct() {
        try {
            ClassPathResource resource = new ClassPathResource("dozer-phr.xml");
            dozerBeanMapper.addMapping(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
