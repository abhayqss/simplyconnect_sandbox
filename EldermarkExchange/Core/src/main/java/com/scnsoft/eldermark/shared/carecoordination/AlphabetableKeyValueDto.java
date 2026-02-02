package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.dto.AlphabetableValueDto;
import com.scnsoft.eldermark.shared.carecoordination.dto.AlphabetableValueDtoInterface;
import com.scnsoft.eldermark.shared.carecoordination.dto.KeyValueDtoInterface;

/**
 * Created by pzhurba on 06-Oct-15.
 */
public class AlphabetableKeyValueDto extends KeyValueDto implements AlphabetableValueDtoInterface, KeyValueDtoInterface {

    private AlphabetableValueDto alphabetableValueDto;

    public AlphabetableKeyValueDto() {
    }

    public AlphabetableKeyValueDto(Long id, String label, String titleLetter, boolean firstInLetterSection ) {
        super(id, label);
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
}
