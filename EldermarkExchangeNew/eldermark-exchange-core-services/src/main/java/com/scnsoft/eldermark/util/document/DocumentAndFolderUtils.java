package com.scnsoft.eldermark.util.document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentAndFolderUtils {

    private static final Pattern TEMPLATE_ID_PATTERN = Pattern.compile("^t(\\d+)_(\\d+)$");

    public static Long getFolderId(String documentAndFolderId) {
        if (isFolderId(documentAndFolderId)) {
            try {
                return Long.parseLong(documentAndFolderId.substring(1));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid document and folder id", e);
            }
        } else {
            throw new IllegalArgumentException("Invalid document and folder id");
        }
    }

    public static Long getCommunityIdFromTemplateFolderId(String documentAndFolderId) {
        if (isTemplateFolder(documentAndFolderId)) {
            try {
                return Long.parseLong(documentAndFolderId.substring(2));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid template folder id", e);
            }
        } else {
            throw new IllegalArgumentException("Invalid template folder id");
        }
    }

    public static Long getDocumentId(String documentAndFolderId) {
        try {
            return Long.parseLong(documentAndFolderId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid document and folder id", e);
        }
    }

    public static Long getTemplateId(String documentAndFolderId) {
        var matcher = TEMPLATE_ID_PATTERN.matcher(documentAndFolderId);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Invalid template id");
        }
    }

    public static boolean isTemplateFolder(String documentAndFolderId) {
        return documentAndFolderId.startsWith("tf");
    }

    public static boolean isFolderId(String documentAndFolderId) {
        return documentAndFolderId.startsWith("f");
    }

    public static boolean isDocumentId(String documentAndFolderId) {
        return !isFolderId(documentAndFolderId);
    }

    public static String toFolderId(Long folderId) {
        return "f" + folderId;
    }

    public static String toDocumentId(Long documentId) {
        return documentId.toString();
    }

}
