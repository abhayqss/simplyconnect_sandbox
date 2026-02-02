package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.phr.MobileUser;

import javax.persistence.*;

@Entity
@Table(name = "PushNotificationRegistration")
public class PushNotificationRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    //todo rename to PhrMobileUser
    private MobileUser mobileUser;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long mobileUserId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "employee_id", insertable = false, updatable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_name", nullable = false)
    private Application appName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MobileUser getMobileUser() {
        return mobileUser;
    }

    public void setMobileUser(MobileUser mobileUser) {
        this.mobileUser = mobileUser;
    }

    public Long getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(Long mobileUserId) {
        this.mobileUserId = mobileUserId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String regId) {
        this.deviceToken = regId;
    }

    public Application getAppName() {
        return appName;
    }

    public void setAppName(Application appName) {
        this.appName = appName;
    }

    /**
     * Push notifications service provider
     */
    public enum ServiceProvider {
        /**
         * Firebase Cloud Messaging
         */
        FCM,
        /**
         * Google Cloud Messaging
         * NOTE that on April 10, 2018, Google deprecated GCM. The GCM server and client APIs were removed
         * on May 29, 2019, and currently any calls to those APIs can be expected to fail.
         * <p>
         * Use FCM instead.
         * <p>
         * todo remove this option if there are no GCM tokens in DB
         */
        @Deprecated
        GCM,
        /**
         * Apple Push Notifications Service UserNotifications Framework
         * notification types: alert, background
         *
         * SB indicates APNS Sandbox
         */
        APNS_UN,
        APNS_UN_SB,
        /**
         * Apple Push Notifications Service PushKit Framework
         * notification types: voip, complication, fileprovider
         *
         * SB indicates APNS Sandbox
         */
        APNS_PK,
        APNS_PK_SB;
    }

    public enum Application {
        /**
         * Legacy PHR application
         */
        PHR(PushNotificationRegistration_.MOBILE_USER_ID, "userId"),

        /**
         * Simply Connect Mobile application
         */
        SCM(PushNotificationRegistration_.EMPLOYEE_ID, "employeeId");

        private final String userIdAttribute;
        private final String userPayloadKey;

        Application(String userIdAttribute, String userPayloadKey) {
            this.userIdAttribute = userIdAttribute;
            this.userPayloadKey = userPayloadKey;
        }

        public String getUserIdAttribute() {
            return userIdAttribute;
        }

        public String getUserPayloadKey() {
            return userPayloadKey;
        }
    }
}
