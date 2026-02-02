package com.scnsoft.eldermark.dump.service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dump.bean.ComprehensiveAssessment;
import com.scnsoft.eldermark.dump.entity.Client;
import com.scnsoft.eldermark.dump.entity.DisplayableNamedEntity;
import com.scnsoft.eldermark.dump.entity.PersonAddress;
import com.scnsoft.eldermark.dump.model.AddressInfo;
import com.scnsoft.eldermark.dump.model.BaseClientInfo;
import com.scnsoft.eldermark.dump.model.ClientInsuranceInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public final class DumpGeneratorUtils {
    private static final DateTimeFormatter LAST_HOSPITALIZATION_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z");

    private DumpGeneratorUtils() {
    }

    public static void fillClientBaseInfo(BaseClientInfo info, Client client) {
        info.setResidentId(client.getId());
        info.setFirstName(client.getFirstName());
        info.setLastName(client.getLastName());
        info.setOrganization(client.getOrganization().getName());
        info.setCommunity(client.getCommunity().getName());
        info.setActive(client.isActive());
    }

    public static void fillInsuranceInfo(ClientInsuranceInfo info, Client client) {
        info.setInsuranceNetwork(displayName(client.getInNetworkInsurance()));
        info.setInsurancePlan(client.getInsurancePlan());
    }

    public static AddressInfo convertFirstAddress(List<PersonAddress> addresses) {
        return CollectionUtils.emptyIfNull(addresses).stream().findFirst()
                .map(DumpGeneratorUtils::convertAddress)
                .orElse(new AddressInfo());
    }

    public static AddressInfo convertAddress(PersonAddress address) {
        var info = new AddressInfo();
        info.setCity(address.getCity());
        info.setState(address.getState());
        info.setStreet(address.getStreetAddress());
        info.setZip(address.getPostalCode());
        return info;
    }

    public static String displayName(DisplayableNamedEntity displayableNamedEntity) {
        if (displayableNamedEntity == null) {
            return null;
        }
        return displayableNamedEntity.getDisplayName();
    }

    public static ComprehensiveAssessment parseComprehensive(String result, ObjectMapper mapper) {
        try {
            return mapper.readerFor(ComprehensiveAssessment.class).readValue(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Instant> parseAssessmentDate(String s) {
        //possible inputs:
        //"12/01/2019 02:01 PM +03:00"
        //"04/21/2020 10:06 AM -05:00"
        //"05/22/2019 09:51 AM (MST)"
        //"03/01/2020 12:06 PM (GMT)"
        if (StringUtils.isNotEmpty(s)) {
            s = s.replace("(", "").replace(")", "");
            try {
                return Optional.of(Instant.from(LAST_HOSPITALIZATION_FORMAT.parse(s)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return Optional.empty();
    }
}
