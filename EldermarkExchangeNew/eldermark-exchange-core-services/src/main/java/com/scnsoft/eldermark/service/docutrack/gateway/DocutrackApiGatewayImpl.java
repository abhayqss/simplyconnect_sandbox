package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.docutrack.ws.api.DocumentEngineSoap;
import com.scnsoft.eldermark.exception.DocutrackApiException;
import com.scnsoft.eldermark.util.HashValidator;
import com.scnsoft.eldermark.util.XmlToMapParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;

import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DocutrackApiGatewayImpl implements DocutrackApiGateway {
    private static final Logger logger = LoggerFactory.getLogger(DocutrackApiGatewayImpl.class);

    private static final String CLIENT_ID = "ClientID";
    private static final int MAX_GETFILE_REATTEMTS = 5;

    private Map<Long, DocutrackAuthentication> communityTokenMap = new ConcurrentHashMap<>();
    private Map<Long, ReentrantLock> communityLockMap = new ConcurrentHashMap<>();
    private Map<DocumentLockKey, ReentrantLock> documentLockMap = new ConcurrentHashMap<>();

    private final DocumentEngineSoapProvider documentEngineSoapProvider;
    private final String clientUserName;
    private final Long authTokenExpiration;

    @Autowired
    public DocutrackApiGatewayImpl(DocumentEngineSoapProvider documentEngineSoapProvider,
                                   @Value("${docutrack.client.username}") String clientUserName,
                                   @Value("${docutrack.authToken.expiration}") Long authTokenExpiration) {
        this.documentEngineSoapProvider = documentEngineSoapProvider;
        this.clientUserName = clientUserName;
        this.authTokenExpiration = authTokenExpiration;
    }

    @Override
    public String insertDocument(DocutrackApiClient apiClient,
                                 String sourceId, String sourceName, String mimeType, byte[] document,
                                 String businessUnitCode, String documentText) {
        try {
            var port = documentEngineSoapProvider.get(apiClient);

            var params = new HashMap<String, String>();
            authorizeCommand(params, apiClient, port);

            params.put("SourceID", sourceId);
            params.put("SourceName", sourceName);
            params.put("MimeType", mimeType);
            params.put("DocumentFile", Base64.getEncoder().encodeToString(document));

            if (StringUtils.isNotEmpty(businessUnitCode)) {
                params.put("BusinessUnitCode", businessUnitCode);
            }

            if (StringUtils.isNotEmpty(documentText)) {
                params.put("DocumentText", documentText);
            }

            var response = executeCommand(DirectExecuteCommand.INSERT_DOCUMENT, params, port);

            if (response.containsKey("BatchID")) {
                return response.get("BatchID");
            }
        } catch (Exception ex) {
            logger.error("Error during DocuTrack insertDocument call", ex);
            throw new DocutrackApiException("Error during DocuTrack insertDocument call");
        }
        throw new DocutrackApiException("Error during DocuTrack insertDocument call");
    }

    @Override
    public byte[] getDocument(DocutrackApiClient apiClient, Long documentId) {
        ReentrantLock lock = null;
        var lockKey = new DocumentLockKey(documentId, apiClient.getCommunityId());
        try {
            //we have to ensure that a document is being downloaded by one person only
            //because downloading is just making consequent getDocumentFile calls
            //if more than one thread download the same document with the same clientId from the same server they
            //won't have full document, but just it's chunks
            logger.info("Fetching lock for document [{}], community [{}]", documentId, apiClient.getCommunityId());
            lock = documentLockMap.computeIfAbsent(lockKey, key -> new ReentrantLock());
            lock.lock();

            var port = documentEngineSoapProvider.get(apiClient);

            int attempt = 0;
            do {
                var byteList = new ArrayList<byte[]>();
                byte[] fileMd5 = null;

                int chunkNumber = 0;
                var authToken = getAuthToken(apiClient, port);
                while (fileMd5 == null) {
                    logger.info("Fetching document [{}] from [{}], chunk #{}", documentId, apiClient.getServerDomain(), chunkNumber);

                    var fileBytesHolder = new Holder<byte[]>();
                    var fileBytesMd5Holder = new Holder<byte[]>();

                    port.getDocumentFile(authToken, documentId, fileBytesHolder, fileBytesMd5Holder);

                    byteList.add(fileBytesHolder.value);
                    fileMd5 = fileBytesMd5Holder.value;

                    ++chunkNumber;
                }

                var fullBytes = toByteArray(byteList);
                if (HashValidator.matches(HashValidator.MD5_HASH, fullBytes, fileMd5)) {
                    return fullBytes;
                }

                logger.warn("File hash doesn't match with DocuTrack hash, reloading the file again...");
                ++attempt;
            } while (attempt <= MAX_GETFILE_REATTEMTS);

        } catch (SOAPFaultException ex) {
            logger.error("Error during DocuTrack getDocument call", ex);
            var translated = translateFault(ex);
            if (translated != null) {
                throw translated;
            }
            throw new DocutrackApiException("Error during DocuTrack getDocument call");
        } catch (Exception ex) {
            logger.error("Error during DocuTrack getDocument call", ex);
            throw new DocutrackApiException("Error during DocuTrack getDocument call");
        } finally {
            if (lock != null) {
                removeLockIfNotWaited(lock, documentLockMap, lockKey);
                lock.unlock();
            }
        }
        throw new RuntimeException("Error during DocuTrack getDocument call");
    }

    private <T> void removeLockIfNotWaited(ReentrantLock lock, Map<T, ReentrantLock> map, T key) {
        try {
            if (lock.getQueueLength() == 0) {
                documentLockMap.remove(key);
                logger.info("Lock for key [{}] was removed from map", key);
            } else {
                logger.info("Lock for key [{}] is being waited by other thread, won't remove from map", key);
            }
        } catch (Exception ex) {
            logger.error("Exception while removing lock from map", ex);
        }
    }

    private byte[] toByteArray(List<byte[]> byteList) {
        var size = byteList.stream().mapToInt(value -> value.length).sum();

        var result = new byte[size];
        var position = 0;
        for (var bytes : byteList) {
            System.arraycopy(bytes, 0, result, position, bytes.length);
            position += bytes.length;
        }

        return result;
    }

    private Map<String, String> executeCommand(DirectExecuteCommand command, Map<String, String> params, DocumentEngineSoap port) {
        var paramsStr = buildParamsStr(params);

        var result = port.directConnectExecute(command.getCommandName(), paramsStr);
        return XmlToMapParser.parseEscaped(result);
    }

    private String buildParamsStr(Map<String, String> params) {
        var sb = new StringBuilder("<Parameters>");
        params.forEach((key, value) -> sb.append("<").append(key).append(">")
                .append("<![CDATA[").append(value).append("]]></")
                .append(key).append(">"));
        sb.append("</Parameters>");
        return sb.toString();
    }


    private void authorizeCommand(Map<String, String> params, DocutrackApiClient apiClient, DocumentEngineSoap port) {
        params.put(CLIENT_ID, getAuthToken(apiClient, port).toString());
    }

    private Long getAuthToken(DocutrackApiClient apiClient, DocumentEngineSoap port) {
        return readToken(apiClient).orElseGet(() -> updateToken(apiClient, port));
    }

    private Optional<Long> readToken(DocutrackApiClient apiClient) {
        var auth = communityTokenMap.getOrDefault(apiClient.getCommunityId(), null);
        if (shouldUpdateToken(auth) || !auth.clientType.equals(apiClient.getClientType())) {
            return Optional.empty();
        }
        return Optional.of(auth.token);

    }

    private Long updateToken(DocutrackApiClient apiClient, DocumentEngineSoap port) {
        var lock = communityLockMap.getOrDefault(apiClient.getCommunityId(), new ReentrantLock());
        try {
            lock.lock(); //lock so that read and write is atomic per clientType
            var auth = communityTokenMap.getOrDefault(apiClient.getCommunityId(), null);
            if (shouldUpdateToken(auth) || !auth.clientType.equals(apiClient.getClientType())) {
                auth = new DocutrackAuthentication(
                        getNewToken(apiClient.getClientType(), port),
                        Instant.now(),
                        apiClient.getClientType()
                );
                communityTokenMap.put(apiClient.getCommunityId(), auth);
            }
            return auth.token;
        } finally {
            lock.unlock();
            removeLockIfNotWaited(lock, communityLockMap, apiClient.getCommunityId());
        }
    }

    private boolean shouldUpdateToken(DocutrackAuthentication auth) {
        return auth == null || auth.updatedTime.plusMillis(authTokenExpiration).isBefore(Instant.now());
    }

    private Long getNewToken(String clientType, DocumentEngineSoap port) {
        var params = new HashMap<String, String>();

        params.put("ClientType", clientType);
        params.put("ClientUserName", clientUserName);

        var response = executeCommand(DirectExecuteCommand.REGISTER, params, port);
        if (response.containsKey(CLIENT_ID)) {
            return Long.valueOf(response.get(CLIENT_ID));
        }
        throw new DocutrackApiException("Token not received: " + response);
    }

    private DocutrackApiException translateFault(SOAPFaultException ex) {
        var exception = findChild(ex.getFault().getDetail(), "exception");
        if (exception == null) {
            return null;
        }

        var codeNode = findChild(exception, "code");
        if (codeNode == null) {
            return null;
        }

        var code = Long.valueOf(codeNode.getTextContent());
        var type = DocutrackSoapExceptionType.fromCode(code);

        if (type != null) {
            return new DocutrackApiException(type.getText());
        }

        return null;
    }

    private Node findChild(Node parent, String childName) {
        for (int i = 0; i < parent.getChildNodes().getLength(); ++i) {
            var node = parent.getChildNodes().item(i);
            if (childName.equals(node.getLocalName())) {
                return node;
            }
        }
        return null;
    }

    private enum DirectExecuteCommand {
        REGISTER("Register"),
        INSERT_DOCUMENT("InsertDocument");

        private final String commandName;

        DirectExecuteCommand(String commandName) {
            this.commandName = commandName;
        }

        public String getCommandName() {
            return commandName;
        }
    }

    private static class DocutrackAuthentication {
        Long token;
        Instant updatedTime;
        String clientType;

        public DocutrackAuthentication(Long token, Instant updatedTime, String clientType) {
            this.token = token;
            this.updatedTime = updatedTime;
            this.clientType = clientType;
        }
    }

    private static class DocumentLockKey {
        private final Long documentId;
        private final Long communityId;

        public DocumentLockKey(Long documentId, Long communityId) {
            this.documentId = documentId;
            this.communityId = communityId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DocumentLockKey that = (DocumentLockKey) o;
            return Objects.equals(documentId, that.documentId) &&
                    Objects.equals(communityId, that.communityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(documentId, communityId);
        }

        @Override
        public String toString() {
            return "DocumentLockKey{" +
                    "documentId=" + documentId +
                    ", communityId=" + communityId +
                    '}';
        }
    }
}
