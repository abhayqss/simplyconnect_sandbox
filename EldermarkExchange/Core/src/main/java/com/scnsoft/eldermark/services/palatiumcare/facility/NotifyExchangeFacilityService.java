package com.scnsoft.eldermark.services.palatiumcare.facility;

import com.scnsoft.eldermark.dao.palatiumcare.FacilityDao;
import com.scnsoft.eldermark.entity.palatiumcare.Facility;
import com.scnsoft.eldermark.shared.palatiumcare.facility.PalCareFacilityInDto;
import com.scnsoft.eldermark.shared.palatiumcare.facility.PalCareFacilityOutDto;
import com.scnsoft.eldermark.util.EldermarkCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotifyExchangeFacilityService {

    private FacilityDao facilityDao;

    @Autowired
    public void setFacilityDao(FacilityDao facilityDao) {
        this.facilityDao = facilityDao;
    }

    private Facility transformInDtoToFacility(PalCareFacilityInDto palCareFacilityInDto) {
        Facility facility = new Facility();
        facility.setPalCareId(palCareFacilityInDto.getPalCareId());
        facility.setName(palCareFacilityInDto.getName());
        facility.setLabel(palCareFacilityInDto.getLabel());
        return facility;
    }

    private PalCareFacilityOutDto transformFromFromFacilityToOutDto(Facility facility) {
        return new PalCareFacilityOutDto(facility.getId(), facility.getName(), facility.getLabel());
    }

    private List<PalCareFacilityOutDto> transformToOutDtoList(List<Facility> facilityList) {
        List<PalCareFacilityOutDto> dtoList = new ArrayList<>();
        for (Facility facility : facilityList) {
            dtoList.add(transformFromFromFacilityToOutDto(facility));
        }
        return dtoList;
    }

    private List<Facility> transformFromInDto(List<PalCareFacilityInDto> dtoList) {
        List<Facility> facilityList = new ArrayList<>();
        for (PalCareFacilityInDto dto : dtoList) {
            facilityList.add(transformInDtoToFacility(dto));
        }
        return facilityList;
    }

    @Transactional(readOnly = true)
    public List<PalCareFacilityOutDto> getAllFacilities() {
        Iterable<Facility> facilityIterable = facilityDao.findAll();
        List<Facility> facilityList = EldermarkCollectionUtils.listFromIterable(facilityIterable);
        return transformToOutDtoList(facilityList);
    }

    @Transactional
    public void createFacility(PalCareFacilityInDto palCareFacilityInDto) {
        Facility facility = transformInDtoToFacility(palCareFacilityInDto);
        facilityDao.save(facility);
    }

    @Transactional(readOnly = true)
    public PalCareFacilityOutDto getFacility(Long id) {
        Facility facility = facilityDao.findOne(id);
        return transformFromFromFacilityToOutDto(facility);
    }

    @Transactional
    public void addFacilityList(List<PalCareFacilityInDto> palCareFacilityInDtoList) {
        List<Facility> facilityList = transformFromInDto(palCareFacilityInDtoList);
        facilityDao.save(facilityList);
    }
}
