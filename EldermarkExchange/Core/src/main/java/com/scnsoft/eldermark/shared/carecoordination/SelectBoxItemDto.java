package com.scnsoft.eldermark.shared.carecoordination;

/**
 * Created by averazub on 4/28/2016.
 */
public class SelectBoxItemDto extends KeyValueDto {
    private boolean selected;

    public SelectBoxItemDto() {
    }

    public SelectBoxItemDto(Long id, String label) {
        super(id, label);
    }

    public SelectBoxItemDto(Long id, String label, boolean selected) {
        super(id, label);
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
