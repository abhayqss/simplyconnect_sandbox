package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.dto.AlphabetableValueDto;
import com.scnsoft.eldermark.shared.carecoordination.dto.AlphabetableValueDtoInterface;
import com.scnsoft.eldermark.shared.carecoordination.dto.KeyTwoValuesDtoInterface;

/**
 * Created by pzhurba on 06-Oct-15.
 */
public class AlphabetableKeyTwoValuesDto implements AlphabetableValueDtoInterface, KeyTwoValuesDtoInterface {

    private AlphabetableValueDto alphabetableValueDto;
    private KeyTwoValuesDto keyTwoValuesDto;

    public AlphabetableKeyTwoValuesDto() {
    }

    public AlphabetableKeyTwoValuesDto(Long id, String label, String secondLabel, String titleLetter, boolean firstInLetterSection) {
        this.keyTwoValuesDto = new KeyTwoValuesDto(id, label, secondLabel);
        this.alphabetableValueDto = new AlphabetableValueDto(titleLetter, firstInLetterSection);
    }

    @Override
    public String getTitleLetter() {
        return alphabetableValueDto.getTitleLetter();
    }

    @Override
    public void setTitleLetter(String titleLetter) {
        alphabetableValueDto.setTitleLetter(titleLetter);
    }

    @Override
    public boolean isFirstInLetterSection() {
        return alphabetableValueDto.isFirstInLetterSection();
    }

    @Override
    public void setFirstInLetterSection(boolean firstInLetterSection) {
        alphabetableValueDto.setFirstInLetterSection(firstInLetterSection);
    }

    @Override
    public String getSecondLabel() {
        return keyTwoValuesDto.getSecondLabel();
    }

    @Override
    public void setSecondLabel(String secondLabel) {
        keyTwoValuesDto.setSecondLabel(secondLabel);
    }

    @Override
    public Long getId() {
        return keyTwoValuesDto.getId();
    }

    @Override
    public void setId(Long id) {
        keyTwoValuesDto.setId(id);
    }

    @Override
    public String getLabel() {
        return keyTwoValuesDto.getLabel();
    }

    @Override
    public void setLabel(String label) {
        keyTwoValuesDto.setLabel(label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlphabetableKeyTwoValuesDto that = (AlphabetableKeyTwoValuesDto) o;

        if (!alphabetableValueDto.equals(that.alphabetableValueDto)) return false;
        return keyTwoValuesDto.equals(that.keyTwoValuesDto);
    }

    @Override
    public int hashCode() {
        int result = alphabetableValueDto.hashCode();
        result = 31 * result + keyTwoValuesDto.hashCode();
        return result;
    }
}
