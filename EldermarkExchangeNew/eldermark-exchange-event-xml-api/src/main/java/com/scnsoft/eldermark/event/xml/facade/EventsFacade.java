package com.scnsoft.eldermark.event.xml.facade;

import com.scnsoft.eldermark.event.xml.dto.DeviceEventProcessingResultDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface EventsFacade {

    void processEvents(HttpServletRequest request) throws IOException, JAXBException;

    DeviceEventProcessingResultDto processDeviceEvents(HttpServletRequest request) throws IOException, JAXBException;
}
