"use client";
import { getFlightPosition, getTrayectoryFromPosition } from "@/lib/map-utils";
import { Vuelo } from "@/lib/types";
import { Icon } from "leaflet";
import { useState } from "react";
import { Marker, Polyline, Tooltip } from "react-leaflet";
import RotateMarker from "./rotate-marker";

interface PlaneMarkerProps {
	vuelo: Vuelo;
	currentTime: Date;
	onClick: (vuelo: Vuelo) => void;
	focusedAirplane: Vuelo | null;
	isAllRoutesVisible: boolean;
}

function haversine(lon1: number, lat1: number, lon2: number, lat2: number) {
	const toRadians = (angle: number) => angle * (Math.PI / 180);

	const R = 6371; // Radio de la Tierra en kilómetros
	const dLat = toRadians(lat2 - lat1);
	const dLon = toRadians(lon2 - lon1);

	const a = Math.sin(dLat / 2) ** 2 + Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) * Math.sin(dLon / 2) ** 2;
	const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	return Math.round(R * c);
}

function PlaneMarker({ vuelo, currentTime, onClick, focusedAirplane, isAllRoutesVisible }: PlaneMarkerProps) {
	const [isHovering, setIsHovering] = useState(false);

	const coordinates = getFlightPosition(
		vuelo.fechaSalida,
		[vuelo.planVuelo.ciudadOrigen.longitud, vuelo.planVuelo.ciudadOrigen.latitud] as [number, number],
		vuelo.fechaLlegada,
		[vuelo.planVuelo.ciudadDestino.longitud, vuelo.planVuelo.ciudadDestino.latitud] as [number, number],
		currentTime
	);

	const vueloTrayectoryLeft = isAllRoutesVisible === false ? [] : getTrayectoryFromPosition(vuelo, coordinates);

	const kmLeft = haversine(coordinates[0], coordinates[1], vuelo.planVuelo.ciudadDestino.longitud, vuelo.planVuelo.ciudadDestino.latitud);

	if (
		(coordinates[0] === vuelo.planVuelo.ciudadDestino.longitud && coordinates[1] === vuelo.planVuelo.ciudadDestino.latitud) ||
		(coordinates[0] === vuelo.planVuelo.ciudadOrigen.longitud && coordinates[1] === vuelo.planVuelo.ciudadOrigen.latitud)
	) {
		return null;
	}

	const porcentajeUtilizado = (vuelo.capacidadUtilizada / vuelo.planVuelo.capacidadMaxima) * 100;

	const airplaneIcon = new Icon({
		iconUrl:
			porcentajeUtilizado >= 0 && porcentajeUtilizado <= 30
				? "/green-airplane.png"
				: porcentajeUtilizado > 30 && porcentajeUtilizado <= 60
				? "/yellow-airplane.png"
				: "/red-airplane.png",
		iconSize: [20, 20],
	});

	const airplaneIndicator = new Icon({
		iconUrl: "/airplane-indicator.png",
		iconSize: [25, 25],
		iconAnchor: [12.5, -20],
	});

	return (
		<>
			{isHovering === true && <Polyline positions={vuelo.posicionesRuta} lineJoin="round" />}

			{isAllRoutesVisible === true && <Polyline positions={vueloTrayectoryLeft} lineJoin="round" className="stroke-[0.7px] stroke-black" dashArray={'10,10'} dashOffset="5"/>}

			{vuelo.id === focusedAirplane?.id && (
				<Marker position={[coordinates[1], coordinates[0]] as [number, number]} icon={airplaneIndicator} zIndexOffset={52} />
			)}
			<RotateMarker
				position={[coordinates[1], coordinates[0]] as [number, number]}
				icon={airplaneIcon}
				eventHandlers={{
					click: () => onClick(vuelo),
					mouseover: () => setIsHovering(true),
					mouseout: () => setIsHovering(false),
				}}
				zIndexOffset={50}
				rotationAngle={vuelo.anguloAvion}
				rotationOrigin="center"
			>
				<Tooltip direction="top" offset={[0, -5]} className="font-bold text-center">
					{vuelo.capacidadUtilizada} / {vuelo.planVuelo.capacidadMaxima + ` (${kmLeft}km restantes)`}
				</Tooltip>
			</RotateMarker>
		</>
	);
}
export default PlaneMarker;
