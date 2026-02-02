package com.scnsoft.eldermark.service.hl7;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v251.message.ORU_R01;
import com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.message.HL7MessageConverter;
import com.scnsoft.eldermark.dao.LabResearchOrderDao;
import com.scnsoft.eldermark.dao.LabResearchOrderORUDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ClientDocumentUploadData;
import com.scnsoft.eldermark.entity.document.SharingOption;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderORU;
import com.scnsoft.eldermark.entity.lab.LabResearchOrderStatus;
import com.scnsoft.eldermark.entity.xds.datatype.OBXValue;
import com.scnsoft.eldermark.entity.xds.message.ORUR01;
import com.scnsoft.eldermark.entity.xds.segment.OBXObservationResult;
import com.scnsoft.eldermark.exception.HL7ProcessingException;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.LabResearchNotificationService;
import com.scnsoft.eldermark.service.LabResearchOrderService;
import com.scnsoft.eldermark.service.document.UploadClientDocumentService;
import com.scnsoft.eldermark.service.storage.ApolloOruLogFileStorage;
import com.scnsoft.eldermark.service.storage.ApolloOruLogTestingFileStorage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApolloOruProcessorImpl implements ApolloOruProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ApolloOruProcessorImpl.class);

    @Autowired
    private ApolloOruLogFileStorage apolloOruLogFileStorage;

    @Autowired
    private ApolloOruLogTestingFileStorage apolloOruLogTestingFileStorage;

    @Autowired
    @Qualifier("apolloHapiContext")
    private HapiContext apolloHapiContext;

    @Autowired
    private LabResearchOrderService labResearchOrderService;

    @Autowired
    private LabResearchOrderDao labResearchOrderDao;

    @Autowired
    private LabResearchOrderORUDao orderORUDao;

    @Autowired
    private HL7MessageConverter<ORU_R01, ORUR01> oruConverter;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UploadClientDocumentService uploadDocumentService;

    @Autowired
    private LabResearchNotificationService notificationService;

    @Autowired
    private EntityManager entityManager;

    @Value("${xds.document.author.login}")
    private String documentAuthorLogin;

    @Value("${xds.document.author.company}")
    private String documentAuthorCompany;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<LabResearchOrderORU> process(String oruRaw, String fileName) {
        return process(oruRaw, fileName, false);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<LabResearchOrderORU> processTesting(String oruRaw) {
        return process(oruRaw, null, true);
    }

    private Optional<LabResearchOrderORU> process(String oruRaw, String fileName, boolean isTesting) {
        LabResearchOrderORU orderOru = null;
        try {
            orderOru = labResearchOrderService.createOruInNewTransaction(createOrderOru(oruRaw, fileName, isTesting));

            var hapiOru = (ORU_R01) apolloHapiContext.getPipeParser().parse(oruRaw);
            var entityOru = oruConverter.convert(hapiOru);

            orderOru.setOru(entityOru);
            orderOru = orderORUDao.save(orderOru);
            entityOru = orderOru.getOru();

            if (entityOru.getPid().getPatientID() == null) {
                throw new HL7ProcessingException("PID-2 with Simply Connect patient id is not present in the message");
            }
            var clientId = Long.valueOf(entityOru.getPid().getPatientID().getpId());
            var requisitionNumber = entityOru.getOrc().getPlaceOrderNumber().getEntityIdentifier();

            var order = labResearchOrderDao.findFirstByClientIdAndRequisitionNumberAndStatus(clientId,
                    requisitionNumber, LabResearchOrderStatus.SENT_TO_LAB)
                    .orElseThrow(() -> new HL7ProcessingException(
                            String.format("'SENT_TO_LAB' Order with [%S] requisition number wan't found for client [%d]",
                                    requisitionNumber, clientId)));

            orderOru.setLabOrder(order);
            order.setOrderORU(orderOru);
            order.setStatus(LabResearchOrderStatus.PENDING_REVIEW);

            attachDocuments(order, entityOru);
            orderOru.setSuccess(true);

            prepareNotifications(orderOru);

        } catch (Exception e) {
            logFailedProcessingInDb(orderOru, e);

            logger.warn("Rollback transaction for LabResearchOrderORU [{}]", orderOru == null ? null : orderOru.getId());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.warn("Transaction rollback successful for LabResearchOrderORU [{}]", orderOru == null ? null : orderOru.getId());

        }
        return Optional.ofNullable(orderOru);
    }

    private LabResearchOrderORU createOrderOru(String oruRaw, String fileName, boolean isTesting) {
        var orderORU = new LabResearchOrderORU();
        orderORU.setOruLogFileName(saveLogFile(oruRaw, fileName, isTesting));
        orderORU.setReceivedDatetime(Instant.now());
        orderORU.setTesting(isTesting);
        orderORU.setFileName(fileName);
        return orderORU;
    }

    private String saveLogFile(String oruRaw, String fileName, boolean isTesting) {
        var fileStorage = isTesting ? apolloOruLogTestingFileStorage : apolloOruLogFileStorage;
        var finalFileName = fileStorage.save(oruRaw.getBytes(StandardCharsets.UTF_8), fileName);
        return fileStorage.getAbsolutePath(finalFileName);
    }

    private void attachDocuments(LabResearchOrder order, ORUR01 entityOru) {
        var docObxList = CollectionUtils.emptyIfNull(entityOru.getObxList()).stream()
                .filter(obx -> "ED".equals(obx.getValueType())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(docObxList)) {
            logger.warn("OBX with document wasn't provided");
            return;
        }

        docObxList.forEach(obxObservationResult -> saveLabDocument(order, obxObservationResult));
    }

    private void saveLabDocument(LabResearchOrder order, OBXObservationResult docObx) {
        var docObxValue = Optional.of(docObx).stream()
                .map(OBXObservationResult::getObsvValues)
                .flatMap(List::stream)
                .map(OBXValue::getObsvValue)
                .findFirst()
                .orElse(StringUtils.EMPTY);

        var split = docObxValue.split("\\^");
        if (split.length < 5) {
            logger.warn("Document wasn't provided in OBX5.4");
            return;
        }

        var docStream = new ByteArrayInputStream(Base64.getDecoder().decode(split[4]));

        var author = employeeService.getActiveOrInactiveEmployee(documentAuthorLogin, documentAuthorCompany);

        var fileName = buildFileName(order.getClient());
        var docUploadData = new ClientDocumentUploadData(fileName, fileName, MediaType.APPLICATION_PDF_VALUE,
                docStream, order.getClient(), author, SharingOption.ALL)
                .withLabObx(docObx)
                .withLabResearchOrder(order);

        uploadDocumentService.upload(docUploadData);
    }

    private String buildFileName(Client client) {
        return client.getFirstName() + "_" + client.getLastName() + "_lab_results.pdf";
    }

    private void prepareNotifications(LabResearchOrderORU orderOru) {
        try {
            notificationService.prepareResultReceivedNotification(orderOru.getLabOrder());
        } catch (Exception e) {
            logger.warn("Failed to send notifications for order [{}]", orderOru.getLabOrder().getId());
        }
    }

    private void logFailedProcessingInDb(LabResearchOrderORU orderOru, Exception e) {
        logger.warn("Exception during processing LabResearchOrderORU [{}]", orderOru == null ? null : orderOru.getId(), e);
        if (orderOru != null) {

            logger.info("Updating LabResearchOrderORU [{}] for fail in new transaction", orderOru);
            labResearchOrderService.updateOrderOruFailInNewTransaction(orderOru.getId(), ExceptionUtils.getStackTrace(e));
            logger.info("LabResearchOrderORU [{}] is updated in new transaction", orderOru.getId());

            logger.info("Reloading LabResearchOrderORU [{}]", orderOru.getId());
            entityManager.refresh(orderOru);
            logger.info("Reloaded LabResearchOrderORU [{}]", orderOru.getId());

            orderOru.setProcessingException(e);
        }
    }
}
