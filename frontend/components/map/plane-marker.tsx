"use client";
import { getFlightPosition, getTrayectory } from "@/lib/map-utils";
import { Vuelo } from "@/lib/types";
import { cn } from "@/lib/utils";
import { Icon } from "leaflet";
import { useState } from "react";
import { Marker, Tooltip } from "react-leaflet";
import RotateMarker from "./test";

interface PlaneMarkerProps {
	vuelo: Vuelo;
	currentTime: Date;
	onClick: (vuelo: Vuelo) => void;
}

function PlaneMarker({ vuelo, currentTime, onClick }: PlaneMarkerProps) {
	const [isHovering, setIsHovering] = useState(false);

	const coordinates = getFlightPosition(
		vuelo.fechaSalida,
		[vuelo.planVuelo.ciudadOrigen.longitud, vuelo.planVuelo.ciudadOrigen.latitud] as [number, number],
		vuelo.fechaLlegada,
		[vuelo.planVuelo.ciudadDestino.longitud, vuelo.planVuelo.ciudadDestino.latitud] as [number, number],
		currentTime
	);

	if (
		(coordinates[0] === vuelo.planVuelo.ciudadDestino.longitud &&
			coordinates[1] === vuelo.planVuelo.ciudadDestino.latitud) ||
		(coordinates[0] === vuelo.planVuelo.ciudadOrigen.longitud &&
			coordinates[1] === vuelo.planVuelo.ciudadOrigen.latitud)
	) {
		return null;
	}

	const dotPositions = getTrayectory(vuelo);
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


	const trackIcon = new Icon({
		iconUrl: "/tracking-dots.png",
		iconSize: [20, 20],
	});

	return (
		<>
			{isHovering &&
				dotPositions.map((dotPosition, idx) => (
					<Marker
						key={idx}
						position={[dotPosition[1], dotPosition[0]] as [number, number]}
						icon={trackIcon}
						zIndexOffset={10}
						autoPan={false}
					/>
				))}
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
				<Tooltip direction="top" offset={[0,-5]} className="w-[55px] font-bold text-center">{vuelo.capacidadUtilizada} / {vuelo.planVuelo.capacidadMaxima}</Tooltip>
			</RotateMarker>
		</>
	);
}
export default PlaneMarker;

function Plane({
	vuelo
}: {
	vuelo: Vuelo;
}) {

	function mapCapacity(_capacity: number) {
		if (_capacity >= 0 && _capacity < 30) {
			return "#61DC00";
		} else if (_capacity >= 30 && _capacity < 60) {
			return "#EAB308";
		} else return "#FF0000";
	}

	const color = mapCapacity(vuelo.capacidadUtilizada);

	return (
		<>
			<g
				fill={color}
				version="1.1"
				id="Layer_1"
				xmlns="http://www.w3.org/2000/svg"
				xmlnsXlink="http://www.w3.org/1999/xlink"
				viewBox="0 0 122.88 122.88"
				xmlSpace="preserve"
				clipRule="evenodd"
				fillRule="evenodd"
				style={{
					width: "10px",
					height: "10px",
					transform: "translate(-6.7px, -5.5px)",
					rotate: vuelo.anguloAvion.toString() + "deg",
					zIndex: "2",
				}}
			>
				<path
					className="st0 stroke-white stroke-[4px]"
					d="M16.63,105.75c0.01-4.03,2.3-7.97,6.03-12.38L1.09,79.73c-1.36-0.59-1.33-1.42-0.54-2.4l4.57-3.9
                    c0.83-0.51,1.71-0.73,2.66-0.47l26.62,4.5l22.18-24.02L4.8,18.41c-1.31-0.77-1.42-1.64-0.07-2.65l7.47-5.96l67.5,18.97L99.64,7.45
                    c6.69-5.79,13.19-8.38,18.18-7.15c2.75,0.68,3.72,1.5,4.57,4.08c1.65,5.06-0.91,11.86-6.96,18.86L94.11,43.18l18.97,67.5
                    l-5.96,7.47c-1.01,1.34-1.88,1.23-2.65-0.07L69.43,66.31L45.41,88.48l4.5,26.62c0.26,0.94,0.05,1.82-0.47,2.66l-3.9,4.57
                    c-0.97,0.79-1.81,0.82-2.4-0.54l-13.64-21.57c-4.43,3.74-8.37,6.03-12.42,6.03C16.71,106.24,16.63,106.11,16.63,105.75
                    L16.63,105.75z"
					transform="scale(0.1)"
				/>
			</g>
			{/* <circle r={30} fill={color} className="" /> */}
			<circle r={5} className="fill-transparent" />
		</>
	);
}
