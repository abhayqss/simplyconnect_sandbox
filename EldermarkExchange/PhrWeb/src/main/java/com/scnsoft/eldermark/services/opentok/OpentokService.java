package com.scnsoft.eldermark.services.opentok;

import com.opentok.exception.OpenTokException;
import com.scnsoft.eldermark.entity.phr.OpentokEntity;

public interface OpentokService {
    
    OpentokEntity createSession() throws OpenTokException;
}
