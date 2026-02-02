package com.scnsoft.eldermark.therap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.scnsoft.eldermark.therap.bean.report.OrganizationReport;
import com.scnsoft.eldermark.therap.bean.report.Report;
import com.scnsoft.eldermark.therap.bean.summary.*;
import com.scnsoft.eldermark.therap.bean.summary.event.TherapEventFileProcessingSummary;
import com.scnsoft.eldermark.therap.bean.summary.event.TherapEventRecordProcessingSummary;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final static ObjectReader reader;

    private static final String sourceDirPath = "C:\\exchange\\integrationsStore\\therap\\inboundSftp\\DemographicProfileExport";

    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        reader = objectMapper.readerFor(TherapTotalProcessingSummary.class);
    }

    public static void main(String[] args) throws IOException {

        Report report = new Report();

        fetchReportFiles()
                .peek(p -> System.out.println("Processing report file " + p.getFileName().toString()))
                .map(Main::toInputStream)
                .map(Main::read)
                .forEach(summary -> process(summary, report));

        System.out.println(objectMapper.writerFor(Report.class).writeValueAsString(report));


    }

    static Stream<Path> fetchReportFiles() throws IOException {
        return Files.find(
                Paths.get(sourceDirPath),
                3,
                (path, basicFileAttributes) -> path.getFileName().toString().contains(".report.txt"));
    }

    static InputStream toInputStream(Path path) {
        try {
            return Files.newInputStream(path, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static TherapTotalProcessingSummary read(InputStream is) {
        try {
            return reader.readValue(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void process(TherapTotalProcessingSummary summary, Report report) {
        report.setTotalFiles(report.getTotalFiles() + 1);
        processEvents(summary, report);
        System.out.println("Processed\n");

    }

    private static void processEvents(TherapTotalProcessingSummary summary, Report report) {
        if (summary.getEventsProcessingSummary() == null) {
            return;
        }
        Map<String, List<TherapEventFileProcessingSummary>> eventFilesByOrganization = splitForOrganizations(summary.getEventsProcessingSummary());
        eventFilesByOrganization.forEach(
                (organization, eventFilesSummary) -> {
                    if (!report.getOrganizations().containsKey(organization)) {
                        report.getOrganizations().put(organization, new OrganizationReport());
                    }
                    var organizationReport = report.getOrganizations().get(organization);
                    var eventReport = organizationReport.getEvents();
                    eventReport.setTotalFiles(eventReport.getTotalFiles() + eventFilesSummary.size());

                    eventFilesSummary.forEach(fileSummary -> {
                        System.out.println("Processing fileSummary " + fileSummary.getFileName());
                        var recordsProcessingSummary = CollectionUtils.emptyIfNull(fileSummary.getRecordsProcessingSummary());
                        eventReport.setTotalRecords(eventReport.getTotalRecords() + fileSummary.getTotalRecords());
                        eventReport.setProcessedRecords(eventReport.getProcessedRecords() + fileSummary.getProcessedRecords());


                        Map<ProcessingSummary.ProcessingStatus, Map<String, Long>> recordsByStatus = recordsProcessingSummary.stream()
                                .filter(r -> !ProcessingSummary.ProcessingStatus.OK.equals(r.getStatus()))
                                .collect(
                                        Collectors.groupingBy(TherapEventRecordProcessingSummary::getStatus,
                                                Collectors.groupingBy(r -> StringUtils.defaultString(r.getMessage()), Collectors.counting())));

                        recordsByStatus.forEach((status, countMap) -> eventReport.getStatistics().merge(status, countMap, (allCountsStatistics, newCounts) -> {
                                    newCounts.forEach((message, count) -> {
                                                allCountsStatistics.merge(message, count, (totalCount, newCount) -> totalCount + newCount);
                                            }
                                    );
                                    return allCountsStatistics;
                                }));
                    });
                }
        );
    }


    private static <R extends TherapEntityRecordProcessingSummary, F extends TherapEntityFileProcessingSummary<R>, E extends TherapEntitiesProcessingSummary<F>>
    Map<String, List<F>> splitForOrganizations(E entitySummary) {
        return CollectionUtils.emptyIfNull(entitySummary.getFilesProcessingSummary())
                .stream()
                .collect(Collectors.groupingBy(Main::extractOrganizationCode));
    }

    private static String extractOrganizationCode(TherapEntityFileProcessingSummary fs) {
        var name = fs.getFileName();
        var folders = name.split("/");
        return folders[1].substring(0, folders[1].indexOf('$'));
    }

}
