"use client";
import { getCurrentAirportOcupation } from "@/lib/map-utils";
import { Aeropuerto, HistoricoValores } from "@/lib/types";
import { cn } from "@/lib/utils";
import { Icon } from "leaflet";
import { useEffect, useState } from "react";
import { Marker, Popup, Tooltip } from "react-leaflet";

function AirportMarker({
	aeropuerto,
	usoHistorico,
	currentTime,
	//currentCapacity,
	coordinates,
	onClick,
}: {
	aeropuerto: Aeropuerto;
	usoHistorico: HistoricoValores;
	currentTime: Date | undefined;
	//currentCapacity: number;
	coordinates: [number, number];
	onClick: (coordinates: [number, number]) => void;
}) {
	const currentCapacity = getCurrentAirportOcupation(usoHistorico, currentTime);
	const porcentajeOcupacion = (currentCapacity / aeropuerto.capacidadMaxima) * 100;

	const airportIcon = new Icon({
		iconUrl: "/airportIcon.png",
		iconSize: [30, 30],
		iconAnchor: [15, 30],
	});

	return (
		<>
			<Marker
				position={coordinates}
				icon={airportIcon}
				eventHandlers={{
					click: () => onClick(coordinates),
				}}
			>
				<Tooltip direction="top" offset={[0, -35]} className="font-bold text-md text-center">
					{aeropuerto.ubicacion.id + " (" + currentCapacity + "/" + aeropuerto.capacidadMaxima + ")"}
				</Tooltip>
				<Tooltip permanent direction="bottom" >
					{currentCapacity}
				</Tooltip>
			</Marker>
		</>
	);
}

export default AirportMarker;
