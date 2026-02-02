package com.scnsoft.eldermark.shared.carecoordination.dto;

/**
 * Created by ggavrysh
 */
public class AlphabetableValueDto implements AlphabetableValueDtoInterface {

    private String titleLetter;
    private boolean firstInLetterSection;

    public AlphabetableValueDto() {
    }

    public AlphabetableValueDto(String titleLetter, boolean firstInLetterSection) {
        this.titleLetter = titleLetter;
        this.firstInLetterSection = firstInLetterSection;
    }

    public String getTitleLetter() {
        return titleLetter;
    }

    public void setTitleLetter(String titleLetter) {
        this.titleLetter = titleLetter;
    }

    public boolean isFirstInLetterSection() {
        return firstInLetterSection;
    }

    public void setFirstInLetterSection(boolean firstInLetterSection) {
        this.firstInLetterSection = firstInLetterSection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlphabetableValueDto that = (AlphabetableValueDto) o;

        return titleLetter.equals(that.titleLetter);
    }

    @Override
    public int hashCode() {
        return titleLetter.hashCode();
    }
}
