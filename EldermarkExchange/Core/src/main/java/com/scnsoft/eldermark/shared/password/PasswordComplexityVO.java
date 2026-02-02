package com.scnsoft.eldermark.shared.password;

/**
 * @author phomal
 * Created on 11/14/2017.
 */
public class PasswordComplexityVO {
    private Long arabicNumeralsCount = 0L;
    private Long lowercaseCount = 0L;
    private Long uppercaseCount = 0L;
    private Long specialCharsCount = 0L;
    private Long alphabeticCount = 0L;
    private Long passwordLength = 0L;

    public Long getArabicNumeralsCount() {
        return arabicNumeralsCount;
    }

    public void setArabicNumeralsCount(Long arabicNumeralsCount) {
        this.arabicNumeralsCount = arabicNumeralsCount;
    }

    public Long getLowercaseCount() {
        return lowercaseCount;
    }

    public void setLowercaseCount(Long lowercaseCount) {
        this.lowercaseCount = lowercaseCount;
    }

    public Long getUppercaseCount() {
        return uppercaseCount;
    }

    public void setUppercaseCount(Long uppercaseCount) {
        this.uppercaseCount = uppercaseCount;
    }

    public Long getSpecialCharsCount() {
        return specialCharsCount;
    }

    public void setSpecialCharsCount(Long specialCharsCount) {
        this.specialCharsCount = specialCharsCount;
    }

    public Long getAlphabeticCount() {
        return alphabeticCount;
    }

    public void setAlphabeticCount(Long alphabeticCount) {
        this.alphabeticCount = alphabeticCount;
    }

    public Long getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(Long passwordLength) {
        this.passwordLength = passwordLength;
    }

    public boolean isConfigured() {
        return arabicNumeralsCount > 0L || lowercaseCount > 0L || uppercaseCount > 0L || specialCharsCount > 0L || alphabeticCount > 0L || passwordLength > 0L;
    }
}
