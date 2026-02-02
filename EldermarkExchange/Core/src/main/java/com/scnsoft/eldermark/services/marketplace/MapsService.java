package com.scnsoft.eldermark.services.marketplace;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.scnsoft.eldermark.shared.marketplace.AutoCompletedAddressInfoDto;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.lang.Math.*;

@Service
public class MapsService {

    public static final double EARTH_RADIUS_MILES = 3963.0d;

    Logger logger = Logger.getLogger(MapsService.class.getName());

    private GeoApiContext context;

    @Value("${google.api.key}")
    String apiKey;

    private static final Map<String, String> autoCompleteResponseTypes = new HashMap<String, String>() {
        {
            put("administrative_area_level_1", "STATE");
            put("locality", "CITY");
            put("postal_code", "ZIP_CODE");
        }
    };

    @PostConstruct
    void initGoogleServices() {
        context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    public LatLng getCoordinatesByAddress(String address) {
        if (StringUtils.isNotEmpty(address)) {
            try {
                GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
                if (results != null && results.length > 0) {
                    GeocodingResult geocodingResult = results[0];
                    return geocodingResult.geometry.location;
                }
            } catch (ApiException e) {
                logger.warning("Unable to retrieve location data from Google services");
                e.printStackTrace();
            } catch (InterruptedException e) {
                logger.warning("Unable to retrieve location data from Google services");
                e.printStackTrace();
            } catch (IOException e) {
                logger.warning("Unable to retrieve location data from Google services");
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    public List<AutoCompletedAddressInfoDto> autoCompleteInUs(String textCityZip) {
        List<AutoCompletedAddressInfoDto> autoCompletedAddressInfoDtoList = null;
        try {
            PlaceAutocompleteRequest placeAutocompleteRequest = PlacesApi.placeAutocomplete(context, textCityZip);
            placeAutocompleteRequest.types(PlaceAutocompleteType.REGIONS);
            placeAutocompleteRequest.components(ComponentFilter.country("US"));
            placeAutocompleteRequest.language("en");
            AutocompletePrediction[] results = placeAutocompleteRequest.await();
            if (results != null && results.length > 0) {
                autoCompletedAddressInfoDtoList = new ArrayList<AutoCompletedAddressInfoDto>();
                for (AutocompletePrediction result : results) {
                    AutoCompletedAddressInfoDto autoCompletedAddressInfoDto = new AutoCompletedAddressInfoDto();
                    autoCompletedAddressInfoDto.setValue(result.description);
                    if (result.types != null && result.types.length > 0) {
                        for (String type : result.types) {
                            if (autoCompleteResponseTypes.containsKey(type)) {
                                autoCompletedAddressInfoDto.setLocationType(autoCompleteResponseTypes.get(type));
                            }
                        }
                    }
                    if (autoCompletedAddressInfoDto.getLocationType() != null) {
                        autoCompletedAddressInfoDtoList.add(autoCompletedAddressInfoDto);
                    }
                }
            }
        } catch (ApiException e) {
            logger.warning("Unable to retrieve location data from Google services");
            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.warning("Unable to retrieve location data from Google services");
            e.printStackTrace();
        } catch (IOException e) {
            logger.warning("Unable to retrieve location data from Google services");
            e.printStackTrace();
        }
        return autoCompletedAddressInfoDtoList;
    }

    public Double calculateDistance(LatLng location1, LatLng location2) {
        double theta = location1.lng - location2.lng;
        double dist = Math.sin(deg2rad(location1.lat)) * Math.sin(deg2rad(location2.lat))
                + Math.cos(deg2rad(location1.lat)) * Math.cos(deg2rad(location2.lat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    /**
     * calculate distance between two points on Earth
     * 
     * @param location1
     * @param location2
     * @return distance in miles
     * @see https://en.wikipedia.org/wiki/Great-circle_distance
     *
     */
    public Double calculateDistanceMiles(LatLng location1, LatLng location2) {
        final double longitudeRadiansFirst = deg2rad(location1.lng);
        final double latitudeRadiansFirst = deg2rad(location1.lat);
        final double longitudeRadiansSecond = deg2rad(location2.lng);
        final double latitudeRadiansSecond = deg2rad(location2.lat);

        final double diffLongitude = Math.abs(longitudeRadiansFirst - longitudeRadiansSecond);

        final double result = EARTH_RADIUS_MILES * acos(sin(latitudeRadiansFirst) * sin(latitudeRadiansSecond)
                + cos(latitudeRadiansFirst) * cos(latitudeRadiansSecond) * cos(diffLongitude));
        return result;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
