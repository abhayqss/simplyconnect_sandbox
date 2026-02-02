package com.scnsoft.eldermark.listener;

import com.scnsoft.eldermark.beans.ConversationType;
import com.scnsoft.eldermark.dao.OneTimeUpdateDao;
import com.scnsoft.eldermark.dao.TwilioConversationDao;
import com.scnsoft.eldermark.exception.ApplicationException;
import com.scnsoft.eldermark.service.EncryptionKeyService;
import com.scnsoft.eldermark.service.HieConsentPolicyOneTimeUpdateService;
import com.scnsoft.eldermark.service.twilio.TwilioAttributeService;
import com.scnsoft.eldermark.util.SdohReportUtils;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.conversations.v1.service.Conversation;
import com.twilio.rest.conversations.v1.service.conversation.Webhook;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class OneTimeUpdatesOnStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(OneTimeUpdatesOnStartupListener.class);

    private final TransactionTemplate transactionTemplate;

    @Autowired
    public OneTimeUpdatesOnStartupListener(PlatformTransactionManager transactionManager) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Autowired
    private HieConsentPolicyOneTimeUpdateService hieConsentPolicyOneTimeUpdateService;

    private final Map<String, Supplier<Boolean>> oneTimeUpdatesMap = Map.ofEntries(
            Map.entry(
                    "missing-webhooks-and-new-filter-and-friendly-names-and-created-date",
                    this::missingWebhooksAndNewFilterAndFriendlyNamesAndCreatedDate
            ),
            Map.entry(
                    "missing-sdoh-report-periods",
                    this::missingSdohReportPeriods
            ),
            Map.entry(
                    "update-webhooks-url",
                    this::updateWebhooks
            ),
            Map.entry(
                    "generate-document-encryption-key",
                    this::generateDocumentEncryptionKey
            ),
            Map.entry(
                    "update-hie-consent-policy",
                    () -> hieConsentPolicyOneTimeUpdateService.updateHieConsentPolicy()
            )
    );

    @Autowired
    private OneTimeUpdateDao oneTimeUpdateDao;

    @EventListener(ApplicationReadyEvent.class)
    @Order(value = Integer.MIN_VALUE + 1)
    public void runOneTimeUpdates() {
        logger.info("Fetching one-time updates to be applied");
        var updates = oneTimeUpdateDao.findAllByAppliedAtIsNullOrderByApplyOrderingAsc();

        if (updates.size() == 0) {
            logger.info("No one-time updates to be applied");
        }

        for (var update : updates) {
            logger.info("Applying update {}", update.getUpdateName());
            transactionTemplate.execute(tx -> {
                var appliedSuccessfully = oneTimeUpdatesMap.get(update.getUpdateName()).get();
                if (appliedSuccessfully) {
                    logger.info("Successfully applied update {}", update.getUpdateName());
                    oneTimeUpdateDao.appliedSuccessfully(update.getUpdateName(), Instant.now());
                } else {
                    logger.info("Failed to apply update {}", update.getUpdateName());
                }
                return null;
            });
        }
    }

    @Autowired
    private SdohReportUtils sdohReportUtils;

    @Value("${twilio.chat.service.sid}")
    private String chatServiceSid;

    @Value("${twilio.chat.enabled}")
    private boolean isChatEnabled;

    @Value("${twilio.conversation.webhook.filters}")
    private List<String> conversationWebhookFilters;

    @Value("${twilio.conversation.webhook.path}")
    private String conversationWebhookPath;

    @Autowired
    private TwilioRestClient twilioRestClient;

    @Autowired
    private TwilioAttributeService twilioAttributeService;

    @Autowired
    private TwilioConversationDao twilioConversationDao;

    @Autowired
    private EncryptionKeyService encryptionKeyService;

    private boolean missingWebhooksAndNewFilterAndFriendlyNamesAndCreatedDate() {
        if (!isChatEnabled) {
            logger.info("Won't run missing-webhooks-and-new-filter-and-friendly-names update - chats are disabled");
            return false;
        }

        Conversation.reader(chatServiceSid).read(twilioRestClient).forEach(
                conversation -> {
                    var attributes = twilioAttributeService.parse(conversation);
                    if (!attributes.getType().equals(ConversationType.SERVICE)) {
                        var name = twilioAttributeService.parse(conversation).getFriendlyName();
                        if (StringUtils.isNotEmpty(name)) {
                            twilioConversationDao.updateFriendlyName(conversation.getSid(), name);
                        }

                        twilioConversationDao.updateDateCreated(conversation.getSid(), conversation.getDateCreated().toInstant());

                        var webhooks = Webhook.reader(chatServiceSid, conversation.getSid())
                                .read(twilioRestClient);

                        if (webhooks.iterator().hasNext()) {
                            webhooks.forEach(webhook -> Webhook.updater(chatServiceSid, conversation.getSid(), webhook.getSid())
                                    .setConfigurationFilters(conversationWebhookFilters)
                                    .update(twilioRestClient));
                        } else {
                            if (StringUtils.isNotEmpty(conversationWebhookPath)) {
                                Webhook.creator(chatServiceSid, conversation.getSid(), Webhook.Target.WEBHOOK)
                                        .setConfigurationMethod(Webhook.Method.POST)
                                        .setConfigurationFilters(conversationWebhookFilters)
                                        .setConfigurationUrl(conversationWebhookPath)
                                        .create(twilioRestClient);
                            }
                        }
                    }
                }
        );
        return true;
    }

    private boolean missingSdohReportPeriods() {
        sdohReportUtils.generateMissingReportPeriods();
        return true;
    }

    private boolean updateWebhooks() {
        if (!isChatEnabled) {
            logger.info("update-webhooks-url - chats are disabled");
            return false;
        }
        Conversation.reader(chatServiceSid).read(twilioRestClient).forEach(
                conversation -> {
                    var webhooks = Webhook.reader(chatServiceSid, conversation.getSid()).read(twilioRestClient);
                    if (webhooks.iterator().hasNext()) {
                        webhooks.forEach(webhook -> Webhook.updater(chatServiceSid, conversation.getSid(), webhook.getSid())
                                .setConfigurationUrl(conversationWebhookPath)
                                .update(twilioRestClient));
                    }
                }
        );
        return true;
    }

    private boolean generateDocumentEncryptionKey() {
        try {
            encryptionKeyService.create();
            return true;
        } catch (ApplicationException exception) {
            return false;
        }
    }
}
