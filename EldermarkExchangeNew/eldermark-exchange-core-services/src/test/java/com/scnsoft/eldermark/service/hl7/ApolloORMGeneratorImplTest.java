package com.scnsoft.eldermark.service.hl7;

import com.scnsoft.eldermark.dao.IntegrityInsuranceDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.lab.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApolloORMGeneratorImplTest {


    @Mock
    private IntegrityInsuranceDao integrityInsuranceDao;

    @Mock
    private Environment environment;

    @InjectMocks
    private ApolloORMGeneratorImpl instance;

    @Test
    void test_generateOrmRaw() {
        var order = new LabResearchOrder();
        var client = new Client();
        client.setId(1234L);
        client.setFirstName("Jack");
        client.setMiddleName("John");
        client.setLastName("Smith");
        client.setSocialSecurity("123456789");

        order.setId(10L);
        order.setRequisitionNumber("SC-10");

        order.setReason(LabResearchOrderReason.RESIDENT);
        order.setClinic("clinic Name");
        order.setClinicAddress("Clinic address");
        order.setNotes("notes");
//        order.setCreatedBy(); not needed for ORM generation
        order.setIsCovid19(true);
        order.setProviderFirstName("ProviderFirst");
        order.setProviderLastName("ProviderLast");
        order.setOrderDate(Instant.ofEpochMilli(1637668777000L));
        order.setIcd10Codes(Arrays.asList("Z03.818", "J20.8"));

        order.setClient(client);
        order.setPhone("859-226-6094");
        order.setAddress("1517 Carson Street");
        order.setCity("Lexington");
        order.setZipCode("40507");
        var state = new State();
        state.setName("Kentucky");
        order.setState(state);
        order.setInNetworkInsurance("Insurance Network");
        order.setPolicyNumber("44321234");
        order.setPolicyHolder(LabOrderPolicyHolder.SPOUSE);
        order.setPolicyHolderName("holder name");
        order.setPolicyHolderDOB(LocalDate.of(1984, 4, 13));

        order.setGender(createCcdCode("M", "Male"));
        order.setRace(createCcdCode("1002-5", "American Indian or Alaska Native"));
        order.setBirthDate(LocalDate.of(1993, 7, 23));

        order.setSpecimenTypes(Arrays.asList(
                createSpecimenType(3L, "NAIL", "Nail"),
                createSpecimenType(4L, "URINE", "Urine")
                )
        );
        order.setCollectorsName("collector name");
        order.setSite("Site");
        order.setSpecimenDate(Instant.ofEpochMilli(1637668000000L));

        var integrityInsurance = new IntegrityInsurance();
        integrityInsurance.setName("Insurance Network");
        integrityInsurance.setId(12345L);


        when(environment.getActiveProfiles())
                .thenReturn(new String[]{"test"});

        when(integrityInsuranceDao.findFirstByName("Insurance Network"))
                .thenReturn(Optional.of(integrityInsurance));

        instance.fillProcessingMode();

        var result = instance.generate(order);


        var startsWith = "MSH|^~\\&|SimplyConnect|SimplyConnect|APOLLO|TNILK|";
        var sentDateTime = result.getOrmRaw().substring(startsWith.length(), result.getOrmRaw().indexOf('|', startsWith.length() + 1));

        assertThat(result.getOrmRaw()).isEqualTo(startsWith + sentDateTime + "||ORM^O01^ORM_O01|10|T|2.5.1\r" +
        "PID|1|1234|1234||Smith^Jack^John||19930723|M||1002-5^American Indian or Alaska Native|1517 Carson Street^^Lexington^Kentucky^40507||859-226-6094||||||123456789\r" +
                "NTE|1||notes\r" +
                "PV1|1||clinic Name^^^^^^^^Clinic address\r" +
                "IN1|1|||Insurance Network||||||||||||holder^name|SPO|19840413||||||||||||||||||44321234\r" +
                "ORC|NW|SC-10\r" +
                "OBR|1|SC-10||703398|||20211123114640|||||||||^ProviderLast^ProviderFirst\r" +
                "DG1|1||Z03.818\r" +
                "DG1|2||J20.8\r");
    }

    private CcdCode createCcdCode(String code, String display) {
        var ccdCode = new CcdCode();
        ccdCode.setCode(code);
        ccdCode.setDisplayName(display);
        return ccdCode;
    }

    private SpecimenType createSpecimenType(Long id, String name, String title) {
        var spm = new SpecimenType();
        spm.setId(id);
        spm.setName(name);
        spm.setTitle(title);
        return spm;
    }
}