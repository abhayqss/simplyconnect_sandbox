import React, {
	memo,
	useRef,
	useMemo,
	useState,
	useEffect,
	useCallback
} from "react"

import cn from 'classnames'
import PTypes from 'prop-types'

import {
	useGeolocated
} from 'react-geolocated'

import {
	map,
	find,
	isEqual
} from 'underscore'

import {
	compose,
	withProps
} from 'recompose'

import {
	Marker,
	GoogleMap,
	withScriptjs,
	withGoogleMap
} from 'react-google-maps'

import config from 'config'

import './Map.scss'

import InfoWindow from 'react-google-maps/lib/components/InfoWindow'
import MarkerWithLabel from 'react-google-maps/lib/components/addons/MarkerWithLabel'

import {
	COORDINATE
} from 'lib/Constants'

import {
	isNotEmpty
} from 'lib/utils/Utils'

import {
	noop
} from 'lib/utils/FuncUtils'

import {
	isValidCoordinate,
	isEqualCoordinates
} from 'lib/utils/GeoUtils'

import Loader from '../Loader/Loader'
import MapControl from '../MapControl/MapControl'

import rippleIcon from 'images/ripple.svg'
import indicatorIcon from 'images/gps-indicator.png'
import blueMarkerIcon from 'images/map-marker-blue.svg'
import lightBlueMarkerIcon from 'images/map-marker-light-blue.svg'

const GOOGLE_MAP_URL = `https://maps.googleapis.com/maps/api/js?key=${config.google.maps.apiKey}&v=3.exp&libraries=geometry,drawing,places`

const {
	DEFAULT_LATITUDE_DELTA,
	DEFAULT_LONGITUDE_DELTA
} = COORDINATE

const DEFAULT_REGION = {
	lat: DEFAULT_LATITUDE_DELTA,
	lng: DEFAULT_LONGITUDE_DELTA
}

function GoogleMapCoordinate(latitude, longitude) {
	return new window.google.maps.LatLng(latitude, longitude)
}

const MapMarker = memo(function MapMarker(
	{
		data,
		coordinate,
		isSelected,
		onClick,
		children
	}
) {

	const _onClick = useCallback(() => {
		onClick({
			data,
			coordinate,
			isSelected
		})
	}, [
		data,
		onClick,
		coordinate,
		isSelected
	])

	return (
		<MarkerWithLabel
			position={coordinate}
			labelClass="Map-MarkerLabel"
			onClick={_onClick}
			labelAnchor={new window.google.maps.Point(10, 35)}
			icon={{ url: isSelected ? lightBlueMarkerIcon : blueMarkerIcon }}
		>
			{children}
		</MarkerWithLabel>
	)
})

function coordinateToString(coordinate) {
	return `${coordinate.lat}:${coordinate.lng}`
}

function Map(
	{
		markers,
		defaultZoom,
		defaultRegion,
		renderMarkerPopup,
		className
	}
) {
	const mapRef = useRef()

	const [selectedMarker, setSelectedMarker] = useState(null)

	const {
		coords,
		isGeolocationEnabled,
		isGeolocationAvailable
	} = useGeolocated()

	const region = useMemo(() => {
		const coordinate = {
			lat: coords?.latitude,
			lng: coords?.longitude
		}

		if (
			isGeolocationEnabled
			&& isGeolocationAvailable
			&& isValidCoordinate(coordinate)
		) return coordinate

		return isValidCoordinate(defaultRegion) ?
			defaultRegion : DEFAULT_REGION
	}, [
		coords,
		defaultRegion,
		isGeolocationEnabled,
		isGeolocationAvailable
	])

	const onClick = useCallback(e => {
		if (!(e.target && (e.target.src || '').includes('marker'))) {
			setSelectedMarker(null)
		}
	}, [])

	const onClickMarker = useCallback(o => {
		const marker = find(markers, m => isEqual(
			m.coordinate, o.coordinate
		))

		setSelectedMarker(marker)
	}, [markers])

	const moveToCurrentLocation = useCallback(() => {
		mapRef.current.panTo(GoogleMapCoordinate(region.lat, region.lng))
	}, [region])

	useEffect(() => {
		window.addEventListener('click', onClick)
		return () => window.removeEventListener('click', onClick)
	}, [onClick])

	return (
		<div className={cn('Map', className)}>
			<GoogleMap
				ref={mapRef}
				style={{ height: 600 }}
				defaultZoom={defaultZoom}
				defaultCenter={region}
				onClick={onClick}>
				{mapRef.current && (
					<MapControl
						position={window.google.maps.ControlPosition.RIGHT_BOTTOM}>
						<div className="Map-CurrentLocationBtn" onClick={moveToCurrentLocation}>
							<img src={indicatorIcon}/>
						</div>
					</MapControl>
				)}
				<Marker
					icon={{ url: rippleIcon }}
					position={GoogleMapCoordinate(region.lat, region.lng)}
				/>
				{renderMarkerPopup && selectedMarker && (
					<InfoWindow
						position={GoogleMapCoordinate(
							selectedMarker.coordinate.lat,
							selectedMarker.coordinate.lng
						)}
					>
						<div>
							{renderMarkerPopup(selectedMarker)}
						</div>
					</InfoWindow>
				)}
				{isNotEmpty(markers) && map(markers, (marker, i) => {
					if (!isValidCoordinate(marker.coordinate)) return

					const isSelected = marker.isSelected || (
						selectedMarker && isEqualCoordinates(
							selectedMarker.coordinate, marker.coordinate
						)
					)

					return (
						<MapMarker
							isSelected={isSelected}
							coordinate={marker.coordinate}
							onClick={onClickMarker}
							data={marker.data}
							key={coordinateToString(marker.coordinate)}
						>
							<div className="Map-MarkerLabelText">{marker.label ?? ''}</div>
						</MapMarker>
					)
				})}
			</GoogleMap>
		</div>
	)
}


Map.propTypes = {
	markers: PTypes.arrayOf(PTypes.shape({
		label: PTypes.string,
		isSelected: PTypes.bool,
		coordinate: PTypes.shape({
			lat: PTypes.oneOfType([PTypes.string, PTypes.number]),
			lng: PTypes.oneOfType([PTypes.string, PTypes.number])
		})
	})),
	defaultZoom: PTypes.number,
	defaultRegion: PTypes.shape({
		lat: PTypes.oneOfType([PTypes.string, PTypes.number]),
		lng: PTypes.oneOfType([PTypes.string, PTypes.number])
	}),

	onClickMarker: PTypes.func,
	renderMarkerPopup: PTypes.func
}

Map.defaultProps = {
	markers: [],
	defaultZoom: 4,
	defaultRegion: DEFAULT_REGION,
	onClickMarker: noop
}

export default compose(
	withProps({
		googleMapURL: GOOGLE_MAP_URL,
		loadingElement: (
			<Loader/>
		),
		containerElement: (
			<div style={{ height: '100%' }}/>
		),
		mapElement: (
			<div style={{ height: '100%' }}/>
		)
	}),
	withScriptjs,
	withGoogleMap
)(Map)