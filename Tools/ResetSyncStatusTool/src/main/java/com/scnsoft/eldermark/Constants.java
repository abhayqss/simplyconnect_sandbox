package com.scnsoft.eldermark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Constants {
    private static final Logger logger = LoggerFactory.getLogger(Constants.class);

    public static final String[] TABLE_NAMES = {
            "Allergies",
            "Companies",
            "Employee",
            "employee_companies",
            "Living_Status",
            "Medical_Professional_Role",
            "Medical_Professionals",
            "Pharmacy",
            "res_admittance_history",
            "Res_Care_History",
            "res_contacts",
            "Res_Diagnosis",
            "Res_Immunization",
            "Res_Med_Professionals",
            "Res_Medications",
            "Res_PaySource_History",
            "Res_Pharmacy",
            "Res_Vitals",
            "resident",
            "Security_Group",
            "system_setup"
    };

    public String[] SOURCE_DATABASES_URLS;

    public static final String SYNC_STATUS_COLUMN = "exchange_sync_status";

    public void loadProperties() {
        InputStream inputStream = null;

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            inputStream = classLoader.getResourceAsStream("config.properties");

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            String databases = prop.getProperty("jdbc.odbc.datasources");

            SOURCE_DATABASES_URLS = databases.split(",");
        } catch (Exception e) {
            logger.error("Exception: " + e);
        } finally {
            if (inputStream != null) try{
                inputStream.close();
            } catch (IOException e) {
                logger.error("Exception: " + e);
            }
        }
    }
}
