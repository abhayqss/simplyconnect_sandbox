package com.scnsoft.eldermark.shared;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateBirthXmlAdapter extends XmlAdapter<String, Date> {
    private static final String FORMAT = "yyyy-MM-dd";

    @Override
    public Date unmarshal(String v) throws Exception {
        return new SimpleDateFormat(FORMAT).parse(v);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return new SimpleDateFormat(FORMAT).format(date);
    }
}
