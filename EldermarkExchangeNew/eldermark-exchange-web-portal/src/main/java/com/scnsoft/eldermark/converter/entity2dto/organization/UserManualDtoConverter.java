package com.scnsoft.eldermark.converter.entity2dto.organization;


import com.scnsoft.eldermark.dto.UserManualDocumentDto;
import com.scnsoft.eldermark.entity.UserManual;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserManualDtoConverter implements Converter<UserManual, UserManualDocumentDto> {

    @Override
    public UserManualDocumentDto convert(UserManual source) {
        var target = new UserManualDocumentDto();
        target.setId(source.getId());
        target.setFileName(source.getFileName());
        target.setTitle(source.getTitle());
        target.setCreatedDate(source.getCreated().toEpochMilli());
        target.setMimeType(source.getMimeType());
        return target;
    }
}
