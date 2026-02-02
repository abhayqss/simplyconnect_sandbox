package com.scnsoft.eldermark.therap.service;

import com.scnsoft.eldermark.therap.bean.*;
import com.scnsoft.eldermark.therap.entity.ResidentMapping;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class TherapServiceImpl implements TherapService {

    private static final Logger logger = LoggerFactory.getLogger(TherapServiceImpl.class);

    @Value("${directory.source}")
    private String sourceDirectory;

    @Value("${directory.target}")
    private String targetDirectory;

    @Value("${directory.error}")
    private String errorDirectory;

    private final ResidentMappingService residentMappingService;

    public TherapServiceImpl(ResidentMappingService residentMappingService) {
        this.residentMappingService = residentMappingService;
    }

    @Override
    public void processAvailableFiles() throws IOException {
        DirectoryStream<Path> sourceFiles = Files.newDirectoryStream(Paths.get(sourceDirectory));
        for (Path sourceFile : sourceFiles) {
            try {
                logger.info("Processing file, {}", sourceFile.getFileName().toString());
                processFile(sourceFile);
                logger.info("{} processed succesfully", sourceFile.getFileName().toString());
            } catch (Exception ex) {
                logger.warn("{} processed with errors", sourceFile.getFileName().toString(), ex);
                processError(sourceFile, ex);
            }
        }
    }


    private void processFile(Path sourceFile) throws IOException {
        if (Files.isDirectory(sourceFile)) {
            return;
        }
        Path targetFile = Paths.get(targetDirectory, sourceFile.getFileName().toString());
        targetFile = Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        final FileSystem zipFs = FileSystems.newFileSystem(targetFile, null);
        Path zipRoot = zipFs.getPath("/");

        try {
            processFilesOfType(zipRoot, "GER_EVENT", TherapEvent::new);
            processFilesOfType(zipRoot, "IDF_DETAIL", TherapIdf::new);
            processFilesOfType(zipRoot, "INDIVIDUAL_ADVANCE_DIRECTIVE", TherapAdvanceDirective::new);
            processFilesOfType(zipRoot, "MEDICATION_HISTORY", TherapMedication::new);
            processFilesOfType(zipRoot, "INDIVIDUAL_DIAGNOSIS", TherapDiagnosis::new);
        } finally {
            zipFs.close();
        }

    }

    private void processFilesOfType(Path targetFile, String filename, BiFunction<Set<String>, CSVRecord, TherapRecord> recordConstructor) throws IOException {
        var eventFiles = Files.find(targetFile, 10, (path, basicFileAttributes) -> path.toString().contains(filename));
        eventFiles.forEach(eventFile -> {
            try {

                final CSVParser csvParser = new CSVParser(new InputStreamReader(Files.newInputStream(eventFile)), CSVFormat.DEFAULT
                        .withFirstRecordAsHeader());

                // relies on the fact that header map in csv parser is LinkedMap - keeps order
                final Set<String> headers = csvParser.getHeaderMap().keySet();

//                System.out.println(StringUtils.join(headers, ", "));

                var events = csvParser.getRecords()
                        .stream()
                        .map(record -> recordConstructor.apply(headers, record))
                        .peek(r -> r.setFilename(eventFile.toString()))
                        .peek(this::processRecord)
                        .collect(Collectors.toList());

                writeRecords(eventFile, events);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    private <T extends TherapRecord> void writeRecords(Path eventFile, List<T> records) throws IOException {
        if (records.isEmpty()) {
            return;
        }

        var faos = Files.newOutputStream(eventFile, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        var writer = new OutputStreamWriter(faos);

        var csvPrinter = new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader(records.get(0).getHeaders().toArray(new String[0])));
        csvPrinter.printRecords(records.stream().map(TherapRecord::valuesAsStringArray).collect(Collectors.toList()));
        csvPrinter.close();

        writer.close();
        faos.close();
    }


    private void processRecord(TherapRecord therapRecord) {
        var mapping = residentMappingService.findAndUpdateMapping(therapRecord);
        if (mapping == null) {
            mapping = residentMappingService.generateAndCreateNewMapping(therapRecord);
        }
        updateTherapRecord(therapRecord, mapping);
    }

    void updateTherapRecord(TherapRecord therapRecord, ResidentMapping residentMapping) {
        therapRecord.setFirstName(residentMapping.getNewFirstName());
        therapRecord.setLastName(residentMapping.getNewLastName());

        if (StringUtils.isNotEmpty(therapRecord.getPatientId())) {
            therapRecord.setPatientId(residentMapping.getNewPatientId());
        }

        if (StringUtils.isNotEmpty(therapRecord.getSSN())) {
            therapRecord.setSSN(residentMapping.getNewSsn());
        }

        if (StringUtils.isNotEmpty(therapRecord.getDateOfBirth())) {
            therapRecord.setDateOfBirth(residentMapping.getNewDateOfBirth());
        }
    }

    private void processError(Path sourceFile, Exception ex) throws IOException {
        var errorLog = Paths.get(errorDirectory, sourceFile.getFileName().toString() + ".error.txt");
        Files.writeString(errorLog, ExceptionUtils.getStackTrace(ex));

        Files.deleteIfExists(Paths.get(targetDirectory, sourceFile.getFileName().toString()));
        Files.copy(sourceFile, Paths.get(errorDirectory, sourceFile.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);

    }
}
