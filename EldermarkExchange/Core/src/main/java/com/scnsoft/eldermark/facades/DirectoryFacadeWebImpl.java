package com.scnsoft.eldermark.facades;

import com.scnsoft.eldermark.dto.dictionary.FreeTextKeyValueDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentLevelReportingSettingsDto;
import com.scnsoft.eldermark.dto.dictionary.IncidentTypeDto;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.incident.ClassMemberType;
import com.scnsoft.eldermark.entity.incident.IncidentPlaceType;
import com.scnsoft.eldermark.entity.incident.IncidentType;
import com.scnsoft.eldermark.entity.incident.IncidentTypeHelp;
import com.scnsoft.eldermark.entity.incident.Race;
import com.scnsoft.eldermark.services.DirectoryService;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.StateDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly=true)
public class DirectoryFacadeWebImpl implements DirectoryFacadeWeb {
    
    @Autowired
    private ListAndItemTransformer<ClassMemberType, KeyValueDto> classMemberTypeEntityToDtoConverter;
    
    @Autowired
    private ListAndItemTransformer<IncidentPlaceType, FreeTextKeyValueDto> incidentPlaceTypeEntityToDtoConverter;
    
    @Autowired
    private ListAndItemTransformer<Race, KeyValueDto> raceEntityToDtoConverter;

    @Autowired
    private Converter<IncidentTypeHelp, IncidentLevelReportingSettingsDto> incidentTypeHelpEntityToDtoConverter;

    @Autowired
    private Converter<List<IncidentType>, List<IncidentTypeDto>> incidentTypeListEntityToDtoConverter;

    @Autowired
    private ListAndItemTransformer<CcdCode, KeyValueDto> ccdCodeToDtoConverter;
    
    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private StateService stateService;

    @Override
    @Transactional(readOnly=true)
    public List<KeyValueDto> getClassMemberTypes() {
        return classMemberTypeEntityToDtoConverter.convertList(directoryService.getClassMemberTypes());
    }

    @Override
    @Transactional(readOnly=true)
    public List<FreeTextKeyValueDto> getIncidentPlaceTypes() {
        return incidentPlaceTypeEntityToDtoConverter.convertList(directoryService.getIncidentPlaceTypes());
    }

    @Override
    @Transactional(readOnly=true)
    public List<KeyValueDto> getRaces(){
        return raceEntityToDtoConverter.convertList(directoryService.getRaces());
    }
    
    @Override
    @Transactional(readOnly=true)
    public IncidentLevelReportingSettingsDto getIncidentTypeHelp(Integer incidentLevel) {
        return incidentTypeHelpEntityToDtoConverter.convert(directoryService.getIncidentTypeHelp(incidentLevel));
    }

    @Override
    @Transactional(readOnly=true)
    public List<IncidentTypeDto> getIncidentTypes(Integer incidentLevel) {
        return incidentTypeListEntityToDtoConverter.convert(directoryService.getIncidentTypes(incidentLevel));
    }

    @Override
    @Transactional(readOnly=true)
    public List<KeyValueDto> getGenders() {
        return ccdCodeToDtoConverter.convertList(directoryService.getGenders());
    }

    @Override
    @Transactional(readOnly=true)
    public List<StateDto> getStates() {
        return stateService.getAllStates();
    }
}
