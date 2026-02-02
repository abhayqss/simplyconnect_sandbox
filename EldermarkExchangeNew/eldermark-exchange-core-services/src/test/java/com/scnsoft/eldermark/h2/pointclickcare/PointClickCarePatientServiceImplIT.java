package com.scnsoft.eldermark.h2.pointclickcare;

import com.scnsoft.eldermark.h2.BaseH2IT;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCarePatientService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
public class PointClickCarePatientServiceImplIT extends BaseH2IT {

    @Autowired
    private PointClickCarePatientService instance;

    @Nested
    class PatientById {

        @Test
        @Order(1)
        public void patientById_NoPatient_NewCreated() {
            //todo implement
        }
    }
}
