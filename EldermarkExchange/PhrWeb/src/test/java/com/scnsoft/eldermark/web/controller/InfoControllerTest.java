package com.scnsoft.eldermark.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author phomal
 * Created on 6/14/2017.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class InfoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    //@Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    //@Test
    public void getVitalSignReferenceInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/info/vitalSigns/RESP/referenceInfo").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.body.data").exists())
                .andExpect(jsonPath("$.body.data").isString())
                .andDo(print());
    }

}