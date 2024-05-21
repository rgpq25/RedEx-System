import { useState, useRef, useEffect } from "react";
import useAnimation, { AnimationObject } from "./useAnimation";
import { Vuelo } from "@/lib/types";
import { getFlightPosition } from "@/lib/map-utils";

const useMapZoom = (
	initialZoom = 1,
	initialLongitude = 0,
	initialLatitude = 0
): {
	currentTime: Date | undefined;
	setCurrentTime: (time: Date, fechaInicioSistema: Date, fechaInicioSim: Date, multiplicadorTiempo: number) => void;
	zoom: AnimationObject;
	centerLongitude: AnimationObject;
	centerLatitude: AnimationObject;
	zoomIn: (coordinates: [number, number]) => void;
	lockInFlight: (vuelo: Vuelo) => void;
	unlockFlight: () => void;
} => {
	const zoomFactor = 4;
	const [currentTime, setCurrentTime] = useState<Date | undefined>(undefined);
	const [currentlyLocked, setCurrentlyLocked] = useState<Vuelo | undefined>(undefined);

	const zoom = useAnimation(initialZoom);
	const centerLongitude = useAnimation(initialLongitude);
	const centerLatitude = useAnimation(initialLatitude);

	const handleSetTime = (time: Date, fechaInicioSistema: Date, fechaInicioSim: Date, multiplicadorTiempo: number) => {
		setCurrentTime(time);
		//const startTime = new Date().getTime();
		const interval = setInterval(() => {
		// 	const currentTime = new Date().getTime();
		// 	const elapsedTime = (currentTime - startTime) * 500;

			const newTime = new Date(fechaInicioSim.getTime() + 500 * (new Date().getTime() - fechaInicioSistema.getTime()));
			// setCurrentTime(new Date(startTime + elapsedTime));
			setCurrentTime(newTime);
		}, 1000);
	};

	// useEffect(() => {
	// 	const startTime = new Date().getTime();
	// 	const interval = setInterval(() => {
	// 		const currentTime = new Date().getTime();
	// 		const elapsedTime = (currentTime - startTime) * 500;
	// 		setCurrentTime(new Date(startTime + elapsedTime));
	// 	}, 1000);

	// 	return () => {
	// 		clearInterval(interval);
	// 	};
	// }, []);

	useEffect(() => {
		if (currentlyLocked && currentTime) {
			const orgLongitude = currentlyLocked.planVuelo.ciudadOrigen.longitud;
			const orgLatitude = currentlyLocked.planVuelo.ciudadOrigen.latitud;
			const destLongitude = currentlyLocked.planVuelo.ciudadDestino.longitud;
			const destLatitude = currentlyLocked.planVuelo.ciudadDestino.latitud;

			const coordinates = getFlightPosition(
				currentlyLocked.fechaSalida,
				[orgLongitude, orgLatitude] as [number, number],
				currentlyLocked.fechaLlegada,
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
		if (currentTime === undefined) return;

		setCurrentlyLocked(undefined);
		const orgLongitude = vuelo.planVuelo.ciudadOrigen.longitud;
		const orgLatitude = vuelo.planVuelo.ciudadOrigen.latitud;
		const destLongitude = vuelo.planVuelo.ciudadDestino.longitud;
		const destLatitude = vuelo.planVuelo.ciudadDestino.latitud;

		const coordinates = getFlightPosition(
			vuelo.fechaSalida,
			[orgLongitude, orgLatitude] as [number, number],
			vuelo.fechaLlegada,
			[destLongitude, destLatitude] as [number, number],
			currentTime
		);
		zoomIn(coordinates);

		setTimeout(() => {
			setCurrentlyLocked(vuelo);
		}, 1100);
	}

	function unlockFlight() {
		setCurrentlyLocked(undefined);
	}

	return {
		currentTime,
		setCurrentTime: handleSetTime,
		zoom,
		centerLongitude,
		centerLatitude,
		zoomIn,
		lockInFlight,
		unlockFlight,
	};
};

export default useMapZoom;
