package com.scnsoft.eldermark.shared.ccd.converters;

import com.scnsoft.eldermark.entity.Address;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.ccd.OrganizationDto;
import com.scnsoft.eldermark.shared.ccd.TelecomDto;
import org.dozer.DozerBeanMapper;
import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CustodianOrganizationConverter extends DozerConverter<Organization, OrganizationDto> {
    @Autowired
    private DozerBeanMapper mapper;

    public CustodianOrganizationConverter() throws Exception {
        this(Organization.class, OrganizationDto.class);
    }

    public CustodianOrganizationConverter(Class<Organization> prototypeA, Class<OrganizationDto> prototypeB) {
        super(prototypeA, prototypeB);
    }

    @Override
    public OrganizationDto convertTo(Organization source, OrganizationDto destination) {
        if(source == null) {
            return null;
        }

        destination = new OrganizationDto();

        destination.setName(source.getName());
        if (source.getTelecom() != null)
            destination.setTelecom(mapper.map(source.getTelecom(), TelecomDto.class));

        if (source.getAddresses() != null) {
            List<AddressDto> addressDtos = new ArrayList<AddressDto>(1);
            Address dboToMap = null;
            for (Address dbo : source.getAddresses()) {
                if ("WP".equals(dbo.getPostalAddressUse())) {
                    dboToMap = dbo;
                }
            }
            if (dboToMap != null) {
                addressDtos.add(mapper.map(dboToMap, AddressDto.class));
                destination.setAddresses(addressDtos);
            }
        }

        return destination;
    }

    @Override
    public Organization convertFrom(OrganizationDto source, Organization destination) {
        throw new UnsupportedOperationException();
    }
}
