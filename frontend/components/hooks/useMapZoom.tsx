import { useState, useRef, useEffect, useCallback } from "react";
import useAnimation, { AnimationObject } from "./useAnimation";
import { Aeropuerto, Simulacion, Vuelo } from "@/lib/types";
import { getFlightPosition } from "@/lib/map-utils";
import { Map as MapType } from "leaflet";

export type MapZoomAttributes = {
	currentTime: Date | undefined;
	setCurrentTime: (simulacion: Simulacion) => void;
	setCurrentTimeNoSimulation: () => void;
	map: MapType | null;
	setMap: (map: MapType) => void;
	zoomToAirport: (airport: Aeropuerto) => void;
	lockToFlight: (flight: Vuelo) => void;
	pauseSimulation: () => void;
	playSimulation: (oldSimulation: Simulacion, setSimulation: (simulacion: Simulacion) => void) => void;
};

const useMapZoom = (initialZoom = 1, initialLongitude = 0, initialLatitude = 0): MapZoomAttributes => {
	const zoomFactor = 7;
	const curInterval = useRef<NodeJS.Timeout | null>(null);
	const pausingTime = useRef<number | null>(null);
	const isTimerPaused = useRef<boolean>(false);
	const time_miliPaused = useRef<number>(0);

	const [map, setMap] = useState<MapType | null>(null);
	const [currentTime, setCurrentTime] = useState<Date | undefined>(undefined);
	const [currentlyLockedFlight, setCurrentlyLockedFlight] = useState<Vuelo | null>(null);
	const [isLockedToFlight, setIsLockedToFlight] = useState(false);

	const handleSetTime = (simulacion: Simulacion) => {
		if (isTimerPaused.current === true) return;
		if (curInterval.current !== null) return;

		function getNewTime(_simu: Simulacion) {
			const fechaInicioSistema: Date = new Date(_simu.fechaInicioSistema);
			const fechaInicioSim: Date = new Date(_simu.fechaInicioSim);
			const multiplicadorTiempo: number = _simu.multiplicadorTiempo;
			// const milisegundosPausados: number = _simu.milisegundosPausados;
			const milisegundosPausados: number = time_miliPaused.current;
			console.log("Miliseconds paused in handleSetTime: " + milisegundosPausados);

			const currentSimTime = new Date(
				fechaInicioSim.getTime() + multiplicadorTiempo * (new Date().getTime() - fechaInicioSistema.getTime() - milisegundosPausados)
			);

			return currentSimTime;
		}

		const currentSimTime = getNewTime(simulacion);

		console.log("Previous time was: ", currentTime);
		console.log("Setting new time: ", currentSimTime);

		setCurrentTime(currentSimTime);

		const interval = setInterval(() => {
			const newTime = getNewTime(simulacion);
			setCurrentTime(newTime);
		}, 300);

		curInterval.current = interval;
		isTimerPaused.current = false;

		return () => clearInterval(interval);
	};

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

	const pauseSimulation = () => {
		//we stop the timer
		if (curInterval.current) {
			console.log("Pausing simulation");
			clearInterval(curInterval.current);
			curInterval.current = null;
			pausingTime.current = (new Date()).getTime();
			isTimerPaused.current = true;
		}
	};

	const playSimulation = (oldSimulation: Simulacion, setSimulation: (simulacion: Simulacion) => void) => {
		//set the new simulation object with the new milisegundosPausados;
		if (pausingTime.current !== null) {
			console.log("Playing simulation");

			isTimerPaused.current = false;

			const pauseDate: number = pausingTime.current;
			const currentDate = (new Date()).getTime();

			const milisecondsPaused = currentDate - pauseDate;
			console.log("Miliseconds paused: ", milisecondsPaused);

			const newSimulation = { ...oldSimulation };
			newSimulation.milisegundosPausados += milisecondsPaused;
			time_miliPaused.current += milisecondsPaused;
			console.log("See the new simulation object: ", newSimulation);
			setSimulation(newSimulation);
			pausingTime.current = null;
			handleSetTime(newSimulation);
		}
	};

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
		pauseSimulation,
		playSimulation,
	};
};

export default useMapZoom;
