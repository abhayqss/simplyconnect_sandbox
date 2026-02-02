package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author phomal Created on 6/2/2017.
 */
@Repository
public interface PushNotificationRegistrationDao extends JpaRepository<PushNotificationRegistration, Long> {

    List<PushNotificationRegistration> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p.deviceToken FROM PushNotificationRegistration p WHERE p.user.id in :userId " +
            "AND p.serviceProvider = :serviceProvider AND p.appName = :appName")
    List<String> getTokenMultiUserIdAndServiceProviderAndAppName(
            @Param("userId") List<Long> userId,
            @Param("serviceProvider") PushNotificationRegistration.ServiceProvider serviceProvider,
            @Param("appName") String appName);

    List<PushNotificationRegistration> findByUserIdAndServiceProviderAndAppName(
            @Param("userId") Long userId,
            @Param("serviceProvider") PushNotificationRegistration.ServiceProvider serviceProvider,
            @Param("appName") String appName);

    PushNotificationRegistration findByDeviceTokenAndServiceProviderAndAppName(
            @Param("deviceToken") String redId,
            @Param("serviceProvider") PushNotificationRegistration.ServiceProvider serviceProvider,
            @Param("appName") String appName);

    @Modifying
    @Query("delete from PushNotificationRegistration where deviceToken in :deviceTokens and serviceProvider = :serviceProvider " +
            "and appName = :appName")
    void deleteByDeviceTokenAndServiceProviderAndAppName(
            @Param("deviceTokens") List<String> deviceTokens,
            @Param("serviceProvider") PushNotificationRegistration.ServiceProvider serviceProvider,
            @Param("appName") String appName);
}
