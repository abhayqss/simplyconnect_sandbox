package com.scnsoft.service;

//import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.scnsoft.TestApplicationH2Config;
import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import com.scnsoft.eldermark.entity.palatiumcare.Location;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationH2Config.class })
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
       // TransactionDbUnitTestExecutionListener.class
})
@Transactional
public class PalCareLocationServiceTest {

    private PalCareLocationService locationService;

    private static List<Long> locationIdList = new ArrayList<>();

    @Autowired
    public void setPalCareLocationService(@Qualifier("palCareLocationService") PalCareLocationService locationService) {
        this.locationService = locationService;
    }

    @Test
    public void saveNewItemTest() {
        NotifyLocationDto locationDto = new NotifyLocationDto();
        NotifyBuildingDto notifyBuildingDto =  new NotifyBuildingDto("Building A", null, null, null, null);
        locationDto.setBuilding(notifyBuildingDto);
        locationDto.setRoom("Hospital");
        Location location = locationService.save(locationDto);
        assertNotNull(location);
        Long locationId = location.getId();
        assert(locationId >= 0);
        locationIdList.add(locationId);
    }

    @Test
    public void getItemById() {
        NotifyLocationDto locationDto = new NotifyLocationDto();
        NotifyBuildingDto notifyBuildingDto = new NotifyBuildingDto("Building B", null, null, null, null);
        locationDto.setBuilding(notifyBuildingDto);
        locationDto.setRoom("Hospital");
        Location location = locationService.save(locationDto);
        assertNotNull(location);
        Long locationId = location.getId();
        assert(locationId >= 0);
        locationIdList.add(locationId);
        NotifyLocationDto fetchedLocationDto = locationService.get(locationId);
        assert (locationDto.getRoom().equals(fetchedLocationDto.getRoom()));
        assert (locationDto.getBuilding().equals(fetchedLocationDto.getBuilding()));
    }

    @Test
    public void getList() {
        List<NotifyLocationDto> locationDtoList = locationService.getList();
        assert (locationDtoList.size() >= 2);
    }

    @AfterClass
    public static void clean() {
        for (Long locationId : locationIdList) {
            locationIdList.remove(locationId);
        }
    }

}
