package com.scnsoft.eldermark.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.xml.bind.ValidationException;
import java.util.*;

public class ValidatingHttpRequest extends HttpServletRequestWrapper {

    public ValidatingHttpRequest(HttpServletRequest request) {

        super(request);
    }

    public String getParameter(String name){

        HttpServletRequest req = (HttpServletRequest) super.getRequest();
        try {
            return validate(name, req.getParameter(name) );
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getHeader(String name){

        HttpServletRequest req = (HttpServletRequest) super.getRequest();
        try {
            return validate(name, req.getHeader(name));
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getRequestURI(){

        HttpServletRequest req = (HttpServletRequest) super.getRequest();
        try {
            return validate("URI", req.getRequestURI());
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String[] getParameterValues(String name){
        String[] params = super.getParameterValues(name);
        String[] newParams = new String[params == null ? 0 : params.length];

        if (params != null) {
            for (int i = 0; i < params.length; i++) {

                try {
                    newParams[i] = validate(name, params[i]); // Apply validation logic to the value
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            }
        }

        return newParams;
    }

    public Map<String, String[]> getParameterMap() {

        Map<String, String[]> map = super.getParameterMap();

        Iterator iterator = map.keySet().iterator();

        Map<String, String[]> newMap = new LinkedHashMap<String, String[]>();

        while (iterator.hasNext()) {

            String key = iterator.next().toString();

            String []values = map.get(key);
            String []newValues = new String[values.length];

            for(int i = 0; i < values.length; i++){

                try {
                    newValues[i] = validate(key, values[i]); // Apply validation logic to the value
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
            }

            newMap.put(key, newValues);
        }
        return newMap;
    }

    private String validate( String name, String input ) throws ValidationException {

        if (input != null) {
            // important - always canonicalize before validating
            String canonical = canonicalize(input);
            // check to see if input matches whitelist character set
            return canonical;
        } else {
            return null;
        }
    }

    // Simplifies input to its simplest form to make encoding tricks more difficult
    private String canonicalize( String input ) {

        String canonical = sun.text.Normalizer.normalize( input, java.text.Normalizer.Form.NFC, 0 );
        return canonical;
    }
}
