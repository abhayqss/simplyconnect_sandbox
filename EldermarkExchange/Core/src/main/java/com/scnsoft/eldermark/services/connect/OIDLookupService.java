package com.scnsoft.eldermark.services.connect;


public class OIDLookupService {

    public static String lookup(String oid) {
        if ("1.2.840.114350.1.13.8.3.7.3.688884.100".equals(oid))
            return "Allina Health";
        else
            return oid;
    }
}
