package com.scnsoft.eldermark.merger.service;


import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMatcherServiceImplTest {

    private ClientMatcherServiceImpl instance = new ClientMatcherServiceImpl();

    @Test
    void findMatchedPatients() {
        var client1 = new Client();
        client1.setId(1L);
        client1.setFirstName("Jane");
        client1.setMiddleName("A");
        client1.setLastName("Doe");
        client1.setBirthDate(LocalDate.now());
        client1.setSocialSecurity("111223333");
        client1.setHieConsentPolicyType(HieConsentPolicyType.OPT_IN);

        //should match
        var client2 = new Client();
        client2.setId(2L);
        client2.setFirstName(client1.getFirstName());
        client2.setMiddleName(client1.getMiddleName());
        client2.setLastName(client1.getLastName());
        client2.setBirthDate(client1.getBirthDate());
        client2.setSocialSecurity(client1.getSocialSecurity());
        client2.setHieConsentPolicyType(HieConsentPolicyType.OPT_IN);

        //should not match
        var client3 = new Client();
        client3.setId(3L);
        client3.setFirstName("Luke");
        client3.setMiddleName("D");
        client3.setLastName("Skywalker");
        client3.setBirthDate(LocalDate.now());
        client3.setSocialSecurity("222334444");
        client3.setHieConsentPolicyType(HieConsentPolicyType.OPT_IN);

        //would have matched if was opted in
        var client4 = new Client();
        client4.setId(4L);
        client4.setFirstName(client1.getFirstName());
        client4.setMiddleName(client1.getMiddleName());
        client4.setLastName(client1.getLastName());
        client4.setBirthDate(client1.getBirthDate());
        client4.setSocialSecurity(client1.getSocialSecurity());
        client4.setHieConsentPolicyType(HieConsentPolicyType.OPT_OUT);


        var matchResult = instance.findMatchedPatients(client1, () -> List.of(client2, client3, client4), true);

        assertThat(matchResult.getMatchedRecords()).hasSize(1);
        assertThat(matchResult.getProbablyMatchedRecords()).hasSize(0);

        assertThat(matchResult.getMatchedRecords().get(0).getR1().getId()).isEqualTo(2L);
        assertThat(matchResult.getMatchedRecords().get(0).getR2().getId()).isEqualTo(1L);
    }
}