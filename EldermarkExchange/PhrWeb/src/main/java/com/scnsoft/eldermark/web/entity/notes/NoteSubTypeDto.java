package com.scnsoft.eldermark.web.entity.notes;

import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.web.entity.BaseDisplayableNamedKeyDto;
import io.swagger.annotations.ApiModel;

import javax.annotation.Generated;

/**
 * Subtype of a note
 */
@ApiModel(description = "Subtype of a note")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-17T14:26:11.290+03:00")
public class NoteSubTypeDto extends BaseDisplayableNamedKeyDto {

    public NoteSubTypeDto(Long id, String name, String key) {
        super(id, name, key);
    }

    public NoteSubTypeDto() {
    }

    public NoteSubTypeDto(Long id, String description, NoteSubType.FollowUpCode followUpCode) {
        super(id, description, followUpCode != null ? followUpCode.getCode() : null);
    }
}
