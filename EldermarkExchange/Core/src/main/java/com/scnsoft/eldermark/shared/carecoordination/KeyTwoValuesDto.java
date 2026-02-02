package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.dto.KeyTwoValuesDtoInterface;

/**
 * Created by ggavrysh
 */
public class KeyTwoValuesDto extends KeyValueDto implements KeyTwoValuesDtoInterface {

    private String secondLabel;

    public KeyTwoValuesDto() {}

    public KeyTwoValuesDto(Long id, String label, String secondLabel) {
        super(id, label);
        this.secondLabel = secondLabel;
    }

    public String getSecondLabel() {
        return secondLabel;
    }

    public void setSecondLabel(String secondLabel) {
        this.secondLabel = secondLabel;
    }
}