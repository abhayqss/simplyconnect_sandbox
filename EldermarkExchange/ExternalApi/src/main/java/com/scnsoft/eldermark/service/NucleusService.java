package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.externalapi.NucleusDeviceDao;
import com.scnsoft.eldermark.dao.externalapi.NucleusInfoDao;
import com.scnsoft.eldermark.entity.externalapi.NucleusDevice;
import com.scnsoft.eldermark.entity.externalapi.NucleusInfo;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.NucleusDeviceDto;
import com.scnsoft.eldermark.web.entity.NucleusInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author phomal
 * Created on 1/31/2018.
 */
@Service
@Transactional
public class NucleusService {

    private final PrivilegesService privilegesService;
    private final NucleusInfoDao nucleusInfoDao;
    private final NucleusDeviceDao nucleusDeviceDao;
    private final EmployeesService employeesService;
    private final ResidentsService residentsService;

    @Autowired
    public NucleusService(PrivilegesService privilegesService, NucleusInfoDao nucleusInfoDao, NucleusDeviceDao nucleusDeviceDao,
                          EmployeesService employeesService, ResidentsService residentsService) {
        this.privilegesService = privilegesService;
        this.nucleusInfoDao = nucleusInfoDao;
        this.nucleusDeviceDao = nucleusDeviceDao;
        this.employeesService = employeesService;
        this.residentsService = residentsService;
    }


    public void updateInfoForEmployee(Long employeeId, NucleusInfoDto body) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        employeesService.checkAccessOrThrow(employeeId);

        final NucleusInfo info = nucleusInfoDao.findOneByEmployeeId(employeeId);
        if (info == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }
        updateInfo(info, body);
    }

    @Transactional(readOnly = true)
    public NucleusInfoDto getInfoByEmployee(Long employeeId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        employeesService.checkAccessOrThrow(employeeId);

        final NucleusInfo info = nucleusInfoDao.findOneByEmployeeId(employeeId);
        if (info == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }

        return convert(info);
    }

    @Transactional(readOnly = true)
    public List<NucleusDeviceDto> getDevicesByEmployee(Long employeeId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        employeesService.checkAccessOrThrow(employeeId);

        return convert(nucleusDeviceDao.getAllByEmployeeId(employeeId));
    }

    public void deleteInfoForEmployee(Long employeeId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        employeesService.checkAccessOrThrow(employeeId);

        nucleusInfoDao.deleteByEmployeeId(employeeId);
    }

    public NucleusInfoDto createInfoForEmployee(Long employeeId, NucleusInfoDto body) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        employeesService.checkAccessOrThrow(employeeId);

        NucleusInfo info = new NucleusInfo();
        info.setEmployee(employeesService.getEntity(employeeId));
        info.setNucleusUserId(String.valueOf(UUID.fromString(body.getUserId())));
        info.setFamilyCareTeamMemberId(body.getFamilyCareTeamMemberId());

        return convert(nucleusInfoDao.save(info));
    }

    public NucleusDeviceDto createDeviceForEmployee(Long employeeId, NucleusDeviceDto body) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        employeesService.checkAccessOrThrow(employeeId);

        NucleusDevice device = new NucleusDevice();
        device.setEmployee(employeesService.getEntity(employeeId));
        device.setNucleusId(String.valueOf(body.getId()));
        device.setLocation(body.getLocation());
        device.setType(body.getType());

        return convert(nucleusDeviceDao.save(device));
    }

// ================================================================================================


    public void updateInfoForResident(Long residentId, NucleusInfoDto body) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        residentsService.checkAccessOrThrow(residentId);

        final NucleusInfo info = nucleusInfoDao.findOneByResidentId(residentId);
        if (info == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }
        updateInfo(info, body);
    }

    private void updateInfo(NucleusInfo info, NucleusInfoDto body) {
        info.setNucleusUserId(String.valueOf(UUID.fromString(body.getUserId())));
        info.setFamilyCareTeamMemberId(body.getFamilyCareTeamMemberId());
        nucleusInfoDao.save(info);
    }

    @Transactional(readOnly = true)
    public NucleusInfoDto getInfoByResident(Long residentId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        residentsService.checkAccessOrThrow(residentId);

        final NucleusInfo info = nucleusInfoDao.findOneByResidentId(residentId);
        if (info == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND);
        }

        return convert(info);
    }

    @Transactional(readOnly = true)
    public List<NucleusDeviceDto> getDevicesByResident(Long residentId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        residentsService.checkAccessOrThrow(residentId);

        return convert(nucleusDeviceDao.getAllByResidentId(residentId));
    }

    public void deleteInfoForResident(Long residentId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        residentsService.checkAccessOrThrow(residentId);

        nucleusInfoDao.deleteByResidentId(residentId);
    }

    public NucleusInfoDto createInfoForResident(Long residentId, NucleusInfoDto body) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        residentsService.checkAccessOrThrow(residentId);

        NucleusInfo info = new NucleusInfo();
        info.setResident(residentsService.getEntity(residentId));
        info.setNucleusUserId(String.valueOf(UUID.fromString(body.getUserId())));
        info.setFamilyCareTeamMemberId(body.getFamilyCareTeamMemberId());

        return convert(nucleusInfoDao.save(info));
    }

    public NucleusDeviceDto createDeviceForResident(Long residentId, NucleusDeviceDto body) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        residentsService.checkAccessOrThrow(residentId);

        NucleusDevice device = new NucleusDevice();
        device.setResident(residentsService.getEntity(residentId));
        device.setNucleusId(String.valueOf(body.getId()));
        device.setLocation(body.getLocation());
        device.setType(body.getType());

        return convert(nucleusDeviceDao.save(device));
    }

// ================================================================================================


    @Transactional(readOnly = true)
    public Page<NucleusDeviceDto> getAllDevices(Pageable pageable) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        return convert(nucleusDeviceDao.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public NucleusDeviceDto getDevice(UUID deviceId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        // TODO implement
        return null;
    }

    public void deleteDevice(UUID deviceId) {
        if (!privilegesService.canManageNucleusData()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        nucleusDeviceDao.deleteByNucleusId(String.valueOf(deviceId));
    }

// ================================================================================================


    private static NucleusInfoDto convert(NucleusInfo src) {
        NucleusInfoDto dto = new NucleusInfoDto();
        dto.setUserId(src.getNucleusUserId());
        dto.setFamilyCareTeamMemberId(src.getFamilyCareTeamMemberId());

        return dto;
    }

    private static List<NucleusDeviceDto> convert(List<NucleusDevice> nucleusDevices) {
        List<NucleusDeviceDto> dtos = new ArrayList<>(nucleusDevices.size());
        for (NucleusDevice nucleusDevice : nucleusDevices) {
            dtos.add(convert(nucleusDevice));
        }

        return dtos;
    }

    private static Page<NucleusDeviceDto> convert(Page<NucleusDevice> devices) {
        return devices.map(new Converter<NucleusDevice, NucleusDeviceDto>() {
            @Override
            public NucleusDeviceDto convert(NucleusDevice source) {
                return NucleusService.convert(source);
            }
        });
    }

    private static NucleusDeviceDto convert(NucleusDevice src) {
        if (src == null) {
            return null;
        }
        NucleusDeviceDto dto = new NucleusDeviceDto();
        dto.setId(src.getNucleusId());
        dto.setLocation(src.getLocation());
        dto.setType(src.getType());

        return dto;
    }

}
