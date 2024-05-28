import { useState, useRef, useEffect, useCallback } from "react";
import useAnimation, { AnimationObject } from "./useAnimation";
import { Vuelo } from "@/lib/types";
import { getFlightPosition } from "@/lib/map-utils";
import { set } from "date-fns";

const useMapZoom = (
	initialZoom = 1,
	initialLongitude = 0,
	initialLatitude = 0
): {
	currentTime: Date | undefined;
	setCurrentTime: (time: Date, fechaInicioSistema: Date, fechaInicioSim: Date, multiplicadorTiempo: number) => void;
	setCurrentTimeNoSimulation: () => void;
	zoom: AnimationObject;
	centerLongitude: AnimationObject;
	centerLatitude: AnimationObject;
	zoomIn: (coordinates: [number, number]) => void;
	lockInFlight: (vuelo: Vuelo) => void;
	unlockFlight: () => void;
} => {
	const zoomFactor = 4;
	const [currentTime, setCurrentTime] = useState<Date | undefined>(undefined);
	const curInterval = useRef<NodeJS.Timeout | null>(null);
	const [currentlyLocked, setCurrentlyLocked] = useState<Vuelo | undefined>(undefined);

	const zoom = useAnimation(initialZoom);
	const centerLongitude = useAnimation(initialLongitude);
	const centerLatitude = useAnimation(initialLatitude);

	const handleSetTime = useCallback(
		(time: Date, fechaInicioSistema: Date, fechaInicioSim: Date, multiplicadorTiempo: number) => {
			if (curInterval.current !== null) return;
			console.log("SETTING TIME RIGHT NOWWW!!!");
			setCurrentTime(time);
			const interval = setInterval(() => {
				const newTime = new Date(
					fechaInicioSim.getTime() +
						multiplicadorTiempo * (new Date().getTime() - fechaInicioSistema.getTime())
				);
				setCurrentTime(newTime);
			}, 1000);

			curInterval.current = interval;
			console.log("The current interval has been set");

			//return () => clearInterval(interval);
		},
		[curInterval, setCurrentTime]
	);

	const handleSetTimeNoSimulation = () => {
		if (curInterval.current !== null) return;
		const curDate = new Date();
		setCurrentTime(curDate);

		const interval = setInterval(() => {
			const newDate = new Date();
			setCurrentTime(newDate);
		}, 1000);

		curInterval.current = interval;
		console.log("The current interval has been set");

		//return () => clearInterval(interval);
	};

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

	const zoomIn = useCallback(
		(coordinates: [number, number], duration = 1000) => {
			zoom.setValue(zoomFactor, duration);
			centerLongitude.setValue(coordinates[0], duration);
			centerLatitude.setValue(coordinates[1], duration);
		},
		[zoom, centerLongitude, centerLatitude]
	);

	const zoomInNoAnimation = useCallback(
		(coordinates: [number, number]) => {
			zoom.setValueNoAnimation(zoomFactor);
			centerLongitude.setValueNoAnimation(coordinates[0]);
			centerLatitude.setValueNoAnimation(coordinates[1]);
		},
		[zoom, centerLongitude, centerLatitude]
	);

	const lockInFlight = useCallback(
		(vuelo: Vuelo) => {
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
		},
		[currentTime, zoomIn]
	);

	const unlockFlight = useCallback(() => {
		setCurrentlyLocked(undefined);
	}, [setCurrentlyLocked]);

	return {
		currentTime,
		setCurrentTime: handleSetTime,
		setCurrentTimeNoSimulation: handleSetTimeNoSimulation,
		zoom,
		centerLongitude,
		centerLatitude,
		zoomIn,
		lockInFlight,
		unlockFlight,
	};
};

export default useMapZoom;
