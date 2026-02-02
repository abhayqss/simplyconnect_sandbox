package com.scnsoft.eldermark.service.docutrack;

import com.scnsoft.eldermark.beans.projection.BusinessUnitCodesAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdNameAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.specification.CommunitySpecificationGenerator;
import com.scnsoft.eldermark.dto.TlsConnectivityCheckResult;
import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.PdfService;
import com.scnsoft.eldermark.service.TlsConnectivityChecker;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackApiClient;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackApiClientFactory;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackApiGateway;
import com.scnsoft.eldermark.service.docutrack.gateway.DocutrackTrustStoresManager;
import com.scnsoft.eldermark.service.image.TiffImageConverter;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.util.KeyStoreUtil;
import com.scnsoft.eldermark.util.MimeTypeConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocutrackServiceImpl implements DocutrackService {
    private static final Logger logger = LoggerFactory.getLogger(DocutrackServiceImpl.class);

    static final long MB_10 = 1024 * 1024 * 10L;
    static final String ATTACHMENT_NAME = "Attachment.pdf";

    private static final List<DocutrackSupportedFileListItemDto> SUPPORTED_FILE_TYPES = Arrays.asList(
            new DocutrackSupportedFileListItemDto(MediaType.APPLICATION_PDF_VALUE, MB_10, false),
            new DocutrackSupportedFileListItemDto(MimeTypeConstants.IMAGE_TIFF, MB_10, false),
            new DocutrackSupportedFileListItemDto(MediaType.IMAGE_PNG_VALUE, MB_10, true),
            new DocutrackSupportedFileListItemDto(MediaType.IMAGE_JPEG_VALUE, MB_10, true),
            new DocutrackSupportedFileListItemDto(MimeTypeConstants.MS_WORD_DOC, MB_10, true),
            new DocutrackSupportedFileListItemDto(MimeTypeConstants.MS_WORD_DOCX, MB_10, true)
    );

    private static final Map<String, ConversionType> MIME_CONVERSION_TYPE_MAPPING = Map.of(
            MediaType.IMAGE_PNG_VALUE, ConversionType.IMAGE,
            MediaType.IMAGE_JPEG_VALUE, ConversionType.IMAGE,
            MimeTypeConstants.MS_WORD_DOC, ConversionType.PDF,
            MimeTypeConstants.MS_WORD_DOCX, ConversionType.PDF
    );

    @Value("${docutrack.integration.enabled}")
    private Boolean isDocutrackIntegrationEnabled;

    @Autowired
    private DocutrackApiGateway docutrackApiGateway;

    @Autowired
    private ChatService chatService;

    @Autowired
    private TiffImageConverter tiffImageConverter;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DocutrackTrustStoresManager docutrackTrustStoresManager;

    @Autowired
    private DocutrackApiClientFactory docutrackApiClientFactory;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private CommunitySpecificationGenerator communitySpecificationGenerator;

    @Autowired
    private TlsConnectivityChecker tlsConnectivityChecker;

    @Override
    @Transactional(readOnly = true)
    public boolean isDocutrackEnabled(Employee employee) {
        return Boolean.TRUE.equals(isDocutrackIntegrationEnabled)
                && isAllowedByRole(employee)
                && hasAssociatedPharmacies(employee);
    }

    private boolean isAllowedByRole(Employee employee) {
        //todo or in security service via permission filter?
        if (employee.getCareTeamRole() == null) {
            return false;
        }
        return CareTeamRolePermissionMapping.getPermissions(employee.getCareTeamRole().getCode()).contains(Permission.DOCUTRACK_INTEGRATION_ALLOWED);
    }


    private boolean hasAssociatedPharmacies(Employee employee) {
        return employee.getCommunity() != null && Boolean.TRUE.equals(employee.getCommunity().getIsDocutrackPharmacy());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Community> getAssociatedPharmacies(Employee employee) {
        if (!isDocutrackEnabled(employee)) {
            return Collections.emptyList();
        }

        return getAssociatedPharmaciesNoIntegrationCheck(employee);
    }

    private List<Community> getAssociatedPharmaciesNoIntegrationCheck(Employee employee) {
        var associatedCommunity = employee.getCommunity();
        if (Boolean.TRUE.equals(associatedCommunity.getIsDocutrackPharmacy())) {
            return Collections.singletonList(associatedCommunity);
        }
        return Collections.emptyList();
    }

    @Override
    public List<DocutrackSupportedFileListItemDto> getSupportedFileTypes() {
        return new ArrayList<>(SUPPORTED_FILE_TYPES);
    }

    @Override
    @Transactional(readOnly = true)
    public void sendToDocutrackFromChat(Employee loggedUser, String mediaSid, String businessUnitCode, String documentText) {
        if (!isDocutrackEnabled(loggedUser)) {
            throw new BusinessException("Integration is not allowed for logged in user");
        }

        var pharmacy = getAssociatedPharmaciesNoIntegrationCheck(loggedUser)
                .stream()
                .filter(p -> StringUtils.isEmpty(businessUnitCode) || p.getBusinessUnitCodes().contains(businessUnitCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Business unit code is not associated with any pharmacy"));

        var media = chatService.fetchMedia(mediaSid);

        if (!chatService.isAnyChatParticipant(
                media.getChannelSid(),
                Collections.singletonList(loggedUser.getId())
        )) {
            throw new BusinessException("No access to media");
        }

        var supportedFileType = SUPPORTED_FILE_TYPES.stream()
                .filter(sft -> sft.getMimeType().equals(media.getContentType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Unsupported file type"));

        if (media.getSize() > supportedFileType.getMaxSize()) {
            throw new BusinessException("File is too big");
        }

        var bytes = chatService.downloadMediaContent(media);
        var mimeType = media.getContentType();

        if (MIME_CONVERSION_TYPE_MAPPING.containsKey(mimeType)) {
            switch (MIME_CONVERSION_TYPE_MAPPING.get(mimeType)) {
                case IMAGE:
                    mimeType = MimeTypeConstants.IMAGE_TIFF;
                    bytes = tiffImageConverter.convertToTiff(bytes, MB_10);
                    break;
                case PDF:
                    mimeType = MediaType.APPLICATION_PDF_VALUE;
                    bytes = convertWordToPdf(bytes);
                    break;
            }

            if (bytes.length > supportedFileType.getMaxSize()) {
                throw new BusinessException("File is too big after conversion");
            }
        }

        //fetch author identity from message instead of media because for documents attached
        //from DocuTrack media author is 'system'
        var message = chatService.fetchMessage(media.getChannelSid(), media.getMessageSid());
        var senderEmployeeId = ConversationUtils.employeeIdFromIdentity(message.getAuthor());

        var source = employeeService.findById(senderEmployeeId, OrganizationIdNameAware.class);
        docutrackApiGateway.insertDocument(
                docutrackApiClientFactory.createDocutrackApiClient(pharmacy),
                "SimplyConnect_Org_" + source.getOrganizationId(),
                source.getOrganizationName(),
                mimeType,
                bytes,
                businessUnitCode, documentText
        );

    }

    private byte[] convertWordToPdf(byte[] bytes) {
        try {
            return pdfService.convertWordToPdf(bytes);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.PDF_CONVERSION_ERROR, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String attachFromDocutrackToChat(Employee loggedUser, String conversationSid, Long documentId) {
        if (!isDocutrackEnabled(loggedUser)) {
            throw new BusinessException("Integration is not allowed for logged in user");
        }

        if (!chatService.isAnyChatParticipant(
                conversationSid,
                Collections.singletonList(loggedUser.getId())
        )) {
            throw new BusinessException("User is not member of conversation");
        }

        var pharmacy = getAssociatedPharmaciesNoIntegrationCheck(loggedUser)
                .stream()
                .findFirst()
                .orElseThrow();

        var documentBytes = docutrackApiGateway.getDocument(
                docutrackApiClientFactory.createDocutrackApiClient(pharmacy),
                documentId
        );

        return chatService.sendMediaMessage(
                conversationSid,
                ConversationUtils.employeeIdToIdentity(loggedUser.getId()),
                "Attachment.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                documentBytes
        );
    }

    @Override
    @Transactional
    public void updateServerCertificate(Community community, byte[] certBytes) {
        updateServerCertificate(
                community,
                certBytes == null ?
                        null :
                        KeyStoreUtil.loadX509Certificate(certBytes)
        );
    }

    @Override
    @Transactional
    public void updateServerCertificate(Community community, X509Certificate certificate) {
        byte[] fingerprint = null;
        if (certificate != null) {
            validateConnectivity(community.getDocutrackServerDomain(), certificate);
            fingerprint = KeyStoreUtil.sha1Fingerprint(certificate);
        }

        docutrackTrustStoresManager.updateServerCertificate(community.getId(), certificate);
        community.setDocutrackServerCertificateSha1(fingerprint);
        communityDao.save(community);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<X509Certificate> getConfiguredCertificate(Community community) {
        return docutrackTrustStoresManager.getCertificate(community.getId());
    }

    @Override
    public Optional<X509Certificate> loadServerCertIfSelfSigned(String serverDomain) {
        var checkResult = checkTlsConnection(serverDomain, null);
        if (!checkResult.isSuccess()) {
            return Optional.of(checkResult.getCertificate());
        }
        return Optional.empty();
    }

    private void validateConnectivity(String serverDomain, Certificate certificate) {
        var checkResult = checkTlsConnection(serverDomain, certificate);
        if (!checkResult.isSuccess()) {
            throw new ValidationException("Provided certificate doesn't match with domain certificate");
        }
    }

    @Override
    public List<String> nonUniqueBusinessUnitCodes(String serverDomain, Long excludeCommunityId, List<String> businessUnitCodes) {
        if (CollectionUtils.isEmpty(businessUnitCodes)) {
            return Collections.emptyList();
        }

        var allCodes = new ArrayList<>(businessUnitCodes);

        var spec = communitySpecificationGenerator.isDocutrackPharmacy();
        spec = spec.and(communitySpecificationGenerator.withDocutrackServerDomain(serverDomain));
        spec = spec.and(communitySpecificationGenerator.hasAnyBusinessUnitCode(businessUnitCodes));
        if (excludeCommunityId != null) {
            spec = spec.and(communitySpecificationGenerator.byIdNot(excludeCommunityId));
        }

        communityDao.findAll(spec, BusinessUnitCodesAware.class).stream()
                .map(BusinessUnitCodesAware::getBusinessUnitCodes)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(allCodes::add);

        //find duplicates in all codes (this way we also search for duplicates among 'businessUnitCodes' input list)
        var loweredDuplicates = allCodes
                .stream()
                .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(p -> p.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());


        //preserve case
        return businessUnitCodes.stream()
                .filter(code -> loweredDuplicates.contains(code.toLowerCase()))
                .collect(Collectors.toList());
    }

    private TlsConnectivityCheckResult checkTlsConnection(String serverDomain, Certificate certificate) {
        var urlPath = DocutrackApiClient.getSoapUrl(serverDomain);
        TlsConnectivityCheckResult result = null;
        try {
            if (certificate == null) {
                result = tlsConnectivityChecker.checkTls(urlPath);
            } else {
                result = tlsConnectivityChecker.checkTls(urlPath, certificate);
            }
        } catch (SocketTimeoutException e) {
            throw new ValidationException(
                    "Connection timeout while trying to connect to server domain. " +
                            "Please make sure that Simply Connect IP address is whitelisted. " +
                            "For more details, please contact the Simply Connect team.");
        } catch (UnknownHostException e) {
            throw new ValidationException(
                    "The specified domain either does not exist or could not be contacted. " +
                            "Please check the domain entered. " +
                            "For more details, please contact the Simply Connect team.");
        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            throw new ValidationException("Unexpected error occurred during server domain validation. " +
                    "Please contact Simply Connect Team.");
        }
        if (result != null) {
            if (!result.isSuccess() && result.getCertificate() == null) {
                throw new ValidationException("The domain provided does not support https. " +
                        "Please check the domain entered. " +
                        "For more details, please contact the Simply Connect team.");
            }
        }
        return result;
    }

    enum ConversionType {
        IMAGE,
        PDF
    }
}
