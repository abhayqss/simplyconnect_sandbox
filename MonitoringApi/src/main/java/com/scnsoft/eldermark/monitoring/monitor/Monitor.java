package com.scnsoft.eldermark.monitoring.monitor;

import com.scnsoft.eldermark.monitoring.dto.ItemType;
import com.scnsoft.eldermark.monitoring.dto.MonitorItem;
import com.scnsoft.eldermark.monitoring.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

@Component
public class Monitor {

    private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

    @Value("${monitoring.root.urls}")
    private List<String> monitoringUrls;

    @Value("${monitoring.endpoint.urls}")
    private List<String> monitoringEndpointUrls;

    @Value("${monitoring.service.names}")
    private List<String> monitoringServiceNames;

    @Value("${monitoring.disks}")
    private List<String> monitoringDisks;

    @Value("${monitoring.max.fail.attempts}")
    private int maxFailAttempts;

    @Value("${monitoring.emails.path}")
    private String emailsConfigPath;

    @Value("${spring.profiles.active}")
    private String springProfile;

    @Value("${monitoring.memory.disk.space}")
    private String triggeredUsedMemorySpacePercentage;

    private final Map<String, Integer> failAttemptCounter = new HashMap<>();

    private final Map<String, Map<String, Boolean>> itemStatusesByEmails = new HashMap<>();

    private final RestTemplate restTemplate;

    private final Converter<List<MonitorItem>, String> messageConverter;

    private final MailService mailService;

    private List<String> emails;

    @Autowired
    public Monitor(RestTemplate restTemplate, Converter<List<MonitorItem>, String> messageConverter, MailService mailService) {
        this.restTemplate = restTemplate;
        this.messageConverter = messageConverter;
        this.mailService = mailService;
    }

    @PostConstruct
    public void initEmails() {
        try (FileInputStream fileInputStream = new FileInputStream(emailsConfigPath);) {
            emails = Arrays.asList(new String(fileInputStream.readAllBytes()).split(","));
        } catch (IOException e) {
            logger.error("Unable to load emails config");
        }
        emails.forEach(email -> mailService.send(email, "The Monitoring Api has been started: " + springProfile));
    }

    @Scheduled(fixedRateString = "${monitoring.schedule.period}")
    public void monitor() {
        var monitorItems = Stream.of(monitorServices(), monitorDiskSpaces())
            .flatMap(stream -> stream)
            .collect(Collectors.toList());
        sendEmails(monitorItems);
    }

    private Stream<MonitorItem> monitorDiskSpaces() {
        return monitoringDisks.stream()
                .map(diskName -> {
                    var fileOptional = Arrays.stream(File.listRoots())
                            .filter(file -> file.getPath().equals(diskName))
                            .findAny();
                    if (fileOptional.isPresent()) {
                        return fileOptional.get();
                    } else {
                        logger.error("Disk {} is skipped from monitoring", diskName);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(this::monitorDiskSpace);

    }

    private MonitorItem monitorDiskSpace(File drive) {
        logger.info("Drive {} is monitoring", drive);
        var totalSpace = drive.getTotalSpace();
        var spaceUsage = totalSpace - drive.getFreeSpace();
        var spaceUsagePercentage = round((double) spaceUsage / totalSpace, 2);
        var format = String.format("%s is using %.0f%% of memory", drive, spaceUsagePercentage * 100);
        var monitorItem = new MonitorItem(format, ItemType.DISK_SPACE, true);
        if (spaceUsagePercentage >= Double.parseDouble(triggeredUsedMemorySpacePercentage)) {
            monitorItem.setAvailable(false);
        }
        logChangedStatus(monitorItem);
        updateAttemptCounter(monitorItem);
        return monitorItem;
    }

    private Stream<MonitorItem> monitorEndpoints() {
        return createMonitoringEndpoints()
            .map(this::monitorEndpoint);
    }

    private MonitorItem monitorEndpoint(String endpointUrl) {
        var isAvailable = false;
        try {
            var responseEntity = restTemplate.getForEntity(endpointUrl, String.class);
            isAvailable = HttpStatus.OK == responseEntity.getStatusCode();
        } catch (RestClientException e) {
            logger.error("During calling endpoint {} there is exception: {}", endpointUrl, e.getLocalizedMessage());
        }
        var monitorItemStatus = new MonitorItem(endpointUrl, ItemType.ENDPOINT, isAvailable);
        logChangedStatus(monitorItemStatus);
        updateAttemptCounter(monitorItemStatus);
        return monitorItemStatus;
    }

    private Stream<MonitorItem> monitorServices() {
        return monitoringServiceNames.stream()
            .map(this::monitorService);
    }

    private MonitorItem monitorService(String serviceName) {
        var isAvailable = false;
        var isServiceExist = false;
        var isServiceRunning = false;
        var wrappedServiceName = serviceName.contains(" ") ? "\"" + serviceName + "\"" : serviceName;
        try (var reader = new Scanner(Runtime.getRuntime().exec("sc query " + wrappedServiceName).getInputStream(), StandardCharsets.UTF_8)) {
            while (reader.hasNextLine()) {
                var line = reader.nextLine();
                if (line.contains(serviceName)) {
                    isServiceExist = true;
                }
                if (line.contains("RUNNING")) {
                    isServiceRunning = true;
                }
                if (isServiceExist && isServiceRunning) {
                    isAvailable = true;
                    break;
                }
            }
        } catch (
            IOException e) {
            logger.error("During checking service {} there is exception: {}", serviceName, e.getLocalizedMessage());
        }

        var monitorItemStatus = new MonitorItem(serviceName, ItemType.SERVICE, isAvailable);
        logChangedStatus(monitorItemStatus);
        updateAttemptCounter(monitorItemStatus);
        return monitorItemStatus;
    }

    private void logChangedStatus(MonitorItem monitorItemStatus) {
        var failCont = failAttemptCounter.getOrDefault(monitorItemStatus.getName(), 0);
        if (!monitorItemStatus.isAvailable() && failCont == 0) {
            logger.info("{} is not available", monitorItemStatus.getType().getName());
        }
        if (monitorItemStatus.isAvailable() && failCont != 0) {
            logger.info("{} has recovered", monitorItemStatus.getType().getName());
        }
    }

    private void updateAttemptCounter(MonitorItem monitorItemStatus) {
        if (monitorItemStatus.isAvailable()) {
            failAttemptCounter.put(monitorItemStatus.getName(), 0);
        } else {
            failAttemptCounter.put(monitorItemStatus.getName(), failAttemptCounter.getOrDefault(monitorItemStatus.getName(), 0) + 1);
        }
    }

    private void sendEmails(List<MonitorItem> monitorItems) {
        emails.forEach(email -> sendEmail(email, monitorItems));
    }

    private void sendEmail(String email, List<MonitorItem> monitorItems) {
        var filteredMonitorItems = filterMonitorItemsForSending(email, monitorItems);
        if (CollectionUtils.isEmpty(filteredMonitorItems)) {
            return;
        }
        var message = "Environment: " + springProfile + "\n" + messageConverter.convert(filteredMonitorItems);
        mailService.send(email, message);
        itemStatusesByEmails.put(email, filteredMonitorItems.stream().collect(Collectors.toMap(MonitorItem::getName, MonitorItem::isAvailable)));
    }

    private List<MonitorItem> filterMonitorItemsForSending(String email, List<MonitorItem> monitorItems) {
        var itemStatuses = itemStatusesByEmails.getOrDefault(email, new HashMap<>());
        return monitorItems.stream()
            .filter(monitorItem -> isNeedMonitorItemSend(itemStatuses, monitorItem))
            .collect(Collectors.toList());
    }

    private boolean isNeedMonitorItemSend(Map<String, Boolean> itemStatuses, MonitorItem monitorItem) {
        if (!monitorItem.isAvailable() && failAttemptCounter.getOrDefault(monitorItem.getName(), 0) < maxFailAttempts) {
            return false;
        }
        var isItemAvailable = itemStatuses.get(monitorItem.getName());
        if (!monitorItem.isAvailable() && isItemAvailable == null) {
            return true;
        }
        return isItemAvailable != null && isItemAvailable != monitorItem.isAvailable();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        var bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Stream<String> createMonitoringEndpoints() {
        return monitoringUrls.stream().flatMap(url -> monitoringEndpointUrls.stream().map(endpoint -> url + endpoint));
    }
}
