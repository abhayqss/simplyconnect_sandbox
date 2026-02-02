package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.phr.VitalSignType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created by phomal on 2/16/2018.
 */
@Service
@Transactional(readOnly = true)
public class VitalSignService {

    Logger logger = Logger.getLogger(VitalSignService.class.getName());

    public static Map<VitalSignType, String> getVitalSigns() {
        Map<VitalSignType, String> map = new HashMap<>();
        for (VitalSignType vitalSignType : VitalSignType.values()) {
            map.put(vitalSignType, vitalSignType.displayName());
        }
        return map;
    }

}
