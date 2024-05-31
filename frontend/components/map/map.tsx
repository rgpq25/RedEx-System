"use client";

import "leaflet/dist/leaflet.css";
import { cn } from "@/lib/utils";
import React, { LegacyRef, useCallback, useEffect, useMemo, useState } from "react";
import { ComposableMap, Geographies, Geography, ZoomableGroup } from "react-simple-maps";
import { Tooltip } from "react-tooltip";
import { AnimationObject } from "../hooks/useAnimation";
import PlaneMarker from "./plane-marker";
import AirportMarker from "./airport-marker";
import { Aeropuerto, EstadoAlmacen, Simulacion, Vuelo } from "@/lib/types";
import AirportModal from "./airport-modal";
import FlightModal from "./flight-modal";

import { MapContainer, TileLayer, Popup, Marker, Circle } from "react-leaflet";
import { Map as MapType } from "leaflet";
import { MapZoomAttributes } from "../hooks/useMapZoom";
import { MapModalAttributes } from "../hooks/useMapModals";
import { getCurrentAirportOcupation } from "@/lib/map-utils";

interface MapProps {
	isSimulation: boolean;
	mapModalAttributes: MapModalAttributes;
	attributes: MapZoomAttributes;
	airports: Aeropuerto[];
	flights: Vuelo[];
	estadoAlmacen: EstadoAlmacen | null;
	simulation: Simulacion | undefined;
	className?: string;
}

function Map({
	isSimulation,
	mapModalAttributes,
	attributes,
	airports,
	flights,
	simulation,
	estadoAlmacen,
	className,
}: MapProps) {
	if (typeof window === "undefined") return null;

	const { currentTime, map, setMap, zoomToAirport, lockToFlight } = attributes;

	const {
		setCurrentAirportModal,
		setCurrentFlightModal,
		currentFlightModal,
		currentAirportModal,
		isAirportModalOpen,
		isFlightModalOpen,
		setIsAirportModalOpen,
		setIsFlightModalOpen,
		openFlightModal,
		openAirportModal,
	} = mapModalAttributes;

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
				zoomControl={false}
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
									zoomToAirport(aeropuerto);
									openAirportModal(aeropuerto);
								}}
								usoHistorico={estadoAlmacen?.uso_historico[aeropuerto.ubicacion.id] || {}}
								currentTime={currentTime}
								//currentCapacity = {getCurrentAirportOcupation(estadoAlmacen?.uso_historico[aeropuerto.ubicacion.id] || {}, currentTime)}
							/>
						);
					})}

				{map &&
					currentTime &&
					flights
						.filter((flight: Vuelo) => flight.capacidadUtilizada !== 0 && flight.fechaSalida <= currentTime && currentTime <= flight.fechaLlegada)
						.map((vuelo, idx) => {
							return (
								<PlaneMarker
									key={idx}
									currentTime={currentTime}
									vuelo={vuelo}
									onClick={(vuelo: Vuelo) => {
										lockToFlight(vuelo);
										openFlightModal(vuelo);
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
				isSimulation={isSimulation}
				isOpen={isFlightModalOpen}
				setIsOpen={(isOpen: boolean) => setIsFlightModalOpen(isOpen)}
				vuelo={currentFlightModal}
				simulacion={simulation}
			/>
			<AirportModal
				isSimulation={isSimulation}
				isOpen={isAirportModalOpen}
				setIsOpen={(isOpen: boolean) => {
					setCurrentFlightModal(null);
					setIsAirportModalOpen(isOpen);
				}}
				aeropuerto={currentAirportModal}
				simulacion={simulation}
			/>
			<div
				className={cn(
					"border rounded-xl flex justify-center items-center  overflow-hidden z-[10]",
					className
				)}
			>
				{displayMap}
			</div>
		</>
	);
}

export default Map;
