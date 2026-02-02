package com.scnsoft.eldermark.dto;

public class PasswordComplexityRules {
    
    private Long length;
    
    private Long alphabeticCount;
    
    private Long upperCaseCount;
    
    private Long lowerCaseCount;
    
    private Long arabicNumeralCount;
    
    private Long nonAlphaNumeralCount;

    private Long spacesLessThan;

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getAlphabeticCount() {
        return alphabeticCount;
    }

    public void setAlphabeticCount(Long alphabeticCount) {
        this.alphabeticCount = alphabeticCount;
    }

    public Long getUpperCaseCount() {
        return upperCaseCount;
    }

    public void setUpperCaseCount(Long upperCaseCount) {
        this.upperCaseCount = upperCaseCount;
    }

    public Long getLowerCaseCount() {
        return lowerCaseCount;
    }

    public void setLowerCaseCount(Long lowerCaseCount) {
        this.lowerCaseCount = lowerCaseCount;
    }

    public Long getArabicNumeralCount() {
        return arabicNumeralCount;
    }

    public void setArabicNumeralCount(Long arabicNumeralCount) {
        this.arabicNumeralCount = arabicNumeralCount;
    }

    public Long getNonAlphaNumeralCount() {
        return nonAlphaNumeralCount;
    }

    public void setNonAlphaNumeralCount(Long nonAlphaNumeralCount) {
        this.nonAlphaNumeralCount = nonAlphaNumeralCount;
    }

    public Long getSpacesLessThan() {
        return spacesLessThan;
    }

    public void setSpacesLessThan(Long spacesLessThan) {
        this.spacesLessThan = spacesLessThan;
    }
}
