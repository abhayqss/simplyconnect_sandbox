package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;

import java.util.List;

public interface CcdCodeFacade {

    List<CcdCodeDto> findReferralReason(String search);
}
