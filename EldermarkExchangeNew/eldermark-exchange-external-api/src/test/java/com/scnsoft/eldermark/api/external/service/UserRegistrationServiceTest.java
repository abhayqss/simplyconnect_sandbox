package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.ThirdPartyApplicationDao;
import com.scnsoft.eldermark.api.external.entity.RegistrationApplication;
import com.scnsoft.eldermark.api.external.service.security.ApiAuthTokenService;
import com.scnsoft.eldermark.api.external.web.dto.RegistrationAnswerDto;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.api.shared.web.dto.Token;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceTest {

    @Mock
    protected ThirdPartyApplicationDao thirdPartyApplicationDao;

    @Mock
    protected ApiAuthTokenService authTokenService;

    @Mock
    protected UserRegistrationApplicationService userRegistrationApplicationService;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    @Captor
    private ArgumentCaptor<RegistrationApplication> registrationApplicationCaptor;

    // Shared test data
    /**
     * Current user ID
     */
    protected final Long userId = TestDataGenerator.randomId();
    /**
     * Current user access token
     */
    protected final Token token = Token.generateToken(userId);
    private final String tokenJson = Token.toJsonString(token);
    private final String tokenEncoded = Token.base64encode(token);
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String appName = TestDataGenerator.randomName();
    private final String appDescription = TestDataGenerator.randomFullName();
    private final Integer timeZoneOffset = TestDataGenerator.randomTimeZoneOffset();

    @BeforeEach
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nToken (JSON): %s\nToken (encoded): %s\nPhone: %s\nEmail: %s\nApp name: %s\nApp description: %s\nTime zone offset (in minutes): %d\n\n",
                userId, tokenJson, tokenEncoded, phone, email, appName, appDescription, timeZoneOffset);
    }

    // Helper methods

    @Test
    public void pleaseDontFailWhenNoTests() {}

    @Test
    public void signupNew3rdPartyAppUser() throws Exception {
        // Expected objects
        final String flowId = UUID.randomUUID().toString().toUpperCase();
        final String normalizedAppName = StringUtils.trim(appName);
        final Date now = new Date();

        final RegistrationApplication expectedApplication = RegistrationApplication.Builder.aRegistrationApplication()
                .withFlowId(flowId)
                .withPhone(phone)
                .withEmail(email)
                .withFirstName(normalizedAppName)
                .withAppDescription(appDescription)
                .withTimeZoneOffset(timeZoneOffset)
                .withCurrentSignupTime(now)
                .withThirdPartyApplication(null)
                .withSignupAttemptCount(1)
                .withPhoneConfirmationAttemptCount(0)
                .withLastSignupTime(null)
                .build();

        // Mockito expectations
        when(userRegistrationApplicationService.getOrCreateRegistrationApplicationFor3rdPartyApp(anyString(), anyString(), anyString()))
                .then((Answer<RegistrationApplication>) invocation -> {
                    final String phone = (String) invocation.getArguments()[0];
                    final String email = (String) invocation.getArguments()[1];
                    final String appName = (String) invocation.getArguments()[2];
                    return RegistrationApplication.Builder.aRegistrationApplication()
                            .withFlowId(flowId)
                            .withPhone(phone)
                            .withEmail(email)
                            .withFirstName(appName)
                            .withCurrentSignupTime(now)
                            .withThirdPartyApplication(null)
                            .withAppDescription(null)
                            .withTimeZoneOffset(null)
                            .withSignupAttemptCount(1)
                            .withPhoneConfirmationAttemptCount(0)
                            .withLastSignupTime(null)
                            .build();
                });
        when(userRegistrationApplicationService.save(any(RegistrationApplication.class))).then(returnsFirstArg());

        // Execute the method being tested
        RegistrationAnswerDto answerDto = userRegistrationService.signupNew3rdPartyAppUser(phone, email, appName, appDescription, String.valueOf(timeZoneOffset));

        // Validation
        assertNotNull(answerDto);
        assertNotNull(answerDto.getFlowId());
        verify(userRegistrationApplicationService).save(registrationApplicationCaptor.capture());
        final RegistrationApplication actualApplication = registrationApplicationCaptor.getValue();
        assertThat(actualApplication).usingRecursiveComparison().isEqualTo(expectedApplication);
    }

}
