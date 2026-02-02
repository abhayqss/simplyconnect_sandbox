package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.dto.KeyValueDtoInterface;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by pzhurba on 06-Oct-15.
 */
public class KeyValueDto implements KeyValueDtoInterface {
    private Long id;
    private String label;

    public KeyValueDto () {}
    public KeyValueDto(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    @Min(1)
    @NotNull
    @ApiModelProperty(required = true, example = "2")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyValueDto that = (KeyValueDto) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        return !(getLabel() != null ? !getLabel().equals(that.getLabel()) : that.getLabel() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getLabel() != null ? getLabel().hashCode() : 0);
        return result;
    }
}
