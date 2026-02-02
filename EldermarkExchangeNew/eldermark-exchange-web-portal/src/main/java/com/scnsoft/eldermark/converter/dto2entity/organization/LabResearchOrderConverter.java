package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dao.SpecimenTypeDao;
import com.scnsoft.eldermark.dto.lab.ClientSummaryLabsAdaptDto;
import com.scnsoft.eldermark.dto.lab.LabOrderSpecimenDto;
import com.scnsoft.eldermark.dto.lab.LabResearchOrderDto;
import com.scnsoft.eldermark.entity.lab.LabOrderPolicyHolder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderReason;
import com.scnsoft.eldermark.service.CcdCodeService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.StateService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LabResearchOrderConverter implements Converter<LabResearchOrderDto, LabResearchOrder> {

    @Autowired
    private StateService stateService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private SpecimenTypeDao specimenTypeDao;

    @Autowired
    private CcdCodeService ccdCodeService;

    @Override
    public LabResearchOrder convert(LabResearchOrderDto source) {
        var target = new LabResearchOrder();
        target.setReason(LabResearchOrderReason.valueOf(source.getReason()));
        target.setClinic(source.getClinic());
        target.setClinicAddress(source.getClinicAddress());
        target.setNotes(source.getNotes());
        target.setCreatedBy(loggedUserService.getCurrentEmployee());
        target.setIsCovid19(true);
        target.setProviderFirstName(source.getProviderFirstName());
        target.setProviderLastName(source.getProviderLastName());
        target.setOrderDate(DateTimeUtils.toInstant(source.getOrderDate()));
        target.setIcd10Codes(source.getIcd10Codes());
        fillClientInfo(source.getClient(), target);
        fillSpecimens(source.getSpecimen(), target);
        return target;
    }

    private void fillClientInfo(ClientSummaryLabsAdaptDto source, LabResearchOrder target) {
        target.setClient(clientService.findById(source.getId()));
        target.setPhone(source.getPhone());
        target.setAddress(source.getAddress().getStreet());
        target.setCity(source.getAddress().getCity());
        target.setZipCode(source.getAddress().getZip());
        target.setState(stateService.findById(source.getAddress().getStateId()).orElse(null));
        target.setInNetworkInsurance(source.getInsuranceNetwork());
        target.setPolicyNumber(source.getPolicyNumber());
        target.setPolicyHolder(LabOrderPolicyHolder.valueOf(source.getPolicyHolderRelationName()));
        target.setPolicyHolderName(source.getPolicyHolderName());
        target.setPolicyHolderDOB(DateTimeUtils.parseDateToLocalDate(source.getPolicyHolderDOB()));
        target.setGender(ccdCodeService.findById(source.getGenderId()));
        target.setRace(ccdCodeService.findById(source.getRaceId()));
        target.setBirthDate(DateTimeUtils.parseDateToLocalDate(source.getBirthDate()));
    }

    private void fillSpecimens(LabOrderSpecimenDto source, LabResearchOrder target) {
        target.setSpecimenTypes(source.getTypes()
                .stream()
                .map(identifiedNamedTitledEntityDto -> specimenTypeDao.getOne(identifiedNamedTitledEntityDto.getId()))
                .collect(Collectors.toList()));
        target.setCollectorsName(source.getCollectorName());
        target.setSite(source.getSite());
        target.setSpecimenDate(DateTimeUtils.toInstant(source.getDate()));
    }
}
