package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.SupportTicketReceiverConfigurationDao;
import com.scnsoft.eldermark.entity.SupportTicketReceiverConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbSupportConfigurationService implements SupportConfigurationService {

    @Autowired
    private SupportTicketReceiverConfigurationDao supportTicketReceiverConfigurationDao;

    @Override
    @Transactional(readOnly = true)
    public List<String> getSupportTicketReceiverEmails() {
        return supportTicketReceiverConfigurationDao.findAll().stream()
                .map(SupportTicketReceiverConfiguration::getReceiverEmail)
                .collect(Collectors.toList());
    }
}
