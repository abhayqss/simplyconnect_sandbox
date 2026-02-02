package com.scnsoft.eldermark.converter.dto2entity.document.folder;

import com.scnsoft.eldermark.converter.base.ItemConverter;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderPermissionDto;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolderPermission;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderPermissionLevelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DocumentFolderEntityConverter implements ItemConverter<DocumentFolderDto, DocumentFolder> {

    @Autowired
    private DocumentCategoryService categoryService;

    @Autowired
    private DocumentFolderPermissionLevelService permissionLevelService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public DocumentFolder convert(DocumentFolderDto source) {
        var target = new DocumentFolder();
        convert(source, target);
        return target;
    }

    @Override
    public void convert(DocumentFolderDto source, DocumentFolder target) {
        if (target.getId() == null) {
            target.setParentId(source.getParentId());

            target.setCommunityId(source.getCommunityId());
            target.setCommunity(communityService.findById(source.getCommunityId()));
            if (target.getCommunity() == null) {
                throw new ValidationException("Invalid community id");
            }
        }

        target.setName(source.getName());
        target.setIsSecurityEnabled(Boolean.TRUE.equals(source.getIsSecurityEnabled()));

        updateCategories(source, target);
        updatePermissions(source, target);
    }

    private void updateCategories(DocumentFolderDto source, DocumentFolder target) {
        if (CollectionUtils.isNotEmpty(source.getCategoryIds())) {
            var foundCategories = categoryService.findAllByOrganizationIdAndIds(
                target.getCommunity().getOrganizationId(),
                source.getCategoryIds()
            );
            var categoryChainIds = foundCategories.stream()
                .map(it -> it.getChainId() == null ? it.getId() : it.getChainId())
                .collect(Collectors.toList());
            target.setCategoryChainIds(categoryChainIds);
        } else {
            target.setCategoryChainIds(new ArrayList<>());
        }
    }

    private void updatePermissions(DocumentFolderDto source, DocumentFolder target) {
        if (target.getPermissions() == null) {
            target.setPermissions(new ArrayList<>());
        }

        var existing = target.getPermissions();
        var updated = source.getPermissions();

        if (CollectionUtils.isNotEmpty(updated)) {

            // Update or remove already existing permissions
            var unprocessedUpdatedMap = updated.stream()
                .filter(it -> it.getId() != null)
                .collect(Collectors.toMap(DocumentFolderPermissionDto::getId, Function.identity()));

            var iterator = existing.iterator();
            while (iterator.hasNext()) {

                var existingItem = iterator.next();

                if (unprocessedUpdatedMap.containsKey(existingItem.getId())) {
                    var updatedItem = unprocessedUpdatedMap.remove(existingItem.getId());
                    existingItem.setEmployeeId(updatedItem.getContactId());
                    existingItem.setEmployee(employeeService.getEmployeeById(updatedItem.getContactId()));
                    existingItem.setPermissionLevel(permissionLevelService.findById(updatedItem.getPermissionLevelId()));
                } else {
                    iterator.remove();
                }
            }

            if (!unprocessedUpdatedMap.isEmpty()) {
                throw new ValidationException("Folder permission not found");
            }

            // Add new permissions
            updated.stream()
                .filter(it -> it.getId() == null)
                .map(it -> {
                    var newItem = new DocumentFolderPermission();
                    newItem.setFolder(target);
                    newItem.setPermissionLevel(permissionLevelService.findById(it.getPermissionLevelId()));
                    newItem.setEmployeeId(it.getContactId());
                    newItem.setEmployee(employeeService.getEmployeeById(it.getContactId()));
                    return newItem;
                })
                .forEach(existing::add);
        } else {
            existing.clear();
        }
    }
}
