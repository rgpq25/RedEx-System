import { useState, useRef, useEffect } from "react";
import useAnimation, { AnimationObject } from "./useAnimation";
import { Vuelo } from "@/lib/types";
import { getFlightPosition } from "@/lib/map-utils";

const useMapZoom = (
	initialZoom = 1,
	initialLongitude = 0,
	initialLatitude = 0
): {
	currentTime: Date;
	zoom: AnimationObject;
	centerLongitude: AnimationObject;
	centerLatitude: AnimationObject;
	zoomIn: (coordinates: [number, number]) => void;
	lockInFlight: (vuelo: Vuelo) => void;
	unlockFlight: () => void;
} => {
	const zoomFactor = 4;
	const [currentTime, setCurrentTime] = useState<Date>(new Date());
	const [currentlyLocked, setCurrentlyLocked] = useState<Vuelo | undefined>(undefined);

	const zoom = useAnimation(initialZoom);
	const centerLongitude = useAnimation(initialLongitude);
	const centerLatitude = useAnimation(initialLatitude);

	useEffect(() => {
		const startTime = new Date().getTime();
		const interval = setInterval(() => {
			const currentTime = new Date().getTime();
			const elapsedTime = (currentTime - startTime) * 500;
			setCurrentTime(new Date(startTime + elapsedTime));
		}, 1000);

		return () => {
			clearInterval(interval);
		};
	}, []);

	useEffect(() => {
		if (currentlyLocked) {
			const orgLongitude = currentlyLocked.planVuelo.ubicacionOrigen.longitud;
			const orgLatitude = currentlyLocked.planVuelo.ubicacionOrigen.latitud;
			const destLongitude = currentlyLocked.planVuelo.ubicacionDestino.longitud;
			const destLatitude = currentlyLocked.planVuelo.ubicacionDestino.latitud;

			const coordinates = getFlightPosition(
				currentlyLocked.fechaOrigen,
				[orgLongitude, orgLatitude] as [number, number],
				currentlyLocked.fechaDestino,
				[destLongitude, destLatitude] as [number, number],
				currentTime
			);
			zoom.setValueNoAnimation(zoomFactor);
			centerLongitude.setValueNoAnimation(coordinates[0]);
			centerLatitude.setValueNoAnimation(coordinates[1]);
		}
	}, [currentTime, currentlyLocked]);

	function zoomIn(coordinates: [number, number], duration = 1000) {
		zoom.setValue(zoomFactor, duration);
		centerLongitude.setValue(coordinates[0], duration);
		centerLatitude.setValue(coordinates[1], duration);
	}

	function zoomInNoAnimation(coordinates: [number, number]) {
		zoom.setValueNoAnimation(zoomFactor);
		centerLongitude.setValueNoAnimation(coordinates[0]);
		centerLatitude.setValueNoAnimation(coordinates[1]);
	}

	function lockInFlight(vuelo: Vuelo) {
		setCurrentlyLocked(undefined);
		const orgLongitude = vuelo.planVuelo.ubicacionOrigen.longitud;
		const orgLatitude = vuelo.planVuelo.ubicacionOrigen.latitud;
		const destLongitude = vuelo.planVuelo.ubicacionDestino.longitud;
		const destLatitude = vuelo.planVuelo.ubicacionDestino.latitud;

		const coordinates = getFlightPosition(
			vuelo.fechaOrigen,
			[orgLongitude, orgLatitude] as [number, number],
			vuelo.fechaDestino,
			[destLongitude, destLatitude] as [number, number],
			currentTime
		);
		zoomIn(coordinates);

		setTimeout(()=>{
			setCurrentlyLocked(vuelo);
		}, 1100);
	}

	function unlockFlight() {
		setCurrentlyLocked(undefined);
	}

	return {
		currentTime,
		zoom,
		centerLongitude,
		centerLatitude,
		zoomIn,
		lockInFlight,
		unlockFlight,
	};
};

export default useMapZoom;
