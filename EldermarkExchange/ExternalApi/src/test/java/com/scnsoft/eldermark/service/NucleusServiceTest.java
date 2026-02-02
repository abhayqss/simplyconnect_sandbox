package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.externalapi.NucleusDeviceDao;
import com.scnsoft.eldermark.dao.externalapi.NucleusInfoDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.externalapi.NucleusDevice;
import com.scnsoft.eldermark.entity.externalapi.NucleusInfo;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.NucleusDeviceDto;
import com.scnsoft.eldermark.web.entity.NucleusInfoDto;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 2/15/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class NucleusServiceTest {
    @Mock
    private PrivilegesService privilegesService;
    @Mock
    private NucleusInfoDao nucleusInfoDao;
    @Mock
    private NucleusDeviceDao nucleusDeviceDao;
    @Mock
    private EmployeesService employeesService;
    @Mock
    private ResidentsService residentsService;

    @InjectMocks
    private NucleusService nucleusService;

    @Captor
    private ArgumentCaptor<NucleusInfo> nucleusInfoCaptor;


    protected final Long residentId = TestDataGenerator.randomId();
    protected final Long employeeId = TestDataGenerator.randomId();
    protected final String nucleusUserId = UUID.randomUUID().toString();
    protected final String nucleusDeviceId = UUID.randomUUID().toString();
    protected final NucleusInfo actualNucleusInfo = new NucleusInfo();
    protected final NucleusDevice actualNucleusDevice = new NucleusDevice();

    {
        actualNucleusInfo.setId(TestDataGenerator.randomId());
        actualNucleusInfo.setNucleusUserId(nucleusUserId);
        actualNucleusDevice.setId(TestDataGenerator.randomId());
        actualNucleusDevice.setNucleusId(nucleusDeviceId);
    }

    private void setupMockitoExpectations(NucleusInfo nucleusInfo) {
        when(privilegesService.canManageNucleusData()).thenReturn(Boolean.TRUE);
        when(nucleusInfoDao.findOneByEmployeeId(employeeId)).thenReturn(nucleusInfo);
        when(nucleusInfoDao.findOneByResidentId(residentId)).thenReturn(nucleusInfo);
        when(nucleusInfoDao.save(any(NucleusInfo.class))).then(returnsFirstArg());
    }

    private void setupMockitoExpectations(List<NucleusDevice> nucleusDevices) {
        when(privilegesService.canManageNucleusData()).thenReturn(Boolean.TRUE);
        when(nucleusDeviceDao.getAllByEmployeeId(employeeId)).thenReturn(nucleusDevices);
        when(nucleusDeviceDao.getAllByResidentId(residentId)).thenReturn(nucleusDevices);
        when(nucleusDeviceDao.save(any(NucleusDevice.class))).then(returnsFirstArg());
        when(nucleusDeviceDao.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(nucleusDevices));
    }

    private static NucleusInfoDto randomNucleusInfoDto() {
        final NucleusInfoDto newNucleusInfo = new NucleusInfoDto();
        newNucleusInfo.setUserId(UUID.randomUUID().toString());
        return newNucleusInfo;
    }


    @Test
    public void testUpdateInfoForEmployee() throws Exception {
        final String newNucleusUserId = UUID.randomUUID().toString();
        final NucleusInfoDto newNucleusInfo = new NucleusInfoDto();
        newNucleusInfo.setUserId(newNucleusUserId);

        setupMockitoExpectations(actualNucleusInfo);

        nucleusService.updateInfoForEmployee(employeeId, newNucleusInfo);

        verify(privilegesService).canManageNucleusData();
        verify(employeesService).checkAccessOrThrow(employeeId);

        verify(nucleusInfoDao).save(nucleusInfoCaptor.capture());
        final NucleusInfo persisted = nucleusInfoCaptor.getValue();
        assertNotNull(persisted);
        assertEquals(newNucleusUserId, persisted.getNucleusUserId());
    }

    @Test(expected = PhrException.class)
    public void testUpdateInfoForEmployeeThrowsNotFound() throws Exception {
        setupMockitoExpectations((NucleusInfo) null);

        nucleusService.updateInfoForEmployee(employeeId, randomNucleusInfoDto());
    }

    @Test(expected = PhrException.class)
    public void testUpdateInfoForEmployeeThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(employeesService).checkAccessOrThrow(employeeId);

        nucleusService.updateInfoForEmployee(employeeId, randomNucleusInfoDto());
    }

    @Test(expected = PhrException.class)
    public void testUpdateInfoForEmployeeThrowsAccessForbidden2() throws Exception {
        nucleusService.updateInfoForEmployee(employeeId, randomNucleusInfoDto());
    }

    @Test
    public void testGetInfoByEmployee() throws Exception {
        final NucleusInfoDto expected = new NucleusInfoDto();
        expected.setUserId(actualNucleusInfo.getNucleusUserId());

        setupMockitoExpectations(actualNucleusInfo);

        NucleusInfoDto result = nucleusService.getInfoByEmployee(employeeId);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canManageNucleusData();
        verify(employeesService).checkAccessOrThrow(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testGetInfoByEmployeeThrowsNotFound() throws Exception {
        setupMockitoExpectations((NucleusInfo) null);

        nucleusService.getInfoByEmployee(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testGetInfoByEmployeeThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(employeesService).checkAccessOrThrow(employeeId);

        nucleusService.getInfoByEmployee(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testGetInfoByEmployeeThrowsAccessForbidden2() throws Exception {
        nucleusService.getInfoByEmployee(employeeId);
    }

    @Test
    public void testGetDevicesByEmployee() throws Exception {
        final NucleusDeviceDto expectedDevice = new NucleusDeviceDto();
        expectedDevice.setId(actualNucleusDevice.getNucleusId());
        final List<NucleusDeviceDto> expected = Arrays.asList(expectedDevice);

        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));

        List<NucleusDeviceDto> result = nucleusService.getDevicesByEmployee(employeeId);

        assertEquals(expected, result);
        verify(privilegesService).canManageNucleusData();
        verify(employeesService).checkAccessOrThrow(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testGetDevicesByEmployeeThrowsAccessForbidden() throws Exception {
        nucleusService.getDevicesByEmployee(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testGetDevicesByEmployeeThrowsAccessForbidden2() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(employeesService).checkAccessOrThrow(employeeId);

        nucleusService.getDevicesByEmployee(employeeId);
    }

    @Test
    public void testDeleteInfoForEmployee() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);

        nucleusService.deleteInfoForEmployee(employeeId);
        verify(privilegesService).canManageNucleusData();
        verify(employeesService).checkAccessOrThrow(employeeId);
        verify(nucleusInfoDao).deleteByEmployeeId(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testDeleteInfoForEmployeeThrowsAccessForbidden() throws Exception {
        nucleusService.deleteInfoForEmployee(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testDeleteInfoForEmployeeThrowsAccessForbidden2() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(employeesService).checkAccessOrThrow(employeeId);

        nucleusService.deleteInfoForEmployee(employeeId);
    }

    @Test
    public void testCreateInfoForEmployee() throws Exception {
        final Employee employee = new Employee();
        employee.setId(employeeId);

        final NucleusInfoDto newInfo = randomNucleusInfoDto();
        final NucleusInfoDto expected = new NucleusInfoDto();
        expected.setUserId(newInfo.getUserId());

        setupMockitoExpectations(actualNucleusInfo);
        when(employeesService.getEntity(employeeId)).thenReturn(employee);

        NucleusInfoDto result = nucleusService.createInfoForEmployee(employeeId, newInfo);

        assertEquals(expected, result);
        verify(privilegesService).canManageNucleusData();
        verify(employeesService).checkAccessOrThrow(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testCreateInfoForEmployeeThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(employeesService).checkAccessOrThrow(employeeId);

        nucleusService.createInfoForEmployee(employeeId, randomNucleusInfoDto());
    }

    @Test(expected = PhrException.class)
    public void testCreateInfoForEmployeeThrowsAccessForbidden2() throws Exception {
        nucleusService.createInfoForEmployee(employeeId, randomNucleusInfoDto());
    }

    @Test
    public void testDeleteInfoForResident() throws Exception {
        when(privilegesService.canManageNucleusData()).thenReturn(Boolean.TRUE);

        nucleusService.deleteInfoForResident(residentId);
        verify(privilegesService).canManageNucleusData();
        verify(residentsService).checkAccessOrThrow(residentId);
        verify(nucleusInfoDao).deleteByResidentId(residentId);
    }

    @Test(expected = PhrException.class)
    public void testDeleteInfoForResidentThrowsAccessForbidden() throws Exception {
        nucleusService.deleteInfoForResident(residentId);
    }

    @Test(expected = PhrException.class)
    public void testDeleteInfoForResidentThrowsAccessForbidden2() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);

        nucleusService.deleteInfoForResident(residentId);
    }

    @Test
    public void testCreateDeviceForEmployee() throws Exception {
        final Employee employee = new Employee();
        employee.setId(employeeId);

        final String newNucleusDeviceId = UUID.randomUUID().toString();
        final NucleusDeviceDto newDevice = new NucleusDeviceDto();
        newDevice.setId(newNucleusDeviceId);
        final NucleusDeviceDto expected = new NucleusDeviceDto();
        expected.setId(newNucleusDeviceId);

        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));
        when(employeesService.getEntity(employeeId)).thenReturn(employee);

        NucleusDeviceDto result = nucleusService.createDeviceForEmployee(employeeId, newDevice);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canManageNucleusData();
        verify(employeesService).checkAccessOrThrow(employeeId);
    }

    @Test(expected = PhrException.class)
    public void testCreateDeviceForEmployeeThrowsAccessForbidden() throws Exception {
        nucleusService.createDeviceForEmployee(employeeId, new NucleusDeviceDto());
    }

    @Test(expected = PhrException.class)
    public void testCreateDeviceForEmployeeThrowsAccessForbidden2() throws Exception {
        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(employeesService).checkAccessOrThrow(employeeId);
        when(employeesService.getEntity(employeeId)).thenReturn(new Employee());

        nucleusService.createDeviceForEmployee(employeeId, new NucleusDeviceDto());
    }

    @Test
    public void testUpdateInfoForResident() throws Exception {
        final Resident resident = new Resident(residentId);

        final String newNucleusUserId = UUID.randomUUID().toString();
        final NucleusInfoDto newInfo = new NucleusInfoDto();
        newInfo.setUserId(newNucleusUserId);
        final NucleusInfoDto expected = new NucleusInfoDto();
        expected.setUserId(newNucleusUserId);

        setupMockitoExpectations(actualNucleusInfo);
        when(residentsService.getEntity(residentId)).thenReturn(resident);

        nucleusService.updateInfoForResident(residentId, newInfo);

        verify(privilegesService).canManageNucleusData();
        verify(residentsService).checkAccessOrThrow(residentId);

        verify(nucleusInfoDao).save(nucleusInfoCaptor.capture());
        final NucleusInfo persisted = nucleusInfoCaptor.getValue();
        assertNotNull(persisted);
        assertEquals(newNucleusUserId, persisted.getNucleusUserId());
    }

    @Test(expected = PhrException.class)
    public void testUpdateInfoForResidentThrowsNotFound() throws Exception {
        setupMockitoExpectations((NucleusInfo) null);

        nucleusService.updateInfoForResident(residentId, randomNucleusInfoDto());
    }

    @Test(expected = PhrException.class)
    public void testUpdateInfoForResidentThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);
        when(residentsService.getEntity(residentId)).thenReturn(new Resident(residentId));

        nucleusService.updateInfoForResident(residentId, randomNucleusInfoDto());
    }

    @Test(expected = PhrException.class)
    public void testUpdateInfoForResidentThrowsAccessForbidden2() throws Exception {
        when(residentsService.getEntity(residentId)).thenReturn(new Resident(residentId));
        nucleusService.updateInfoForResident(residentId, randomNucleusInfoDto());
    }

    @Test
    public void testGetInfoByResident() throws Exception {
        final NucleusInfoDto expected = new NucleusInfoDto();
        expected.setUserId(nucleusUserId);

        setupMockitoExpectations(actualNucleusInfo);

        NucleusInfoDto result = nucleusService.getInfoByResident(residentId);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canManageNucleusData();
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test(expected = PhrException.class)
    public void testGetInfoByResidentThrowsNotFound() throws Exception {
        setupMockitoExpectations((NucleusInfo) null);

        nucleusService.getInfoByResident(residentId);
    }

    @Test(expected = PhrException.class)
    public void testGetInfoByResidentThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);

        nucleusService.getInfoByResident(residentId);
    }

    @Test(expected = PhrException.class)
    public void testGetInfoByResidentThrowsAccessForbidden2() throws Exception {
        nucleusService.getInfoByResident(residentId);
    }

    @Test
    public void testGetDevicesByResident() throws Exception {
        final NucleusDeviceDto expectedDevice = new NucleusDeviceDto();
        expectedDevice.setId(actualNucleusDevice.getNucleusId());
        final List<NucleusDeviceDto> expected = Arrays.asList(expectedDevice);

        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));

        List<NucleusDeviceDto> result = nucleusService.getDevicesByResident(residentId);

        assertEquals(expected, result);
        verify(privilegesService).canManageNucleusData();
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test(expected = PhrException.class)
    public void testGetDevicesByResidentThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);

        nucleusService.getDevicesByResident(residentId);
    }

    @Test(expected = PhrException.class)
    public void testGetDevicesByResidentThrowsAccessForbidden2() throws Exception {
        nucleusService.getDevicesByResident(residentId);
    }

    @Test
    public void testCreateInfoForResident() throws Exception {
        final Resident resident = new Resident();
        resident.setId(residentId);

        final NucleusInfoDto newInfo = randomNucleusInfoDto();
        final NucleusInfoDto expected = new NucleusInfoDto();
        expected.setUserId(newInfo.getUserId());

        setupMockitoExpectations(actualNucleusInfo);
        when(residentsService.getEntity(residentId)).thenReturn(resident);

        NucleusInfoDto result = nucleusService.createInfoForResident(residentId, newInfo);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canManageNucleusData();
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test(expected = PhrException.class)
    public void testCreateInfoForResidentThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(actualNucleusInfo);
        when(residentsService.getEntity(residentId)).thenReturn(new Resident(residentId));
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);

        nucleusService.createInfoForResident(residentId, randomNucleusInfoDto());
    }

    @Test(expected = PhrException.class)
    public void testCreateInfoForResidentThrowsAccessForbidden2() throws Exception {
        when(residentsService.getEntity(residentId)).thenReturn(new Resident(residentId));
        nucleusService.createInfoForResident(residentId, randomNucleusInfoDto());
    }

    @Test
    public void testCreateDeviceForResident() throws Exception {
        final String newNucleusDeviceId = UUID.randomUUID().toString();
        final NucleusDeviceDto newDevice = new NucleusDeviceDto();
        newDevice.setId(newNucleusDeviceId);
        final NucleusDeviceDto expected = new NucleusDeviceDto();
        expected.setId(newNucleusDeviceId);

        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));
        when(residentsService.getEntity(residentId)).thenReturn(new Resident(residentId));

        NucleusDeviceDto result = nucleusService.createDeviceForResident(residentId, newDevice);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canManageNucleusData();
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test(expected = PhrException.class)
    public void testCreateDeviceForResidentThrowsAccessForbidden() throws Exception {
        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));
        when(residentsService.getEntity(residentId)).thenReturn(new Resident(residentId));
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);

        nucleusService.createDeviceForResident(residentId, new NucleusDeviceDto());
    }

    @Test(expected = PhrException.class)
    public void testCreateDeviceForResidentThrowsAccessForbidden2() throws Exception {
        when(residentsService.getEntity(residentId)).thenReturn(new Resident(residentId));

        nucleusService.createDeviceForResident(residentId, new NucleusDeviceDto());
    }

    @Test
    public void testGetAllDevices() throws Exception {
        final NucleusDevice device = new NucleusDevice();
        device.setNucleusId(UUID.randomUUID().toString());
        device.setLocation(TestDataGenerator.randomName());
        device.setType(TestDataGenerator.randomName());

        final NucleusDeviceDto expectedDevice = new NucleusDeviceDto();
        expectedDevice.setId(actualNucleusDevice.getNucleusId());

        final NucleusDeviceDto expectedDevice2 = new NucleusDeviceDto();
        expectedDevice2.setId(device.getNucleusId());
        expectedDevice2.setLocation(device.getLocation());
        expectedDevice2.setType(device.getType());

        final List<NucleusDevice> actualNucleusDevices = Arrays.asList(actualNucleusDevice, device);
        final NucleusDeviceDto[] expected = {expectedDevice, expectedDevice2};

        setupMockitoExpectations(actualNucleusDevices);

        Page<NucleusDeviceDto> result = nucleusService.getAllDevices(new PageRequest(0, 20));

        assertThat(result.getContent(), containsInAnyOrder(expected));
        verify(privilegesService).canManageNucleusData();
    }

    @Test(expected = PhrException.class)
    public void testGetAllDevicesThrowsAccessForbidden() throws Exception {
        nucleusService.getAllDevices(new PageRequest(0, 20));
    }

    @Ignore("not implemented")
    @Test
    public void testGetDevice() throws Exception {
        final NucleusDeviceDto expected = new NucleusDeviceDto();
        expected.setId(actualNucleusDevice.getNucleusId());

        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));

        NucleusDeviceDto result = nucleusService.getDevice(UUID.fromString(nucleusDeviceId));

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canManageNucleusData();
    }

    @Test(expected = PhrException.class)
    public void testGetDeviceThrowsAccessForbidden() throws Exception {
        nucleusService.getDevice(UUID.fromString(nucleusDeviceId));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        setupMockitoExpectations(Arrays.asList(actualNucleusDevice));

        nucleusService.deleteDevice(UUID.fromString(nucleusDeviceId));

        verify(privilegesService).canManageNucleusData();
        verify(nucleusDeviceDao).deleteByNucleusId(nucleusDeviceId);
    }

    @Test(expected = PhrException.class)
    public void testDeleteDeviceThrowsAccessForbidden() throws Exception {
        nucleusService.deleteDevice(UUID.fromString(nucleusDeviceId));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme