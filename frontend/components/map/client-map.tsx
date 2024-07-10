"use client";

import "leaflet/dist/leaflet.css";
import { cn } from "@/lib/utils";
import React, { LegacyRef, useMemo, useState } from "react";
import PlaneMarker from "./plane-marker";
import AirportMarker from "./airport-marker";
import { Aeropuerto, EstadoAlmacen, Simulacion, Vuelo } from "@/lib/types";
import AirportModal from "./airport-modal";
import FlightModal from "./flight-modal";
import { MapContainer, TileLayer } from "react-leaflet";
import { Map as MapType } from "leaflet";
import { MapZoomAttributes } from "../hooks/useMapZoom";
import { MapModalAttributes } from "../hooks/useMapModals";
import EnvioModal from "./envio-modal";
import useApi from "../hooks/useApi";
import { toast } from "sonner";
import { useFilteredFlightsContext } from "../contexts/flights-filter";

export interface MapProps {
	isSimulation: boolean;
	mapModalAttributes: MapModalAttributes;
	attributes: MapZoomAttributes;
	flights: Vuelo[];
	estadoAlmacen: EstadoAlmacen | null;
	simulation: Simulacion | undefined;
	className?: string;
}

function ClientMap({ isSimulation, mapModalAttributes, attributes, flights, simulation, estadoAlmacen, className }: MapProps) {

	const [airports, setAirports] = useState<Aeropuerto[]>([]);

	const { isLoading } = useApi(
		"GET",
		`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
		(data: Aeropuerto[]) => {
			setAirports(data);
		},
		(error) => {
			console.log(error);
			toast.error("Error al cargar aeropuertos");
		}
	);

	const { currentTime, map, setMap, zoomToAirport, lockToFlight, zoomToUbicacion, currentlyLockedFlight, getCurrentlyPausedTime, isAllRoutesVisible } = attributes;

	const {
		isAirportModalOpen,
		isFlightModalOpen,
		isEnvioModalOpen,
		setIsAirportModalOpen,
		setIsFlightModalOpen,
		setIsEnvioModalOpen,
		currentAirportModal,
		currentFlightModal,
		currentEnvioModal,
		setCurrentAirportModal,
		setCurrentFlightModal,
		setCurrentEnvioModal,
		openFlightModal,
		openAirportModal,
		openEnvioModal,
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
				zoomSnap={0.1}
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
							/>
						);
					})}

				{map &&
					currentTime &&
					flights.map((vuelo, idx) => {
						return (
							<PlaneMarker
								key={idx}
								currentTime={currentTime}
								vuelo={vuelo}
								onClick={(vuelo: Vuelo) => {
									lockToFlight(vuelo);
									openFlightModal(vuelo);
								}}
								focusedAirplane={currentlyLockedFlight}
								isAllRoutesVisible={isAllRoutesVisible}
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
				currentTime={currentTime}
				getCurrentlyPausedTime={getCurrentlyPausedTime}
				estadoAlmacen={estadoAlmacen}
			/>
			<EnvioModal
				currentTime={currentTime}
				isSimulation={isSimulation}
				isOpen={isEnvioModalOpen}
				setIsOpen={(isOpen: boolean) => setIsEnvioModalOpen(isOpen)}
				envio={currentEnvioModal}
				simulacion={simulation}
				lockToFlight={lockToFlight}
				zoomToUbicacion={zoomToUbicacion}
			/>
			<div className={cn("border rounded-xl flex justify-center items-center  overflow-hidden z-[10]", className)}>{displayMap}</div>
		</>
	);
}

export default ClientMap;
