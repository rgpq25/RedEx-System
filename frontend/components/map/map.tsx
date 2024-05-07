"use client";

import { cn } from "@/lib/utils";
import React, { forwardRef, useEffect, useState } from "react";
import { ComposableMap, Geographies, Geography, ZoomableGroup } from "react-simple-maps";
import { Tooltip } from "react-tooltip";
import useAnimation, { AnimationObject } from "../hooks/useAnimation";
import Chip from "../ui/chip";
import PlaneMarker from "./plane-marker";
import AirportMarker from "./airport-marker";
import { Aeropuerto, Vuelo } from "@/lib/types";
import { aeropuertos, ubicaciones, vuelos } from "@/lib/sample";
import useMapZoom from "../hooks/useMapZoom";
import AirportModal from "./airport-modal";
import FlightModal from "./flight-modal";
import useApi from "../hooks/useApi";

//TODO: Download and store on local repository, currently depends on third party URL
const geoUrl = "https://cdn.jsdelivr.net/npm/world-atlas@2/countries-110m.json";

//define props for coordinates and zoom
type Position = {
	coordinates: [number, number];
	zoom: number;
};

interface MapProps {
	currentTime: Date;
	zoom: AnimationObject;
	centerLongitude: AnimationObject;
	centerLatitude: AnimationObject;
	zoomIn: (coordinates: [number, number]) => void;
	lockInFlight: (vuelo: Vuelo) => void;
	unlockFlight: () => void;
	className?: string;
}

function Map({
	currentTime,
	zoom,
	centerLongitude,
	centerLatitude,
	zoomIn,
	lockInFlight,
	unlockFlight,
	className,
}: MapProps) {
	if (!zoom || !centerLongitude || !centerLatitude || !zoomIn) {
		throw new Error("Missing required zoom props, use useMapZoom hook to get them");
	}

	const [content, setContent] = useState<string>("");
	const [currentAirportModal, setCurrentAirportModal] = useState<Aeropuerto | undefined>(undefined);
	const [currentFlightModal, setCurrentFlightModal] = useState<Vuelo | undefined>(undefined);

	const { isLoading } = useApi(
		"localhost:8080/back/aeropuerto/",
		(data) => {
			console.log(data);
		},
		(error) => {
			console.log(error);
		}
	);

	function handleMoveEnd(position: Position) {
		zoom.setValueNoAnimation(position.zoom);
		centerLongitude.setValueNoAnimation(position.coordinates[0]);
		centerLatitude.setValueNoAnimation(position.coordinates[1]);
		unlockFlight();
	}

	function handleMoveStart() {
		zoom.cancelAnimation();
		centerLongitude.cancelAnimation();
		centerLatitude.cancelAnimation();
		unlockFlight();
	}

	return (
		<>
			<FlightModal
				isOpen={currentAirportModal !== undefined}
				setIsOpen={(isOpen: boolean) => setCurrentAirportModal(undefined)}
			/>
			<AirportModal
				isOpen={currentFlightModal !== undefined}
				setIsOpen={(isOpen: boolean) => setCurrentFlightModal(undefined)}
			/>
			<Tooltip
				id="my-tooltip"
				className="border border-white z-[100]"
				classNameArrow="border-b-[1px] border-r-[1px] border-white"
			>
				{content}
			</Tooltip>
			<div
				className={cn(
					"border rounded-xl flex justify-center items-center flex-1  overflow-hidden relative",
					className
				)}
			>
				<Chip color="blue" className="absolute top-8 right-8 text-2xl h-[40px] w-[130px] px-4  rounded-xl">
					{currentTime.toLocaleTimeString()}
				</Chip>
				<ComposableMap className="h-full w-full" projection={"geoEqualEarth"}>
					<ZoomableGroup
						zoom={zoom.value}
						center={[centerLongitude.value, centerLatitude.value]}
						onMoveEnd={handleMoveEnd}
						onMoveStart={handleMoveStart}
					>
						<Geographies geography={geoUrl}>
							{({ geographies }) =>
								geographies.map((geo) => (
									<Geography
										data-tooltip-id="my-tooltip"
										key={geo.rsmKey}
										geography={geo}
										onMouseEnter={() => {
											const { name } = geo.properties;
											setContent(name);
										}}
										onMouseLeave={() => {
											setContent("");
										}}
										className="hover:fill-gray-900 transition-all duration-75 ease-in-out stroke-white stroke-[0.2px]"
									/>
								))
							}
						</Geographies>
						{vuelos.map((vuelo, idx) => {
							return (
								<PlaneMarker
									currentTime={currentTime}
									vuelo={vuelo}
									key={idx}
									onClick={(vuelo: Vuelo) => {
										setCurrentFlightModal(vuelo);
										lockInFlight(vuelo);
									}}
								/>
							);
						})}
						{aeropuertos.map((aeropuerto, idx) => {
							const latitud = aeropuerto.ubicacion.coordenadas.latitud;
							const longitud = aeropuerto.ubicacion.coordenadas.longitud;

							return (
								<AirportMarker
									key={idx}
									coordinates={[longitud, latitud] as [number, number]}
									onClick={(coordinates: [number, number]) => {
										setCurrentAirportModal(aeropuerto);
										zoomIn(coordinates);
									}}
								/>
							);
						})}
					</ZoomableGroup>
				</ComposableMap>
			</div>
		</>
	);
}

export default Map;
