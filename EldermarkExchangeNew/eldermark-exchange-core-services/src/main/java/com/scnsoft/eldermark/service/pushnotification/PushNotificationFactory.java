package com.scnsoft.eldermark.service.pushnotification;

import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;

import java.util.*;

/**
 * Creates {@link PushNotificationVO} instances for various use-cases, defined by {@link PushNotificationType}
 */
public class PushNotificationFactory {

    public static PushNotificationVOBuilder builder(PushNotificationType type) {
        return new PushNotificationVOBuilder(type);
    }

    public static class PushNotificationVOBuilder {
        PushNotificationType type;
        List<PushNotificationVO.Receiver> receivers;
        String devicePushNotificationTokenToExclude;

        private PushNotificationVOBuilder(PushNotificationType type) {
            this.type = Objects.requireNonNull(type);
            receivers = new ArrayList<>();
        }

        public PushNotificationVOBuilder receiver(PushNotificationRegistration.Application app,
                                                  Long userMobileId) {
            receivers.add(new PushNotificationVO.Receiver(app, userMobileId));
            return this;
        }

        public PushNotificationVOBuilder excludingDevice(String devicePushNotificationTokenToExclude) {
            this.devicePushNotificationTokenToExclude = devicePushNotificationTokenToExclude;
            return this;
        }

        public PushNotificationVO build() {
            var vo = new PushNotificationVO(receivers);
            vo.setDevicePushNotificationTokenToExclude(devicePushNotificationTokenToExclude);

            fillPayload(vo);

            vo.setAndroidSettings(type.createAndroidSettings());
            vo.setIosSettings(type.createIosSettings());

            return vo;
        }

        private void fillPayload(PushNotificationVO vo) {
            final Map<String, String> payload = vo.getPayload();
            payload.put("id", String.valueOf(type.getTypeId()));

            //todo - push users for PHR only?
            vo.getReceivers().forEach(ar -> payload.put(ar.getAppName().getUserPayloadKey(), ar.getUserId().toString()));
        }
    }
}
