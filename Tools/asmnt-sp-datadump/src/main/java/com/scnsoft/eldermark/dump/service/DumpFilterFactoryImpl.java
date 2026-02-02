package com.scnsoft.eldermark.dump.service;

import com.scnsoft.eldermark.dump.bean.DumpFilter;
import com.scnsoft.eldermark.dump.bean.FileMode;
import com.scnsoft.eldermark.dump.dao.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

@Service
@Transactional(readOnly = true)
public class DumpFilterFactoryImpl implements DumpFilterFactory {

    @Autowired
    private ClientDao clientDao;

    @Override
    public DumpFilter buildFilter() {
        var filter = new DumpFilter();
//        var organizationId = 10408L; //Lutheran Social Services of North Dakota
        var organizationId = 10386L; //genacross

//        var organizationId = 7L; //RBA local

        filter.setOrganizationId(organizationId);
        filter.setFrom(LocalDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.MIN));
        filter.setTo(LocalDateTime.of(LocalDate.of(2020, Month.DECEMBER, 31), LocalTime.MAX));

        filter.setFileMode(FileMode.SINGLE);
//        var residents = Stream.of()
//                .map(Integer::longValue)
//                .collect(Collectors.toList());
//
//        var residents = clientDao.idsInOrganization(organizationId);
//        filter.setResidentIds(residents);
        return filter;
    }
}
