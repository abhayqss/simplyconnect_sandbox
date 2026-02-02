package com.scnsoft.eldermark.merger.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.merger.util.MergerUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import no.priv.garshol.duke.*;
import no.priv.garshol.duke.datasources.InMemoryDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class ClientMatcherServiceImpl implements ClientMatcherService {
    private static final Logger logger = LoggerFactory.getLogger(ClientMatcherServiceImpl.class);

    private static final int NUM_THREADS = 2;
    private static final int BATCH_SIZE = 40_000;

    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public MatchResult<Client> findMatchedPatients(Client targetClient, Supplier<Collection<Client>> sourceClients,
                                                   boolean respectHieConsentPolicy) {
        return findMatchedPatients(List.of(targetClient), sourceClients, respectHieConsentPolicy);
    }

    @Override
    public MatchResult<Client> findMatchedPatients(Collection<Client> targetClients, Supplier<Collection<Client>> sourceClients, boolean respectHieConsentPolicy) {
        try {
            var config = ConfigLoader.load("classpath:duke-config.xml");

            var processor = new Processor(config);
            processor.setThreads(NUM_THREADS);

            InMemoryDataSource newDataSource = new InMemoryDataSource();
            targetClients.stream()
                    .filter(client -> !respectHieConsentPolicy || client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_IN)
                    .map(this::createRecord)
                    .forEach(newDataSource::add);

            var newDataSources = new ArrayList<DataSource>();
            newDataSources.add(newDataSource);

            var clients = sourceClients.get();

            var actualDataSource = new InMemoryDataSource();
            clients.stream()
                    .filter(client -> !respectHieConsentPolicy || client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_IN)
                    .map(this::createRecord)
                    .forEach(actualDataSource::add);

            var matchListener = new ClientMatchListener<>(r -> {
                var id = Long.parseLong(r.getValue("id"));

                return Stream.concat(clients.stream(), targetClients.stream())
                        .filter(client -> Objects.equals(client.getId(), id))
                        .findFirst()
                        .orElseThrow();
            });
            processor.addMatchListener(matchListener);

            processor.setLogger(loggerWrapper(logger));

            processor.link(newDataSources, List.of(actualDataSource), BATCH_SIZE);

            processor.close();

            return new MatchResult<>(
                    matchListener.getMatchedRecordsEntries(),
                    matchListener.getProbablyMatchedRecordsEntries()
            );
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private Record createRecord(Client client) {
        RecordImpl record = new RecordImpl();

        record.addValue(
                "id",
                client.getId() != null ? client.getId().toString() : UUID.randomUUID().toString()
        );
        record.addValue("firstName", MergerUtils.normalizeString(client.getFirstName()));
        record.addValue("middleName", MergerUtils.normalizeString(client.getMiddleName()));
        record.addValue("lastName", MergerUtils.normalizeString(client.getLastName()));
        if (client.getBirthDate() != null) {
            record.addValue("birthDate", DATE_FORMAT.format(client.getBirthDate()));
        }
        if (client.getSocialSecurity() != null) {
            record.addValue("ssn", MergerUtils.normalizeSsn(client.getSocialSecurity()));
        }
        if (client.getGender() != null) {
            record.addValue("gender", MergerUtils.normalizeString(client.getGender().getDisplayName()));
        }

        Optional.ofNullable(client.getPerson())
                .map(Person::getAddresses)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .ifPresent(address -> {
                    record.addValue("addrStreet", MergerUtils.normalizeStreet(address.getStreetAddress()));
                    record.addValue("addrCity", MergerUtils.normalizeString(address.getCity()));
                    record.addValue("addrState", MergerUtils.normalizeString(address.getState()));
                    record.addValue("addrZip", MergerUtils.normalizeString(address.getPostalCode()));
                });

        PersonTelecomUtils.find(client.getPerson(), PersonTelecomCode.EMAIL)
                .map(PersonTelecom::getNormalized)
                .ifPresent(email -> record.addValue("email", email));

        PersonTelecomUtils.find(client.getPerson(), PersonTelecomCode.MC)
                .map(PersonTelecom::getNormalized)
                .ifPresent(email -> record.addValue("phone", email));

        return record;
    }

    private no.priv.garshol.duke.Logger loggerWrapper(Logger logger) {
        return new no.priv.garshol.duke.Logger() {
            @Override
            public void trace(String s) {
                logger.trace(s);
            }

            @Override
            public boolean isTraceEnabled() {
                return logger.isTraceEnabled();
            }

            @Override
            public void debug(String s) {
                logger.debug(s);
            }

            @Override
            public boolean isDebugEnabled() {
                return logger.isDebugEnabled();
            }

            @Override
            public void info(String s) {
                logger.info(s);
            }

            @Override
            public boolean isInfoEnabled() {
                return logger.isInfoEnabled();
            }

            @Override
            public void warn(String s) {
                logger.warn(s);
            }

            @Override
            public void warn(String s, Throwable throwable) {
                logger.warn(s, throwable);
            }

            @Override
            public boolean isWarnEnabled() {
                return logger.isWarnEnabled();
            }

            @Override
            public void error(String s) {
                logger.error(s);
            }

            @Override
            public void error(String s, Throwable throwable) {
                logger.error(s, throwable);
            }

            @Override
            public boolean isErrorEnabled() {
                return logger.isErrorEnabled();
            }
        };
    }
}

