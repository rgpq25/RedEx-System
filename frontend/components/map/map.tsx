"use client";

import { cn } from "@/lib/utils";
import React, { useState } from "react";
import { ComposableMap, Geographies, Geography, ZoomableGroup } from "react-simple-maps";
import { Tooltip } from "react-tooltip";
import { AnimationObject } from "../hooks/useAnimation";
import PlaneMarker from "./plane-marker";
import AirportMarker from "./airport-marker";
import { Aeropuerto, Vuelo } from "@/lib/types";
import { vuelos } from "@/lib/sample";
import AirportModal from "./airport-modal";
import FlightModal from "./flight-modal";

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
		currentTime: Date;
		zoom: AnimationObject;
		centerLongitude: AnimationObject;
		centerLatitude: AnimationObject;
		zoomIn: (coordinates: [number, number]) => void;
		lockInFlight: (vuelo: Vuelo) => void;
		unlockFlight: () => void;
	};
	airports: Aeropuerto[];
	className?: string;
}

function Map({
	currentAirportModal,
	currentFlightModal,
	setCurrentAirportModal,
	setCurrentFlightModal,
	attributes,
	airports,
	className,
}: MapProps) {
	const { currentTime, zoom, centerLongitude, centerLatitude, zoomIn, lockInFlight, unlockFlight } = attributes;

	if (!zoom || !centerLongitude || !centerLatitude || !zoomIn) {
		throw new Error("Missing required zoom props, use useMapZoom hook to get them");
	}

	const [content, setContent] = useState<string>("");

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
				isOpen={currentFlightModal !== undefined}
				setIsOpen={(isOpen: boolean) => setCurrentFlightModal(undefined)}
				vuelo={currentFlightModal}
			/>
			<AirportModal
				isOpen={currentAirportModal !== undefined}
				setIsOpen={(isOpen: boolean) => setCurrentAirportModal(undefined)}
				aeropuerto={currentAirportModal}
			/>
			<Tooltip
				id="my-tooltip"
				className="border border-white z-[100]"
				classNameArrow="border-b-[1px] border-r-[1px] border-white"
			>
				{content}
			</Tooltip>
			<div
				className={cn("border rounded-xl flex justify-center items-center flex-1  overflow-hidden", className)}
			>
				{/* <Button size="icon" className="absolute top-4 right-4">
					<Settings className="w-5 h-5"/>
				</Button> */}
				<ComposableMap className="" projection={"geoMercator"} min={-5}>
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
										className="hover:fill-gray-600 fill-slate-400 transition-all duration-75 ease-in-out stroke-white stroke-[0.2px]"
									/>
								))
							}
						</Geographies>
						{vuelos.map((vuelo, idx) => {
							return (
								<PlaneMarker
									key={idx}
									currentTime={currentTime}
									vuelo={vuelo}
									onClick={(vuelo: Vuelo) => {
										setCurrentFlightModal(vuelo);
										lockInFlight(vuelo);
									}}
								/>
							);
						})}
						{airports.map((aeropuerto, idx) => {
							const latitud = aeropuerto.ubicacion.latitud;
							const longitud = aeropuerto.ubicacion.longitud;

							return (
								<AirportMarker
									key={idx}
									aeropuerto={aeropuerto}
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
