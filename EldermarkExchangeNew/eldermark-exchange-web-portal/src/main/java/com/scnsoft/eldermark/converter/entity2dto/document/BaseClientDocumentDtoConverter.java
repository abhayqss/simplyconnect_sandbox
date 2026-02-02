package com.scnsoft.eldermark.converter.entity2dto.document;

import com.scnsoft.eldermark.beans.security.projection.entity.ClientDocumentSecurityAwareEntityImpl;
import com.scnsoft.eldermark.dto.document.BaseDocumentDto;
import com.scnsoft.eldermark.dto.document.category.DocumentCategoryItemDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.document.SharingOption;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.service.document.ClientDocumentSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.Comparator;
import java.util.stream.Collectors;

public abstract class BaseClientDocumentDtoConverter<T extends BaseDocumentDto> implements Converter<ClientDocument, T> {

    public static final String MARCO_DOCUMENTS_DATASOURCE = "Simply Connect HIE";

    @Autowired
    protected ClientDocumentSecurityService clientDocumentSecurityService;

    @Autowired
    private Converter<DocumentCategory, DocumentCategoryItemDto> categoryDtoConverter;

    protected void fillData(ClientDocument source, T target) {
        Employee author = source.getAuthor();

        Client client = source.getClient();
        Organization organization = client.getOrganization();
        Community community = client.getCommunity();
        target.setAuthor(author.getFullName());

        target.setOrganizationTitle(organization.getName());
        target.setOrganizationOid(organization.getOid());
        if (community != null) {
            target.setCommunityTitle(community.getName());
            target.setCommunityOid(community.getOid());
        }

        target.setId(source.getId());
        target.setTitle(source.getDocumentTitle());
        target.setCreatedDate(DateTimeUtils.toEpochMilli(source.getCreationTime()));
        target.setMimeType(DocumentUtils.resolveMimeType(source));
        target.setSize(source.getSize());
        target.setType(source.getDocumentType().getTitle());

        if (source.getEldermarkShared()) {
            target.setSharedWith(SharingOption.ALL.name());
        } else {
            target.setSharedWith(organization.getName());
        }

        var categories = source.getCategories().stream()
                .map(categoryDtoConverter::convert)
                .sorted(Comparator.comparing(DocumentCategoryItemDto::getName))
                .collect(Collectors.toList());

        target.setCategories(categories);
        target.setDescription(source.getDescription());

        var securityAware = new ClientDocumentSecurityAwareEntityImpl(source);
        target.setCanDelete(clientDocumentSecurityService.canDelete(securityAware));
        target.setCanEdit(clientDocumentSecurityService.canEdit(securityAware));
        target.setIsTemporarilyDeleted(source.getTemporaryDeleted());
        DocumentUtils.adjustForIntegrations(source, target);
    }
}
