package org.openhealthtools.openxds.registry.patient;

import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.Escape;
import com.misyshealthcare.connect.net.Identifier;

/**
 * This class is a copypaste of necessary methods from org.openhealthexchange.openpixpdq.ihe.registry.HL7 class with one
 * additional method to fetch assigningFacility from CX data type.
 *
 * It was needed, because necessary methods are private in original class and using reflection to call them is not safe
 * because internal implementation of original class may change.
 */
public class CustomHL7 {

    private static EncodingCharacters encodingCharacters = new EncodingCharacters('|', "^~\\&");

    private CustomHL7() {
    }

    public static Identifier getAssigningFacilityFromCX(String cx) {
        String aa = getFieldText(cx, 6);
        return aa == null ? null : new Identifier(getComponentText(aa, 1), getComponentText(aa, 2), getComponentText(aa, 3));
    }

    private static String getFieldText(String input, int field) {
        String value = getField(input, field);
        return value == null ? null : fromHL7Text(value);
    }

    private static String fromHL7Text(String text) {
        return text == null ? null : Escape.unescape(text, encodingCharacters);
    }

    private static String getField(String input, int field) {
        return getPart(input, field, "\\^");
    }

    private static String getComponent(String field, int component) {
        return getPart(field, component, "\\&");
    }

    private static String getComponentText(String input, int component) {
        String value = getComponent(input, component);
        return value == null ? null : fromHL7Text(value);
    }

    private static String getPart(String input, int field, String separator) {
        if (input == null) {
            return null;
        } else {
            String[] fields = input.split(separator);
            if (fields == null) {
                return null;
            } else if (fields.length < field) {
                return null;
            } else {
                String result = fields[field - 1];
                if (result == null) {
                    return null;
                } else {
                    return result.equals("") ? null : result;
                }
            }
        }
    }

}
