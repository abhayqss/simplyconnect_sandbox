package com.scnsoft.eldermark.shared.utils.test;

import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.shared.DocumentType;
import com.scnsoft.eldermark.shared.service.validation.SsnValidator;
import com.scnsoft.eldermark.shared.utils.RegistrationUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author phomal
 * Created on 6/12/2017.
 */
public abstract class TestDataGenerator {

    /**
     * Generate random ID
     */
    public static Long randomId() {
        return randomId(10000);
    }

    /**
     * Generate random ID, greater than 0, less than {@code max}.
     *
     * @param max the upper bound (exclusive)
     */
    public static Long randomId(long max) {
        return ThreadLocalRandom.current().nextLong(1, max);
    }

    /**
     * Generate random ID that is not present in the provided ids list.<br/>
     * Use this method in tests that depend on ID uniqueness in order to prevent random ID collisions.
     */
    public static Long randomIdExceptOf(Long... ids) {
        Long id;
        do {
            id = randomId();
        } while (Arrays.asList(ids).contains(id));
        return id;
    }

    @SafeVarargs
    public static <T> T randomObjectFromList(T... args) {
        int i = ThreadLocalRandom.current().nextInt(0, args.length);
        return args[i];
    }

    public static String randomPhone() {
        final String PHONE_CHARS = "0123456789- ";
        return "+" + RandomStringUtils.randomNumeric(3) + "(" + RandomStringUtils.randomNumeric(2) + ")" + RandomStringUtils.random(8, PHONE_CHARS);
    }

    public static String randomEmail() {
        return RandomStringUtils.randomAlphabetic(randomLength()) + "@" + RandomStringUtils.randomAlphabetic(16) + ".com";
    }

     public static String randomApnsToken() {
        return RandomStringUtils.randomAlphanumeric(32);
    }

    public static String randomGcmToken() {
        return RandomStringUtils.randomAlphanumeric(randomLength(163, 255));
    }

    public static String randomValidSsn() {
        String ssn;
        do {
            ssn = RandomStringUtils.randomNumeric(9);
        } while (!SsnValidator.isValidSsn(ssn));
        return ssn;
    }

    public static String randomValidSsnExceptOf(String... ssns) {
        String ssn;
        do {
            ssn = RandomStringUtils.randomNumeric(9);
        } while (!SsnValidator.isValidSsn(ssn) || Arrays.asList(ssns).contains(ssn));
        return ssn;
    }

    public static Long randomConfirmationCode() {
        return RegistrationUtils.generateConfirmationCode();
    }

    public static String randomInvalidSsn() {
        return "9" + RandomStringUtils.randomNumeric(8);
    }

    public static Integer randomTimeZoneOffset() {
        return (RandomUtils.nextInt(12) - 6) * 60;
    }

    public static String randomName() {
        return StringUtils.capitalize(StringUtils.lowerCase(RandomStringUtils.randomAlphabetic(randomLength())));
    }

    public static String randomFullName() {
        return randomName() + " " + randomName();
    }

    public static Date randomBirthDate() {
        final Date startDate = new GregorianCalendar(1917, Calendar.JUNE, 1).getTime();
        final Date endDate = new GregorianCalendar(2017, Calendar.JUNE, 1).getTime();
        long ms = ThreadLocalRandom.current().nextLong(startDate.getTime(), endDate.getTime());
        return new Date(ms);
    }

    public static Date randomDate() {
        final Date endDate = new Date();
        return randomDateBefore(endDate);
    }

    public static Date randomDateBefore(Date endDate) {
        Date startDate = new GregorianCalendar(1917, Calendar.JUNE, 1).getTime();
        if (!startDate.before(endDate)) {
            startDate = new Date(endDate.getTime() - 1000 * 60);
        }
        long ms = ThreadLocalRandom.current().nextLong(startDate.getTime(), endDate.getTime());
        return new Date(ms);
    }

    public static AccountType getConsumerAccountType() {
        AccountType accountType = new AccountType();
        accountType.setId(1L);
        accountType.setType(AccountType.Type.CONSUMER);
        accountType.setName("Consumer");
        return accountType;
    }

    public static AccountType getProviderAccountType() {
        AccountType accountType = new AccountType();
        accountType.setId(2L);
        accountType.setType(AccountType.Type.PROVIDER);
        accountType.setName("Provider");
        return accountType;
    }

    private static int randomLength(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private static int randomLength() {
        return randomLength(4, 16);
    }

    public static Report ccdMetadata() {
        Report document = new Report();
        document.setDocumentTitle("CCD.XML");
        document.setMimeType("text/xml");
        document.setDocumentType(DocumentType.CCD);
        return document;
    }

    public static Report facesheetMetadata() {
        Report document = new Report();
        document.setDocumentTitle("FACESHEET.PDF");
        document.setMimeType("application/pdf");
        document.setDocumentType(DocumentType.FACESHEET);
        return document;
    }

    public static CareTeamRole careTeamRole(CareTeamRoleCode code) {
        final CareTeamRole role = new CareTeamRole();
        role.setId(TestDataGenerator.randomId());
        switch (code) {
            case ROLE_BEHAVIORAL_HEALTH:
                role.setName("Behavioral Health");
                role.setDisplayName("Provider");
                break;
            case ROLE_PARENT_GUARDIAN:
                role.setName("Parent/Guardian");
                role.setDisplayName(null);
                break;
            case ROLE_PRIMARY_PHYSICIAN:
                role.setName("Primary physician");
                role.setDisplayName("Provider");
                break;
        }
        role.setCode(code);
        return role;
    }

}
