import { useState, useRef, useEffect, useCallback } from "react";
import useAnimation, { AnimationObject } from "./useAnimation";
import { Aeropuerto, Simulacion, Ubicacion, Vuelo } from "@/lib/types";
import { getFlightPosition } from "@/lib/map-utils";
import { Map as MapType } from "leaflet";
import { api } from "@/lib/api";

export type MapZoomAttributes = {
	currentTime: Date | undefined;
	elapsedRealTime: number;
	elapsedSimulationTime: number;
	currentlyLockedFlight: Vuelo | null;
	setCurrentTime: (simulacion: Simulacion) => void;
	setCurrentTimeNoSimulation: () => void;
	map: MapType | null;
	setMap: (map: MapType) => void;
	zoomToAirport: (airport: Aeropuerto) => void;
	lockToFlight: (flight: Vuelo) => void;
	zoomToUbicacion: (ubicacion: Ubicacion) => void;
	pauseSimulation: (_simu: Simulacion) => Promise<void>;
	playSimulation: (oldSimulation: Simulacion, setSimulation: (simulacion: Simulacion) => void) => Promise<void>;
	getCurrentlyPausedTime: () => number;
};

const useMapZoom = (initialZoom = 1, initialLongitude = 0, initialLatitude = 0): MapZoomAttributes => {
	const zoomFactor = 7;
	const curInterval = useRef<NodeJS.Timeout | null>(null);
	const pausingTime = useRef<number | null>(null);
	const ref_milisecondsPaused = useRef<number>(0);
	const isTimerPaused = useRef<boolean>(false);

	const [map, setMap] = useState<MapType | null>(null);
	const [currentTime, setCurrentTime] = useState<Date | undefined>(undefined);
	const [elapsedSimulationTime, setElapsedSimulationTime] = useState<number>(0);
	const [elapsedRealTime, setElapsedRealTime] = useState<number>(0);
	const [currentlyLockedFlight, setCurrentlyLockedFlight] = useState<Vuelo | null>(null);
	const [isLockedToFlight, setIsLockedToFlight] = useState(false);

	const handleSetTime = (simulacion: Simulacion) => {
		if (isTimerPaused.current === true) return;
		if (curInterval.current !== null) return;

		function getNewTime(_simu: Simulacion) {
			const fechaInicioSistema: Date = new Date(_simu.fechaInicioSistema);
			const fechaInicioSim: Date = new Date(_simu.fechaInicioSim);
			const multiplicadorTiempo: number = _simu.multiplicadorTiempo;
			const milisegundosPausados: number = _simu.milisegundosPausados;

			const currentSimTime = new Date(
				fechaInicioSim.getTime() + multiplicadorTiempo * (new Date().getTime() - fechaInicioSistema.getTime() - milisegundosPausados)
			);

			return currentSimTime;
		}

		function getElapsedTime(_simu: Simulacion) {
			const fechaInicioSistema: Date = new Date(_simu.fechaInicioSistema);
			const fechaInicioSim: Date = new Date(_simu.fechaInicioSim);
			const multiplicadorTiempo: number = _simu.multiplicadorTiempo;
			const milisegundosPausados: number = _simu.milisegundosPausados;

			const elapsedSimulatedTime = new Date(
				multiplicadorTiempo * (new Date().getTime() - fechaInicioSistema.getTime() - milisegundosPausados)
			).getTime();

			const elapsedRealTime = new Date(new Date().getTime() - fechaInicioSistema.getTime() - milisegundosPausados).getTime();

			return { elapsedRealTime, elapsedSimulatedTime };
		}

		const currentSimTime = getNewTime(simulacion);
		setCurrentTime(currentSimTime);

		const elapsedTimes = getElapsedTime(simulacion);
		setElapsedRealTime(elapsedTimes.elapsedRealTime);
		setElapsedSimulationTime(elapsedTimes.elapsedSimulatedTime);

		const interval = setInterval(() => {
			const newTime = getNewTime(simulacion);
			setCurrentTime(newTime);

			const newElapsedTimes = getElapsedTime(simulacion);
			setElapsedRealTime(newElapsedTimes.elapsedRealTime);
			setElapsedSimulationTime(newElapsedTimes.elapsedSimulatedTime);
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

	const zoomToUbicacion = useCallback(
		(ubicacion: Ubicacion) => {
			if (map === null) return;
			setCurrentlyLockedFlight(null);
			map.setView([ubicacion.latitud, ubicacion.longitud], zoomFactor);
		},
		[map]
	);

	const onFlightClick = useCallback(
		(flight: Vuelo) => {
			console.log("About to lock to flight: ", flight);
			setCurrentlyLockedFlight(flight);
			setTimeout(() => {
				setIsLockedToFlight(true);
			}, 500);
		},
		[map]
	);

	const pauseSimulation = async (_simulation: Simulacion) => {
		//we stop the timer
		if (curInterval.current) {
			console.log("Pausing simulation");
			clearInterval(curInterval.current);
			curInterval.current = null;
			pausingTime.current = new Date().getTime();
			isTimerPaused.current = true;

			await api(
				"PUT",
				`${process.env.NEXT_PUBLIC_API}/back/simulacion/pausar`,
				(data: Simulacion) => {
					console.log("Respuesta de /back/simulacion/pausar: ", data);
				},
				(error) => {
					console.log("Error en /back/simulacion/pausar: ", error);
				},
				_simulation
			);
		}
	};

	const playSimulation = async (oldSimulation: Simulacion, setSimulation: (simulacion: Simulacion) => void) => {
		//set the new simulation object with the new milisegundosPausados;
		if (pausingTime.current !== null) {
			console.log("Playing simulation");

			isTimerPaused.current = false;
			const pauseDate: number = pausingTime.current;
			const currentDate = new Date().getTime();
			const milisecondsPaused = currentDate - pauseDate;

			const newSimulation = { ...oldSimulation };
			newSimulation.milisegundosPausados += milisecondsPaused;
			ref_milisecondsPaused.current += milisecondsPaused;
			pausingTime.current = null;

			await api(
				"PUT",
				`${process.env.NEXT_PUBLIC_API}/back/simulacion/reanudar`,
				(data: Simulacion) => {
					console.log("Respuesta de /back/simulacion/reanudar: ", data);
				},
				(error) => {
					console.log("Error en /back/simulacion/reanudar: ", error);
				},
				newSimulation
			);

			setSimulation(newSimulation);
			handleSetTime(newSimulation);
		}
	};

	const getCurrentlyPausedTime = () => {
		if (pausingTime.current === null) return ref_milisecondsPaused.current;
		const pauseDate: number = pausingTime.current;
		const currentDate = new Date().getTime();
		const milisecondsPaused = currentDate - pauseDate;
		return ref_milisecondsPaused.current + milisecondsPaused;
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
		elapsedRealTime,
		elapsedSimulationTime,
		currentlyLockedFlight,
		setCurrentTime: handleSetTime,
		setCurrentTimeNoSimulation: handleSetTimeNoSimulation,
		map,
		setMap,
		zoomToAirport: onAirportClick,
		lockToFlight: onFlightClick,
		zoomToUbicacion,
		pauseSimulation,
		playSimulation,
		getCurrentlyPausedTime,
	};
};

export default useMapZoom;
