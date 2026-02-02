package com.scnsoft.eldermark.services;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import com.scnsoft.eldermark.dao.DatabaseJpaDao;
import com.scnsoft.eldermark.dao.carecoordination.AdtMessageDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.PV1SegmentContainingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.scnsoft.eldermark.services.hl7.util.Hl7Utils.toHl7TSFormat;
import static java.lang.Boolean.TRUE;

@Service
public class OutboundAdtServiceImpl implements OutboundAdtService {

    private static final Logger logger = LoggerFactory.getLogger(OutboundAdtServiceImpl.class);

    @Autowired
    @Qualifier("kobleContext")
    private HapiContext context;

    @Autowired
    private DatabaseJpaDao databaseJpaDao;

    @Value("${koble.adt.enabled}")
    private Boolean isKobleEnabled;

    @Value("${koble.namespace}")
    private String kobleNamespace;

    @Value("${koble.oid}")
    private String kobleOid;

    @Value("${koble.ip}")
    private String kobleIp;

    @Value("${koble.port}")
    private int koblePort;

    @Autowired
    private PreProcessAdtService preProcessAdtService;

    private final AdtMessageDao adtMessageDao;
    private final Converter<AdtMessage, Message> adt251Converter;

    @Autowired
    public OutboundAdtServiceImpl(AdtMessageDao adtMessageDao, Converter<AdtMessage, Message> adt251Converter) {
        this.adtMessageDao = adtMessageDao;
        this.adt251Converter = adt251Converter;
    }

    @Override
    public void sendOutAdts(Long adtMsgId) {
        if(TRUE.equals(isKobleEnabled) && isOrganizationInWhitelist(adtMsgId)){
            final AdtMessage adtMessage = adtMessageDao.findOne(adtMsgId);
            preProcessAdtService.preprocessMessage(adtMessage);
            sendToKobbleGroup(adtMessage);
        } else {
            logger.info("[KOBLE] Koble integration is not enabled or source database for adt msg id {} is not in whitelist.", adtMsgId);
        }
    }

    private boolean isOrganizationInWhitelist(Long adtMsgId){
        Database sourceDatabase = databaseJpaDao.findByAdtMessageId(adtMsgId);
        return sourceDatabase != null && TRUE.equals(sourceDatabase.getKobleIntegrationEnabled());
    }

    private void sendToKobbleGroup(AdtMessage adtMessage) {
        Connection connection = null;
        try {
            Message message = adt251Converter.convert(adtMessage);

            Terser terser = new Terser(message);
            terser.set("MSH-5-1", kobleNamespace);
            terser.set("MSH-5-2", kobleOid);
            terser.set("MSH-7", toHl7TSFormat(toBeginOfDay(new Date())));
            terser.set("MSH-10", "Koble_" + adtMessage.getId());
            terser.set("PV1-19", toString(((PV1SegmentContainingMessage) adtMessage).getPv1().getId()));

            logger.info("[KOBLE] Outgoing message is {}", message.encode());

            connection = context.newClient(kobleIp, koblePort, true); //ip port
            Initiator initiator = connection.getInitiator();
            Message responseMessage = initiator.sendAndReceive(message);
            if (responseMessage != null){
                logger.info("[KOBLE] Koble response message is {}", responseMessage.encode());
            }
        } catch (Exception ex) {
            logger.error("[KOBLE] Couldn't send or receive Adt Message [{}]", adtMessage.getId(),ex);
        } finally {
            if (connection != null){
                connection.close();
                logger.error("[KOBLE] Connection is closed for {}", adtMessage.getId());
            }
        }
    }

    private String toString(Object obj){
        return obj != null ? obj.toString() : null;
    }

    private Date toBeginOfDay(Date date){
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return  cal.getTime();
    }

}
