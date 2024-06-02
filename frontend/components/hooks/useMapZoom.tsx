import { useState, useRef, useEffect, useCallback } from "react";
import useAnimation, { AnimationObject } from "./useAnimation";
import { Aeropuerto, Vuelo } from "@/lib/types";
import { getFlightPosition } from "@/lib/map-utils";
import { Map as MapType } from "leaflet";


export type MapZoomAttributes = {
	currentTime: Date | undefined;
	setCurrentTime: (time: Date, fechaInicioSistema: Date, fechaInicioSim: Date, multiplicadorTiempo: number) => void;
	setCurrentTimeNoSimulation: () => void;
	map: MapType | null;
	setMap: (map: MapType) => void;
	zoomToAirport: (airport: Aeropuerto) => void;
	lockToFlight: (flight: Vuelo) => void;
}

const useMapZoom = (
	initialZoom = 1,
	initialLongitude = 0,
	initialLatitude = 0
): MapZoomAttributes => {
	const zoomFactor = 7;
	const curInterval = useRef<NodeJS.Timeout | null>(null);

	const [map, setMap] = useState<MapType | null>(null);
	const [currentTime, setCurrentTime] = useState<Date | undefined>(undefined);
	const [currentlyLockedFlight, setCurrentlyLockedFlight] = useState<Vuelo | null>(null);
	const [isLockedToFlight, setIsLockedToFlight] = useState(false);

	const handleSetTime = useCallback(
		(time: Date, fechaInicioSistema: Date, fechaInicioSim: Date, multiplicadorTiempo: number) => {
			if (curInterval.current !== null) return;
			setCurrentTime(time);
			const interval = setInterval(() => {
				const newTime = new Date(
					fechaInicioSim.getTime() +
						multiplicadorTiempo * (new Date().getTime() - fechaInicioSistema.getTime())
				);
				setCurrentTime(newTime);
			}, 1000);

			curInterval.current = interval;
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
	};

	const onAirportClick = useCallback(
		(aeropuerto: Aeropuerto) => {
			if (map === null) return;
			setCurrentlyLockedFlight(null);
			map.setView([aeropuerto.ubicacion.latitud, aeropuerto.ubicacion.longitud], zoomFactor);
		},
		[map]
	);

	const onFlightClick = useCallback(
		(flight: Vuelo) => {
			setCurrentlyLockedFlight(flight);
			setTimeout(() => {
				setIsLockedToFlight(true);
			}, 500);
		},
		[map]
	);

	function onMove() {
		if (isLockedToFlight && currentlyLockedFlight) {
			console.log("Removing locked flight");
			setCurrentlyLockedFlight(null);
			setIsLockedToFlight(false);
		}
	}

	useEffect(() => {
		if (currentlyLockedFlight && currentTime && map) {
			//get current flight position and set it in the map.setView
			const orgLongitude = currentlyLockedFlight.planVuelo.ciudadOrigen.longitud;
			const orgLatitude = currentlyLockedFlight.planVuelo.ciudadOrigen.latitud;
			const destLongitude = currentlyLockedFlight.planVuelo.ciudadDestino.longitud;
			const destLatitude = currentlyLockedFlight.planVuelo.ciudadDestino.latitud;

			const coordinates = getFlightPosition(
				currentlyLockedFlight.fechaSalida,
				[orgLongitude, orgLatitude] as [number, number],
				currentlyLockedFlight.fechaLlegada,
				[destLongitude, destLatitude] as [number, number],
				currentTime
			);

			map.setView([coordinates[1], coordinates[0]], zoomFactor);
		}
	}, [currentlyLockedFlight, currentTime, map]);

	useEffect(() => {
		if (map === null) return;
		map.on("dragstart", onMove);
		map.on("zoomstart", onMove);
		return () => {
			map.off("dragstart", onMove);
			map.off("zoomstart", onMove);
		};
	}, [map, onMove]);

	return {
		currentTime,
		setCurrentTime: handleSetTime,
		setCurrentTimeNoSimulation: handleSetTimeNoSimulation,
		map,
		setMap,
		zoomToAirport: onAirportClick,
		lockToFlight: onFlightClick,
	};
};

export default useMapZoom;
