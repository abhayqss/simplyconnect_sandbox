package com.scnsoft.eldermark.service.docutrack;

import com.scnsoft.eldermark.beans.projection.OrganizationIdNameAware;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.PdfService;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackApiClient;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackApiClientFactory;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackApiGateway;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackTrustStoresManager;
import com.scnsoft.eldermark.service.image.TiffImageConverter;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.service.twilio.media.Media;
import com.scnsoft.eldermark.util.MimeTypeConstants;
import com.twilio.rest.conversations.v1.service.conversation.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocutrackServiceImplTest {

    private static final String MEDIA_SID = "mediaSid";
    private static final String BUSINESS_UNIT_CODE = "buc";
    private static final String DOCUMENT_TEXT = "document text";
    private static final String CONVERSATION_SID = "conversationSid";
    private static final String SERVER_DOMAIN = "server.domain";

    @Mock
    private DocutrackApiGateway docutrackApiGateway;

    @Mock
    private ChatService chatService;

    @Mock
    private TiffImageConverter tiffImageConverter;

    @Mock
    private PdfService pdfService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private DocutrackTrustStoresManager docutrackTrustStoreHelper;

    @Mock
    private DocutrackApiClientFactory docutrackApiClientFactory;

    @InjectMocks
    private DocutrackServiceImpl instance;

    void setDocutrackEnabled(boolean value) {
        ReflectionTestUtils.setField(instance, "isDocutrackIntegrationEnabled", value);
    }

    @Test
    void sendToDocutrackFromChat_unknownBUC_ShouldThrow() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);

        var thrown = assertThrows(BusinessException.class,
                () -> instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, "unknown BUC", DOCUMENT_TEXT));

        assertEquals("Business unit code is not associated with any pharmacy", thrown.getMessage());
    }

    @Test
    void sendToDocutrackFromChat_docAuthoredByLoggedUser_ShouldSend() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
            MEDIA_SID,
            1024 * 1024 * 10L - 1,
            MediaType.APPLICATION_PDF_VALUE,
            ConversationUtils.employeeIdToIdentity(pharmacist.getId()),
            CONVERSATION_SID
        );
        var message = mockMessage(media.getAuthor());
        byte[] content = new byte[0];
        var sourceOrganizationId = 5L;
        var sourceOrganizationName = "SrcOrg";
        var apiClient = Mockito.mock(DocutrackApiClient.class);

        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.fetchMessage(media.getChannelSid(), media.getMessageSid())).thenReturn(message);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);
        when(chatService.downloadMediaContent(media)).thenReturn(content);
        when(employeeService.findById(pharmacist.getId(), OrganizationIdNameAware.class)).thenReturn(new OrganizationIdNameAware() {
            @Override
            public Long getOrganizationId() {
                return sourceOrganizationId;
            }

            @Override
            public String getOrganizationName() {
                return sourceOrganizationName;
            }
        });
        when(docutrackApiClientFactory.createDocutrackApiClient(pharmacy)).thenReturn(apiClient);

        instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT);

        verify(docutrackApiGateway).insertDocument(
            eq(apiClient),
            eq("SimplyConnect_Org_" + sourceOrganizationId),
            eq(sourceOrganizationName),
            eq(MediaType.APPLICATION_PDF_VALUE),
            eq(content),
            eq(BUSINESS_UNIT_CODE),
            eq(DOCUMENT_TEXT)
        );
    }

    @Test
    void sendToDocutrackFromChat_userNotInChat_ShouldThrow() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
                MEDIA_SID,
                111L,
                MimeTypeConstants.IMAGE_TIFF,
                ConversationUtils.employeeIdToIdentity(1235L),
                CONVERSATION_SID
        );

        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(false);

        var thrown = assertThrows(BusinessException.class,
                () -> instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT)
        );

        assertEquals("No access to media", thrown.getMessage());
    }

    @Test
    void sendToDocutrackFromChat_unsupportedFileType_ShouldThrow() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
                MEDIA_SID,
                1024 * 1024 * 20L,
                "mime/unsupported",
                ConversationUtils.employeeIdToIdentity(1235L),
                CONVERSATION_SID
        );

        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);

        var thrown = assertThrows(BusinessException.class,
                () -> instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT)
        );

        assertEquals("Unsupported file type", thrown.getMessage());
    }

    @Test
    void sendToDocutrackFromChat_greaterThan10MB_ShouldThrow() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
                MEDIA_SID,
                1024 * 1024 * 20L,
                MimeTypeConstants.IMAGE_TIFF,
                ConversationUtils.employeeIdToIdentity(1235L),
                CONVERSATION_SID
        );

        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);

        var thrown = assertThrows(BusinessException.class,
                () -> instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT)
        );

        assertEquals("File is too big", thrown.getMessage());
    }

    @Test
    void sendToDocutrackFromChat_tiffFile_ShouldSend() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
                MEDIA_SID,
                1024 * 1024 * 10L - 1,
                MimeTypeConstants.IMAGE_TIFF,
                ConversationUtils.employeeIdToIdentity(1235L),
                CONVERSATION_SID
        );
        byte[] content = new byte[0];
        var sourceOrganizationId = 5L;
        var sourceOrganizationName = "SrcOrg";
        var apiClient = Mockito.mock(DocutrackApiClient.class);
        var message = mockMessage(media.getAuthor());

        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.fetchMessage(media.getChannelSid(), media.getMessageSid())).thenReturn(message);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);
        when(chatService.downloadMediaContent(media)).thenReturn(content);
        when(employeeService.findById(1235L, OrganizationIdNameAware.class)).thenReturn(new OrganizationIdNameAware() {
            @Override
            public Long getOrganizationId() {
                return sourceOrganizationId;
            }

            @Override
            public String getOrganizationName() {
                return sourceOrganizationName;
            }
        });
        when(docutrackApiClientFactory.createDocutrackApiClient(pharmacy)).thenReturn(apiClient);

        instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT);

        verify(docutrackApiGateway).insertDocument(
                eq(apiClient),
                eq("SimplyConnect_Org_" + sourceOrganizationId),
                eq(sourceOrganizationName),
                eq(MimeTypeConstants.IMAGE_TIFF),
                eq(content),
                eq(BUSINESS_UNIT_CODE),
                eq(DOCUMENT_TEXT)
        );
    }

    private Message mockMessage(String author) {
        var message = mock(Message.class);
        when(message.getAuthor()).thenReturn(author);
        return message;
    }

    @Test
    void sendToDocutrackFromChat_pdfFile_ShouldSend() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
                MEDIA_SID,
                1024 * 1024 * 10L - 1,
                MediaType.APPLICATION_PDF_VALUE,
                ConversationUtils.employeeIdToIdentity(1235L),
                CONVERSATION_SID
        );
        var message = mockMessage(media.getAuthor());
        byte[] content = new byte[0];
        var sourceOrganizationId = 5L;
        var sourceOrganizationName = "SrcOrg";
        var apiClient = Mockito.mock(DocutrackApiClient.class);

        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.fetchMessage(media.getChannelSid(), media.getMessageSid())).thenReturn(message);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);
        when(chatService.downloadMediaContent(media)).thenReturn(content);
        when(employeeService.findById(1235L, OrganizationIdNameAware.class)).thenReturn(new OrganizationIdNameAware() {
            @Override
            public Long getOrganizationId() {
                return sourceOrganizationId;
            }

            @Override
            public String getOrganizationName() {
                return sourceOrganizationName;
            }
        });
        when(docutrackApiClientFactory.createDocutrackApiClient(pharmacy)).thenReturn(apiClient);

        instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT);

        verify(docutrackApiGateway).insertDocument(
                eq(apiClient),
                eq("SimplyConnect_Org_" + sourceOrganizationId),
                eq(sourceOrganizationName),
                eq(MediaType.APPLICATION_PDF_VALUE),
                eq(content),
                eq(BUSINESS_UNIT_CODE),
                eq(DOCUMENT_TEXT)
        );
    }

    @Test
    void sendToDocutrackFromChat_pngFile_ShouldConvertToTiffAndSend() throws IOException {
        testWhenConversionNeeded(MediaType.IMAGE_PNG_VALUE, DocutrackServiceImpl.ConversionType.IMAGE);
    }

    @Test
    void sendToDocutrackFromChat_jpegFile_ShouldConvertToTiffAndSend() throws IOException {
        testWhenConversionNeeded(MediaType.IMAGE_JPEG_VALUE, DocutrackServiceImpl.ConversionType.IMAGE);
    }

    @Test
    void sendToDocutrackFromChat_docFile_ShouldConvertToPDFAndSend() throws IOException {
        testWhenConversionNeeded(MimeTypeConstants.MS_WORD_DOC, DocutrackServiceImpl.ConversionType.PDF);
    }

    @Test
    void sendToDocutrackFromChat_docxFile_ShouldConvertToPDFAndSend() throws IOException {
        testWhenConversionNeeded(MimeTypeConstants.MS_WORD_DOCX, DocutrackServiceImpl.ConversionType.PDF);
    }

    void testWhenConversionNeeded(String mimeType, DocutrackServiceImpl.ConversionType expectedConversion) throws IOException {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
                MEDIA_SID,
                1024 * 1024 * 10L - 1,
                mimeType,
                ConversationUtils.employeeIdToIdentity(1235L),
                CONVERSATION_SID
        );
        var message = mockMessage(media.getAuthor());
        byte[] originalContent = new byte[0];
        byte[] convertedContent = new byte[0];
        var sourceOrganizationId = 5L;
        var sourceOrganizationName = "SrcOrg";
        var apiClient = Mockito.mock(DocutrackApiClient.class);

        String expectedMediaType = null;


        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.fetchMessage(media.getChannelSid(), media.getMessageSid())).thenReturn(message);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);
        when(chatService.downloadMediaContent(media)).thenReturn(originalContent);
        switch (expectedConversion) {
            case PDF:
                when(pdfService.convertWordToPdf(originalContent)).thenReturn(convertedContent);
                expectedMediaType = MediaType.APPLICATION_PDF_VALUE;
                break;
            case IMAGE:
                when(tiffImageConverter.convertToTiff(originalContent, 1024 * 1024 * 10L)).thenReturn(convertedContent);
                expectedMediaType = MimeTypeConstants.IMAGE_TIFF;
                break;
        }
        when(employeeService.findById(1235L, OrganizationIdNameAware.class)).thenReturn(new OrganizationIdNameAware() {
            @Override
            public Long getOrganizationId() {
                return sourceOrganizationId;
            }

            @Override
            public String getOrganizationName() {
                return sourceOrganizationName;
            }
        });
        when(docutrackApiClientFactory.createDocutrackApiClient(pharmacy)).thenReturn(apiClient);

        instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT);

        verify(docutrackApiGateway).insertDocument(
                eq(apiClient),
                eq("SimplyConnect_Org_" + sourceOrganizationId),
                eq(sourceOrganizationName),
                eq(expectedMediaType),
                eq(convertedContent),
                eq(BUSINESS_UNIT_CODE),
                eq(DOCUMENT_TEXT)
        );
    }

    @Test
    void sendToDocutrackFromChat_fileExceedesAfterConversion_ShouldThrow() throws IOException {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var media = createMedia(
                MEDIA_SID,
                1024 * 1024 * 10L - 1,
                MimeTypeConstants.MS_WORD_DOC,
                ConversationUtils.employeeIdToIdentity(1235L),
                CONVERSATION_SID
        );
        byte[] originalContent = new byte[0];
        byte[] convertedContent = new byte[1024 * 1024 * 10 + 1];

        when(chatService.fetchMedia(MEDIA_SID)).thenReturn(media);
        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);
        when(chatService.downloadMediaContent(media)).thenReturn(originalContent);
        when(pdfService.convertWordToPdf(originalContent)).thenReturn(convertedContent);

        var thrown = assertThrows(BusinessException.class,
                () -> instance.sendToDocutrackFromChat(pharmacist, MEDIA_SID, BUSINESS_UNIT_CODE, DOCUMENT_TEXT)
        );

        assertEquals("File is too big after conversion", thrown.getMessage());
    }

    @Test
    void attachFromDocutrack_UserNotInChat_ShouldThrow() {
        setDocutrackEnabled(true);

        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);

        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(false);

        var thrown = assertThrows(BusinessException.class,
                () -> instance.attachFromDocutrackToChat(pharmacist, CONVERSATION_SID, 123456L)
        );

        assertEquals("User is not member of conversation", thrown.getMessage());
    }

    @Test
    void attachFromDocutrack_UserInChat_ShouldFetchAndAttach() {
        setDocutrackEnabled(true);

        var documentId = 12345L;
        var pharmacy = createPharmacy(BUSINESS_UNIT_CODE);
        var pharmacist = createPharmacist(1234L, pharmacy);
        var fileContent = new byte[0];
        var messageSid = "messageSid";
        var apiClient = Mockito.mock(DocutrackApiClient.class);

        when(chatService.isAnyChatParticipant(eq(CONVERSATION_SID), ArgumentMatchers.anyCollection())).thenReturn(true);
        when(docutrackApiClientFactory.createDocutrackApiClient(pharmacy)).thenReturn(apiClient);
        when(docutrackApiGateway.getDocument(apiClient, documentId)).thenReturn(fileContent);
        when(chatService.sendMediaMessage(
                eq(CONVERSATION_SID),
                eq(ConversationUtils.employeeIdToIdentity(1234L)),
                eq(DocutrackServiceImpl.ATTACHMENT_NAME),
                eq(MediaType.APPLICATION_PDF_VALUE),
                eq(fileContent))
        ).thenReturn(messageSid);

        var result = instance.attachFromDocutrackToChat(pharmacist, CONVERSATION_SID, documentId);

        assertEquals(messageSid, result);
    }

    private Community createPharmacy(String businessUnitCode) {
        var community = new Community();
        community.setId(1L);
        community.setIsDocutrackPharmacy(true);
        community.setDocutrackClientType("ClientType");
        community.setBusinessUnitCodes(Collections.singletonList(businessUnitCode));
        community.setDocutrackServerDomain(SERVER_DOMAIN);

        return community;
    }

    private Employee createPharmacist(Long id, Community pharmacy) {
        Employee e = new Employee();
        e.setId(id);

        var role = new CareTeamRole();
        role.setCode(CareTeamRoleCode.ROLE_PHARMACIST);
        e.setCareTeamRole(role);

        e.setCommunity(pharmacy);
        e.setCommunityId(pharmacy.getId());

        return e;
    }

    private Media createMedia(String mediaSid, Long size, String contentType, String author, String conversationSid) {
        return new Media(mediaSid,
                "chatService",
                Instant.now().toString(),
                Instant.now().toString(),
                Instant.now().toString(),
                Collections.emptyMap(),
                size,
                contentType,
                "media filename",
                author,
                "media",
                "messageSid",
                conversationSid,
                null,
                false
        );
    }
}
