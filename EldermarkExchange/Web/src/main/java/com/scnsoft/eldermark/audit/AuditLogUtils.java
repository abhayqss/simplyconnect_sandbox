package com.scnsoft.eldermark.audit;

import com.scnsoft.eldermark.entity.AuditLogAction;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.ResidentDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuditLogUtils {
    public static List<Long> getResidentIds(List<ResidentDto> residents) {
        if(residents == null)
            return null;

        List<Long> ids = new ArrayList<Long>();
        for(ResidentDto residentDto : residents) {
            ids.add(Long.parseLong(residentDto.getId()));
        }

        return ids;
    }

    public static List<Long> getDocumentIds(List<DocumentDto> documents) {
        if(documents == null)
            return null;

        List<Long> ids = new ArrayList<Long>();
        for(DocumentDto documentDto : documents) {
            String id = documentDto.getId();
            // exclude CCD from list, since it is not stored in db
            if(!"0".equals(id)) {
                ids.add(Long.parseLong(documentDto.getId()));
            }
        }

        return ids;
    }

    public static List<Long> toList(Long element) {
        return (element == null) ? null : Arrays.asList(element);
    }

    public static String errorToString(AuditLogAction action, Long employeeId, List<Long> residentIds, List<Long> documentIds) {
        String employeeIdStr = (employeeId != null)? employeeId.toString() : "null";

        return errorToString(action, employeeIdStr, listToString(residentIds), listToString(documentIds));
    }

    private static String listToString(List<Long> list) {
        if(list == null)
            return "null";

        StringBuilder sb = new StringBuilder();
        for(Long element: list) {
            sb.append(element);
            sb.append(", ");
        }

        return sb.toString();
    }

    private static String errorToString(AuditLogAction action, String employeeId, String residentIds, String documentIds) {
        return String.format("Audit log %s: employee_id {%s}, resident_ids {%s}, document_ids {%s}", action, employeeId, residentIds, documentIds);
    }
}