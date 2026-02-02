package com.scnsoft.eldermark.entity.document.folder;

public enum DocumentFolderPermissionLevelCode {
    ADMIN(3), UPLOADER(2), VIEWER(1);

    private final int priority;

    DocumentFolderPermissionLevelCode(int priority) {
        this.priority = priority;
    }

    public boolean isWiderOrEqualTo(DocumentFolderPermissionLevelCode other) {
        return this.priority >= other.priority;
    }

    public int getPriority() {
        return priority;
    }
}
