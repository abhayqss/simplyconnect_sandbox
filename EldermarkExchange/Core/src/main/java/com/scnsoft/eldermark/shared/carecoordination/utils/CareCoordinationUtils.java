package com.scnsoft.eldermark.shared.carecoordination.utils;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.json.CustomDateSerializer;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 03-Nov-15.
 */
public class CareCoordinationUtils {
    public static KeyValueDto createKeyValueDto(final State state) {
        if (state==null) {
            return null;
        }
        return new KeyValueDto(state.getId(), state.getName() + " (" + state.getAbbr() + ")");
    }

    // TODO move normalization to Name insert trigger
    public static String normalizeName(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase().replaceAll("[' \\-]", "");
    }

    public static String normalizePhone(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase().replaceAll("[' \\-+()]", "");
    }

    public static String normalizeEmail(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String base64Encode(String text) {
        try {
            return Base64.encodeBase64String(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to base64 encode text: '" + text + "'", e);
        }
    }


    public static String getFullName(String firstName, String lastName) {
        String result = !StringUtils.isEmpty(firstName) ? lastName : "";
        if (!StringUtils.isEmpty(lastName)) {
            result = !StringUtils.isEmpty(result) ? result + " " + firstName : lastName;
        }
        return result;
    }

    public static Integer tryParseInteger(String source) {
        try {
            return Integer.valueOf(source);
        } catch (NumberFormatException exc) {
            return null;
        }
    }

    public static Date tryParseDate(String source) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomDateSerializer.EXCHANGE_DATE_FORMAT);
        try {
            return dateFormat.parse(source);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getResidentInitials(Resident resident) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(resident.getFirstName()) && org.apache.commons.lang3.StringUtils.isNotEmpty(resident.getLastName())) {
            return resident.getFirstName().substring(0, 1) + ". " + resident.getLastName().substring(0, 1) + ".";
        }

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(resident.getFirstName())) {
            return resident.getFirstName().substring(0, 1) + ".";
        }

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(resident.getLastName())) {
            return resident.getLastName().substring(0, 1) + ".";
        }

        return org.apache.commons.lang3.StringUtils.EMPTY;
    }


}
