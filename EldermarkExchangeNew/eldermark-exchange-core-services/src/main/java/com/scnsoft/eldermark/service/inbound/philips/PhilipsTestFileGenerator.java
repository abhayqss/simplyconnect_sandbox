package com.scnsoft.eldermark.service.inbound.philips;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.scnsoft.eldermark.dto.PhilipsTestDto;
import com.scnsoft.eldermark.entity.inbound.philips.PhilipsEventCSV;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class PhilipsTestFileGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PhilipsTestFileGenerator.class);
    private static final DateTimeFormatter FILE_NAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy_HHmmss").withZone(ZoneId.of("UTC"));


    @Value("${philips.sftp.localStorage.base}")
    private String localStorageBaseDirPath;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "File paths don't depend on user input")
    public boolean generateCsvFile(List<PhilipsTestDto> dtos) {

        var filePath = Paths.get(localStorageBaseDirPath + "/" + generateFileName());
        try (Writer writer = Files.newBufferedWriter(filePath)) {

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            List<PhilipsEventCSV> myUsers = generateCsvEvents(dtos);
            if (myUsers.size() == 0) {
                return false;
            }
            beanToCsv.write(myUsers);
            var csvBytes = Files.readAllBytes(filePath);
            Files.write(filePath, documentEncryptionService.encrypt(csvBytes));
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            logger.warn("Error during generating test data for PhilipsEventCSV");
            return false;
        }
        return true;
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "File paths don't depend on user input")
    public boolean generateCsvFile(String csv) {
        try {
            var filePath = Paths.get(localStorageBaseDirPath + "/" + generateFileName());
            Files.copy(new ByteArrayInputStream(documentEncryptionService.encrypt(csv.getBytes(StandardCharsets.UTF_8))), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.warn("Error during pushing csv content to file");
            return false;
        }
        return true;
    }

    private String generateFileName() {
        return "test_" + FILE_NAME_DATE_TIME_FORMATTER.format(Instant.now()) + ".csv";
    }

    private List<PhilipsEventCSV> generateCsvEvents(List<PhilipsTestDto> dtos) {
        return dtos.stream().map(dto -> {
            var client = clientService.findById(dto.getClientId());
            var csvEvent = new PhilipsEventCSV();
            csvEvent.setMrn(String.valueOf(client.getId()));
            csvEvent.setCreatedDate(LocalDateTime.now());
            csvEvent.setFirstName(client.getFirstName());
            csvEvent.setLastName(client.getLastName());
            csvEvent.setInServiceDays(dto.getOutcome());
            csvEvent.setProgramCode(dto.getProgramCode());
            csvEvent.setSituation(dto.getSituation());
            csvEvent.setSubNid(dto.getSubNid());
            return csvEvent;
        }).collect(Collectors.toList());
    }
}
