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
			const orgLongitude = currentlyLocked.plan_vuelo.ubicacion_origen.coordenadas.longitud;
			const orgLatitude = currentlyLocked.plan_vuelo.ubicacion_origen.coordenadas.latitud;
			const destLongitude = currentlyLocked.plan_vuelo.ubicacion_destino.coordenadas.longitud;
			const destLatitude = currentlyLocked.plan_vuelo.ubicacion_destino.coordenadas.latitud;

			const coordinates = getFlightPosition(
				currentlyLocked.fecha_origen,
				[orgLongitude, orgLatitude] as [number, number],
				currentlyLocked.fecha_destino,
				[destLongitude, destLatitude] as [number, number],
				currentTime
			);
			zoom.setValueNoAnimation(2);
			centerLongitude.setValueNoAnimation(coordinates[0]);
			centerLatitude.setValueNoAnimation(coordinates[1]);
		}
	}, [currentTime, currentlyLocked]);

	function zoomIn(coordinates: [number, number], duration = 1000) {
		zoom.setValue(2, duration);
		centerLongitude.setValue(coordinates[0], duration);
		centerLatitude.setValue(coordinates[1], duration);
	}

	function zoomInNoAnimation(coordinates: [number, number]) {
		zoom.setValueNoAnimation(2);
		centerLongitude.setValueNoAnimation(coordinates[0]);
		centerLatitude.setValueNoAnimation(coordinates[1]);
	}

	function lockInFlight(vuelo: Vuelo) {
		setCurrentlyLocked(undefined);
		const orgLongitude = vuelo.plan_vuelo.ubicacion_origen.coordenadas.longitud;
		const orgLatitude = vuelo.plan_vuelo.ubicacion_origen.coordenadas.latitud;
		const destLongitude = vuelo.plan_vuelo.ubicacion_destino.coordenadas.longitud;
		const destLatitude = vuelo.plan_vuelo.ubicacion_destino.coordenadas.latitud;

		const coordinates = getFlightPosition(
			vuelo.fecha_origen,
			[orgLongitude, orgLatitude] as [number, number],
			vuelo.fecha_destino,
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
