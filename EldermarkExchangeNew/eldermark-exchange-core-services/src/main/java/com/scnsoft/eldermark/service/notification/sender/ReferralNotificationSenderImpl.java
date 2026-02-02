package com.scnsoft.eldermark.service.notification.sender;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.converter.ReferralRequestNotificationMailDtoConverter;
import com.scnsoft.eldermark.dao.ReferralRequestNotificationDao;
import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;
import com.scnsoft.eldermark.entity.referral.ReferralRequestNotification;
import com.scnsoft.eldermark.entity.referral.ReferralRequestSharedChannel;
import com.scnsoft.eldermark.service.FaxService;
import com.scnsoft.eldermark.service.ReferralRequestPdfGenerationService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@Service
public class ReferralNotificationSenderImpl implements ReferralNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(ReferralNotificationSenderImpl.class);

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private ReferralRequestNotificationDao referralRequestNotificationDao;

    @Autowired
    private ReferralRequestNotificationMailDtoConverter referralRequestNotificationMailDtoConverter;

    @Autowired
    private Converter<ReferralRequestNotification, BaseFaxNotificationDto> referralRequestNotificationFaxDtoConverter;

    @Autowired
    private ReferralRequestPdfGenerationService referralRequestPdfGenerationService;

    @Autowired
    private FaxService faxService;

    @Async
    @Transactional
    public void send(Long id) {
        var referralRequestNotification = referralRequestNotificationDao.getOne(id);
        try {
            boolean result;
            if (ReferralRequestSharedChannel.FAX == referralRequestNotification.getSharedChannel()) {
                var zoneId = ZoneId.of(referralRequestNotification.getReferralRequest().getZoneId());
                result = faxService.sendAndWait(referralRequestNotificationFaxDtoConverter.convert(referralRequestNotification),
                        referralRequestPdfGenerationService.generatePdfReport(referralRequestNotification.getReferralRequest(), zoneId).getInputStream().readAllBytes());
            } else {
                result = exchangeMailService.sendReferralRequestNotificationAndWait(referralRequestNotificationMailDtoConverter.convert(referralRequestNotification));
            }
            if (result) {
                logger.info("Referral request notification [{}] was sent", referralRequestNotification.getId());
                referralRequestNotification.setSentDatetime(Instant.now());
                referralRequestNotificationDao.save(referralRequestNotification);
            } else {
                logger.info("Referral request notification [{}] wasn't sent", referralRequestNotification.getId());
            }
        } catch (RuntimeException ex) {
            logger.warn("Couldn't send referral request notification [{}]", referralRequestNotification.getId(), ex);
        } catch (DocumentException | IOException ex) {
            logger.error("Couldn't convert referral request [{}] to pdf", referralRequestNotification.getId(), ex);
        }
    }


    private boolean sendAndWait(Supplier<Future<Boolean>> supplier) {
        try {
            return BooleanUtils.isTrue(supplier.get().get());
        } catch (Exception e) { //todo check exception catch
            throw new RuntimeException(e);
        }
    }
}
