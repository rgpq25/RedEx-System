"use client";
import { Aeropuerto } from "@/lib/types";
import { cn } from "@/lib/utils";
import { Icon } from "leaflet";
import { useEffect, useState } from "react";
import { Marker, Popup, useMapEvents } from "react-leaflet";

function AirportMarker({
	aeropuerto,
	coordinates,
	onClick,
}: {
	aeropuerto: Aeropuerto;
	coordinates: [number, number];
	onClick: (coordinates: [number, number]) => void;
}) {
	const [isHovering, setIsHovering] = useState(false);

	function handleMouseEnter() {
		setIsHovering(true);
	}

	function handleMouseLeave() {
		setIsHovering(false);
	}

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
			</Marker>
		</>
	);
}

export default AirportMarker;
