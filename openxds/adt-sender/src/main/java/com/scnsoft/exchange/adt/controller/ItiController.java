package com.scnsoft.exchange.adt.controller;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by averazub on 10/6/2016.
 */
public class ItiController {


    @Value("${default.host}")
    protected String defaultHost;

    protected String getContentFromFile(Resource resource) {
        try {
            FileInputStream fis = new FileInputStream(resource.getFile());
            byte[] b = IOUtils.toByteArray(fis);
            return new String(b);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
