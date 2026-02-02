package com.scnsoft.eldermark.validation.context;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TwilioConversationServiceSidRepository extends JpaRepository<TwilioConversationServiceSid, String> {

    Optional<TwilioConversationServiceSid> findFirstByServiceSidAndAccountSid(String serviceSid, String accountSid);
}
