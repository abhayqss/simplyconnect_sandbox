package com.scnsoft.eldermark.entity.document;

import java.util.EnumSet;
import java.util.Set;

public enum DocumentAndFolderType {
    CUSTOM, FOLDER, TEMPLATE_FOLDER, TEMPLATE;

    private static final EnumSet<DocumentAndFolderType> folderTypes = EnumSet.of(FOLDER, TEMPLATE_FOLDER);

    private static final EnumSet<DocumentAndFolderType> documentTypes = EnumSet.complementOf(folderTypes);

    public static Set<DocumentAndFolderType> documentTypes() {
        return documentTypes;
    }

    public boolean isFolderType() {
        return folderTypes.contains(this);
    }
}
