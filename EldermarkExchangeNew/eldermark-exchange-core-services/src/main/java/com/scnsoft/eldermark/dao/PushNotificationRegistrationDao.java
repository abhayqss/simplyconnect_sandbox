package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PushNotificationRegistrationDao extends AppJpaRepository<PushNotificationRegistration, Long> {

        @Modifying
    @Query("delete from PushNotificationRegistration where deviceToken in :deviceTokens and serviceProvider = :serviceProvider")
    void deleteByRegIdAndServiceProvider(
            @Param("deviceTokens") List<String> deviceTokens,
            @Param("serviceProvider") PushNotificationRegistration.ServiceProvider serviceProvider);

    PushNotificationRegistration findByDeviceTokenAndServiceProviderAndAppName(
            @Param("deviceToken") String redId,
            @Param("serviceProvider") PushNotificationRegistration.ServiceProvider serviceProvider,
            @Param("appName") PushNotificationRegistration.Application appName);
}
