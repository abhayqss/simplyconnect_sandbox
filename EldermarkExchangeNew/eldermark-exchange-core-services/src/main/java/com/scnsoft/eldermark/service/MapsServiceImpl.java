package com.scnsoft.eldermark.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class MapsServiceImpl implements MapsService {

    private final static Logger logger = LoggerFactory.getLogger(MapsServiceImpl.class);
    public static final double EARTH_RADIUS_MILES = 3963.0d;

    private GeoApiContext context;

    @Value("${google.api.key}")
    String apiKey;


    @PostConstruct
    void initGoogleServices() {
        context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    @Override
    public Double calculateDistanceMiles(LatLng location1, LatLng location2) {
        double latDistance = Math.toRadians(location2.lat - location1.lat);
        double lonDistance = Math.toRadians(location2.lng - location1.lng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(location1.lat)) * Math.cos(Math.toRadians(location2.lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_MILES * c;
        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);


    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    @Override
    public LatLng getCoordinatesByAddress(String address) {
        if (StringUtils.isNotEmpty(address)) {
            try {
                GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
                if (results != null && results.length > 0) {
                    GeocodingResult geocodingResult = results[0];
                    return geocodingResult.geometry.location;
                }
            } catch (ApiException | IOException | InterruptedException e) {
                logger.warn("Unable to retrieve location data from Google services", e);
            }
            return null;
        }
        return null;
    }

    @Override
    public Double calculateDistance(LatLng location1, LatLng location2) {
        double theta = location1.lng - location2.lng;
        double dist = Math.sin(deg2rad(location1.lat)) * Math.sin(deg2rad(location2.lat))
                + Math.cos(deg2rad(location1.lat)) * Math.cos(deg2rad(location2.lat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
