package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.dao.palatiumcare.HandsetDao;
import com.scnsoft.eldermark.entity.palatiumcare.Handset;
import com.scnsoft.eldermark.mapper.palatiumcare.NotifyHandsetMapper;
import com.scnsoft.eldermark.shared.palatiumcare.GenericMapper;
import com.scnsoft.eldermark.services.palatiumcare.BasicService;
import com.scnsoft.eldermark.shared.palatiumcare.HandsetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class HandsetService extends BasicService<Handset, HandsetDto> {

    private HandsetDao handsetDao;

    @Autowired
    public void setHandsetDao(HandsetDao handsetDao) {
        this.handsetDao = handsetDao;
    }

    @Override
    protected GenericMapper<Handset, HandsetDto> getMapper() {
        return new NotifyHandsetMapper();
    }

    @Override
    protected CrudRepository<Handset, Long> getCrudRepository() {
        return handsetDao;
    }

}
