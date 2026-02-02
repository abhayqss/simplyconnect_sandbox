package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.PushNotificationRegistration_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationRegistrationSpecificationGenerator {

    public Specification<PushNotificationRegistration> byUserIdAndServiceProviderAndAppNameExcludingToken(
            Long userId,
            PushNotificationRegistration.ServiceProvider serviceProvider,
            PushNotificationRegistration.Application appName,
            String devicePushNotificationTokenToExclude) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(appName.getUserIdAttribute()), userId),
                criteriaBuilder.equal(root.get(PushNotificationRegistration_.serviceProvider), serviceProvider),
                criteriaBuilder.equal(root.get(PushNotificationRegistration_.appName), appName),
                StringUtils.isEmpty(devicePushNotificationTokenToExclude) ?
                        criteriaBuilder.and() :
                        criteriaBuilder.notEqual(
                                root.get(PushNotificationRegistration_.deviceToken),
                                devicePushNotificationTokenToExclude
                        )
        );
    }
}
