package com.scnsoft.eldermark.service.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import com.scnsoft.eldermark.dao.MarketplaceRatingDao;
import com.scnsoft.eldermark.dao.MarketplaceRatingUpdateDao;
import com.scnsoft.eldermark.entity.MarketplaceRating;
import com.scnsoft.eldermark.entity.MarketplaceRatingUpdate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "marketplace.rating.scheduled.update.enabled", havingValue = "true")
public class ScheduledMarketplaceRatingUpdateServiceImpl implements ScheduledMarketplaceRatingUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledMarketplaceRatingUpdateServiceImpl.class);

    private static final String CSV_TYPE = "text/csv";
    private static final int MIN_RATING = 0;
    private static final int MAX_RATING = 5;

    @Value("${marketplace.rating.dataset.url}")
    private String datasetUrl;

    @Autowired
    private MarketplaceRatingDao marketplaceRatingDao;

    @Autowired
    private MarketplaceRatingUpdateDao marketplaceRatingUpdateDao;

    @Autowired
    @Qualifier("jsonRestTemplateBuilder")
    private RestTemplateBuilder restTemplateBuilder;

    @Override
    @Scheduled(cron = "${marketplace.rating.update.cron}")
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void checkAndUpdate() {
        logger.info("Check of marketplace rating is started");
        var responseEntity = restTemplateBuilder.build().getForEntity(datasetUrl, MarketplaceRatingDataset.class);
        var dataset = responseEntity.getBody();
        if (isDatasetNotValid(dataset)) {
            return;
        }
        var optUpdate = marketplaceRatingUpdateDao.findAll().stream()
                .filter(u -> !u.getModifiedDate().isBefore(Objects.requireNonNull(dataset).modifiedDate))
                .findFirst();
        if (optUpdate.isPresent()) {
            logger.info("Update for marketplace rating no need");
            return;
        }
        logger.info("Update of marketplace rating is started");
        var ratings = getCsvRatings(Objects.requireNonNull(dataset).distributions.get(0).downloadURL).stream()
                .filter(r -> StringUtils.isNoneEmpty(r.federalProviderNumber, r.providerName, r.overallRating, r.processingDate))
                .map(r -> {
                    try {
                        return new MarketplaceRating(r.federalProviderNumber, r.providerName, Integer.parseInt(r.overallRating), LocalDate.parse(r.processingDate), false);
                    } catch (Exception e) {
                        logger.error("Exception during marketplace rating converting: {}", ExceptionUtils.getStackTrace(e));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(r -> r.getOverallRating() >= MIN_RATING && r.getOverallRating() <= MAX_RATING)
                .collect(Collectors.toList());
        marketplaceRatingDao.deleteAllByIsManualIs(false);
        marketplaceRatingDao.saveAll(ratings);
        marketplaceRatingUpdateDao.deleteAll();
        marketplaceRatingUpdateDao.save(new MarketplaceRatingUpdate(dataset.modifiedDate));
        logger.info("Update of marketplace rating is finished");
    }

    private boolean isDatasetNotValid(MarketplaceRatingDataset dataset) {
        if (dataset == null) {
            logger.error("Dataset for marketplace rating updating is null");
            return true;
        }
        if (dataset.modifiedDate == null) {
            logger.error("Modified date of dataset for marketplace rating updating is null");
            return true;
        }
        if (CollectionUtils.isEmpty(dataset.distributions)) {
            logger.error("Distributions of dataset for marketplace rating updating is empty");
            return true;
        }
        var mediaType = dataset.distributions.get(0).mediaType;
        if (!CSV_TYPE.equals(mediaType)) {
            logger.error("Media type of dataset for marketplace rating updating is incorrect: {}", mediaType);
            return true;
        }
        if (StringUtils.isEmpty(dataset.distributions.get(0).downloadURL)) {
            logger.error("Download URL of dataset for marketplace rating updating is empty");
            return true;
        }
        return false;
    }

    private List<MarketplaceRatingCsv> getCsvRatings(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.execute(url, HttpMethod.GET, null, clientHttpResponse -> {
            var rfc4180Parser = new RFC4180ParserBuilder()
                    .build();
            var reader = new CSVReaderBuilder(new InputStreamReader(clientHttpResponse.getBody()))
                    .withCSVParser(rfc4180Parser)
                    .build();
            return new CsvToBeanBuilder<MarketplaceRatingCsv>(reader)
                    .withType(MarketplaceRatingCsv.class)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();
        });
    }

    public static class MarketplaceRatingDataset {
        @JsonProperty("modified")
        private LocalDate modifiedDate;
        @JsonProperty("distribution")
        private List<MarketplaceRatingDistribution> distributions;
    }

    public static class MarketplaceRatingDistribution {
        @JsonProperty("mediaType")
        private String mediaType;
        @JsonProperty("downloadURL")
        private String downloadURL;
    }

    public static class MarketplaceRatingCsv {
        @CsvBindByName(column = "Federal Provider Number")
        private String federalProviderNumber;
        @CsvBindByName(column = "Provider Name")
        private String providerName;
        @CsvBindByName(column = "Overall Rating")
        private String overallRating;
        @CsvBindByName(column = "Processing Date")
        private String processingDate;
    }
}
