package com.scnsoft.eldermark.converter.event.base;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.converter.hl7.entity2dto.datatype.CodedValueForHL7TableConverter;
import com.scnsoft.eldermark.dao.AdtMessageDao;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.event.ClientSummaryViewData;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.facesheet.Language;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.CXExtendedCompositeId;
import com.scnsoft.eldermark.entity.xds.datatype.XADPatientAddress;
import com.scnsoft.eldermark.entity.xds.message.PIDSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.PIDPatientIdentificationSegment;
import com.scnsoft.eldermark.util.ClientUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class BaseClientSummaryViewDataConverter<C extends ClientSummaryViewData> implements ClientSummaryViewDataConverter<C> {

    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private Converter<XADPatientAddress, AddressDto> xadAddressConverter;

    @Autowired
    private Converter<Address, AddressDto> personAddressConverter;

    @Autowired
    private CodedValueForHL7TableConverter isCodedValueConverter;

    @Autowired
    private ListAndItemConverter<CECodedElement, String> ceToStringConverter;


    @Override
    public C convert(Client client) {
        var info = create();

        fill(client, info);

        return info;
    }

    protected void fill(Client client, C info) {
        info.setFullName(client.getFullName());

        info.setSsn(ClientUtils.formatSsn(client.getSsnLastFourDigits()));
        info.setBirthDate(DateTimeUtils.formatLocalDate(client.getBirthDate()));
        info.setGender(CcdUtils.displayName(client.getGender()));
        info.setMaritalStatus(CcdUtils.displayName(client.getMaritalStatus()));
        info.setRace(CcdUtils.displayName(client.getRace()));
        info.setEthnicGroup(CcdUtils.displayName(client.getRace()));
        info.setReligion(CcdUtils.displayName(client.getReligion()));

        info.setCitizenships(Collections.singletonList(client.getCitizenship()));
        info.setVeteranStatus(client.getVeteran());

        info.setHomePhone(PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.HP).orElse(null));
        info.setBusinessPhone(PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.WP).orElse(null));

        if (CollectionUtils.isNotEmpty(client.getPerson().getAddresses())) {
            var address = client.getPerson().getAddresses().get(0);
            info.setAddress(personAddressConverter.convert(address));
        }

        info.setOrganizationTitle(client.getOrganization().getName());
        info.setCommunityTitle(Optional.ofNullable(client.getCommunity()).map(Community::getName).orElse(null));
        info.setDeathDate(DateTimeUtils.toEpochMilli(client.getDeathDate()));
        info.setIsActive(client.getActive());
        info.setMaidenName(client.getMaidenName());
        if (CollectionUtils.isNotEmpty(client.getLanguages())) {
            info.setLanguages(client.getLanguages().stream()
                    .map(Language::getCode)
                    .filter(Objects::nonNull)
                    .map(CcdCode::getDisplayName)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList())
            );
        }
        info.setPreferredName(Optional.ofNullable(client.getPerson())
                .map(Person::getNames)
                .stream()
                .flatMap(Collection::stream)
                .map(Name::getPreferredName)
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse(null)
        );
        info.setPrefix(Optional.ofNullable(client.getPerson())
                .map(Person::getNames)
                .stream()
                .flatMap(Collection::stream)
                .map(Name::getPrefix)
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse(null)
        );
    }

    @Override
    public C convert(Event event) {
        var info = convert(event.getClient());

        if (event.getAdtMsgId() != null) {
            var adtMessage = adtMessageDao.findById(event.getAdtMsgId()).orElseThrow();

            if (adtMessage instanceof PIDSegmentContainingMessage && ((PIDSegmentContainingMessage) adtMessage).getPid() != null) {
                var pid = ((PIDSegmentContainingMessage) adtMessage).getPid();
                fillFromPID(pid, info);
            }
        }
        return info;
    }

    protected void fillFromPID(PIDPatientIdentificationSegment pid, C info) {
        var aliases = CollectionUtils.emptyIfNull(pid.getPatientAliases());
        info.setAliases(
                aliases.stream().map(CcdUtils::buildFullName).collect(Collectors.toList())
        );

        var identifiers = CollectionUtils.emptyIfNull(pid.getPatientIdentifiers());
        info.setIdentifiers(
                identifiers.stream().map(CXExtendedCompositeId::getpId).collect(Collectors.toList())
        );

        info.setBirthDate(DateTimeUtils.formatLocalDate(pid.getDateTimeOfBirth()));
        info.setGender(isCodedValueConverter.convert(pid.getAdministrativeSex()));
        info.setMaritalStatus(ceToStringConverter.convert(pid.getMaritalStatus()));
        info.setPrimaryLanguage(CcdUtils.getIdentifier(pid.getPrimaryLanguage()));
        info.setClientAccountNumber(
                Optional.ofNullable(pid.getPatientAccountNumber()).map(CXExtendedCompositeId::getpId).orElse(null)
        );

        info.setRace(CollectionUtils.emptyIfNull(pid.getRaces()).stream().findFirst().map(ceToStringConverter::convert).orElse(null));
        info.setEthnicGroup(CollectionUtils.emptyIfNull(pid.getEthnicGroups()).stream().findFirst().map(ceToStringConverter::convert).orElse(null));
        info.setNationality(CcdUtils.getIdentifier(pid.getNationality()));
        info.setReligion(ceToStringConverter.convert(pid.getReligion()));

        var citizenship = CollectionUtils.emptyIfNull(pid.getCitizenships());
        info.setCitizenships(
                citizenship.stream().map(CcdUtils::getIdentifier).collect(Collectors.toList())
        );

        info.setVeteranStatus(ceToStringConverter.convert(pid.getVeteransMilitaryStatus()));

        info.setHomePhone(CollectionUtils.isNotEmpty(pid.getPhoneNumbersHome()) ? pid.getPhoneNumbersHome().get(0).getPhoneNumber() : null);
        info.setBusinessPhone(CollectionUtils.isNotEmpty(pid.getPhoneNumbersBusiness()) ? pid.getPhoneNumbersBusiness().get(0).getTelephoneNumber() : null);
        var addresses = CollectionUtils.emptyIfNull(pid.getPatientAddresses());

        var address = addresses.stream()
                .findAny()
                .map(xadAddressConverter::convert)
                .orElse(null);
        info.setAddress(address);

        info.setDeathDate(DateTimeUtils.toEpochMilli(pid.getPatientDeathDateAndTime()));
    }

    protected abstract C create();
}
