package com.scnsoft.eldermark.service;

import com.google.maps.model.LatLng;

public interface MapsService {

	 Double calculateDistanceMiles(LatLng location1, LatLng location2) ;
	 
	 LatLng getCoordinatesByAddress(String address);
	 
	 Double calculateDistance(LatLng location1, LatLng location2);
}
