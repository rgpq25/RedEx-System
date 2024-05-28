"use client";

import "leaflet/dist/leaflet.css";
import { cn } from "@/lib/utils";
import React, { LegacyRef, useCallback, useEffect, useMemo, useState } from "react";
import { ComposableMap, Geographies, Geography, ZoomableGroup } from "react-simple-maps";
import { Tooltip } from "react-tooltip";
import { AnimationObject } from "../hooks/useAnimation";
import PlaneMarker from "./plane-marker";
import AirportMarker from "./airport-marker";
import { Aeropuerto, Simulacion, Vuelo } from "@/lib/types";
import AirportModal from "./airport-modal";
import FlightModal from "./flight-modal";

import { MapContainer, TileLayer, Popup, Marker, Circle } from "react-leaflet";
import { Map as MapType } from "leaflet";
import { getFlightPosition } from "@/lib/map-utils";

//TODO: Download and store on local repository, currently depends on third party URL
const geoUrl = "https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json";

type Position = {
	coordinates: [number, number];
	zoom: number;
};

interface MapProps {
	currentAirportModal: Aeropuerto | undefined;
	currentFlightModal: Vuelo | undefined;
	setCurrentAirportModal: (aeropuerto: Aeropuerto | undefined) => void;
	setCurrentFlightModal: (vuelo: Vuelo | undefined) => void;
	attributes: {
		currentTime: Date | undefined;
		setCurrentTime: (
			time: Date,
			fechaInicioSistema: Date,
			fechaInicioSim: Date,
			multiplicadorTiempo: number
		) => void;
		setCurrentTimeNoSimulation: () => void;
		map: MapType | null;
		setMap: (map: MapType) => void;
		zoomToAirport: (airport: Aeropuerto) => void;
		lockToFlight: (flight: Vuelo) => void;
	};
	airports: Aeropuerto[];
	flights: Vuelo[];
	simulation: Simulacion | undefined;
	className?: string;
}

function Map({
	currentAirportModal,
	currentFlightModal,
	setCurrentAirportModal,
	setCurrentFlightModal,
	attributes,
	airports,
	flights,
	simulation,
	className,
}: MapProps) {
	const {
		currentTime,
		map,
		setMap,
		zoomToAirport,
		lockToFlight,
	} = attributes;

	const displayMap = useMemo(
		() => (
			<MapContainer
				center={[0, 0]}
				zoom={3}
				scrollWheelZoom={true}
				maxZoom={8}
				minZoom={3}
				className="z-[10]"
				ref={setMap as LegacyRef<MapType>}
			>
				<TileLayer
					attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
					//url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x}"	//Clear less realistic
					//url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}" //Realistic
					url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png" //Light clear map
					//url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"		//Dark map
				/>

				{map &&
					airports.map((aeropuerto, idx) => {
						const latitud = aeropuerto.ubicacion.latitud;
						const longitud = aeropuerto.ubicacion.longitud;

						return (
							<AirportMarker
								key={aeropuerto.id}
								aeropuerto={aeropuerto}
								coordinates={[latitud, longitud] as [number, number]}
								onClick={(coordinates: [number, number]) => {
									setCurrentAirportModal(aeropuerto);
									zoomToAirport(aeropuerto);
								}}
							/>
						);
					})}

				{map &&
					currentTime &&
					flights
						.filter((flight: Vuelo) => flight.capacidadUtilizada !== 0)
						.map((vuelo, idx) => {
							return (
								<PlaneMarker
									key={idx}
									currentTime={currentTime}
									vuelo={vuelo}
									onClick={(vuelo: Vuelo) => {
										setCurrentFlightModal(vuelo);
										lockToFlight(vuelo);
									}}
								/>
							);
						})}
			</MapContainer>
		),
		[currentTime, flights, airports]
	);

	return (
		<>
			<FlightModal
				isOpen={currentFlightModal !== undefined}
				setIsOpen={(isOpen: boolean) => setCurrentFlightModal(undefined)}
				vuelo={currentFlightModal}
				simulacion={simulation}
			/>
			<AirportModal
				isOpen={currentAirportModal !== undefined}
				setIsOpen={(isOpen: boolean) => setCurrentAirportModal(undefined)}
				aeropuerto={currentAirportModal}
				simulacion={simulation}
			/>
			<div
				className={cn(
					"border rounded-xl flex justify-center items-center flex-1  overflow-hidden z-[10]",
					className
				)}
			>
				{displayMap}
			</div>
		</>
	);
}

export default Map;
