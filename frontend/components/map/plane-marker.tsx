import { Flight } from "@/lib/types/flight";
import { useState } from "react";
import { Marker, useMapContext } from "react-simple-maps";

function PlaneMarker({
	flight,
	currentTime,
    onClick
}: {
	flight: Flight;
	currentTime: Date;
    onClick: (coords: [number, number]) => void;
}) {
	const coordinates = getFlightPosition(
		flight.originTime,
		flight.originCoordinate as [number, number],
		flight.destinationTime,
		flight.destinationCoordinate as [number, number],
		currentTime
	);

	const dotPositions = getDotPositions();

	return (
		<>
			{dotPositions.map((dotPosition, idx) => (
				<Marker key={idx} coordinates={dotPosition as [number, number]}>
					<circle
						r={1}
						className="fill-mainRed"
						// onClick={() => {
						// 	zoomIn(coordinates as [number, number]);
						// }}
					/>
				</Marker>
			))}
			<Marker coordinates={coordinates as [number, number]}>
				<circle
					r={1}
					className="fill-green-500"
					// onClick={() => {
					// 	zoomIn(coordinates as [number, number]);
					// }}
				/>

				<Plane
					originCoordinate={
						flight.originCoordinate as [number, number]
					}
					destinationCoordinate={
						flight.destinationCoordinate as [number, number]
					}
					capacity={flight.capacity}
					onClick={() => {
						onClick(coordinates as [number, number]);
					}}
				/>
			</Marker>
		</>
	);

	function getDotPositions() {
		const dotPositions = [];
        const steps = 50;
		//we get 20 steps from the origin coordinate to the destination coordinate and return them in an array
		for (let i = 0; i < steps; i++) {
			const x =
				flight.originCoordinate[0] +
				((flight.destinationCoordinate[0] -
					flight.originCoordinate[0]) *
					i) /
					steps;
			const y =
				flight.originCoordinate[1] +
				((flight.destinationCoordinate[1] -
					flight.originCoordinate[1]) *
					i) /
					steps;
			dotPositions.push([x, y]);
		}
		return dotPositions;
	}

	function getFlightPosition(
		departureTime: Date,
		departurePosition: [number, number],
		arrivalTime: Date,
		arrivalPosition: [number, number],
		currentTime: Date
	) {
		// If the current time is before the departure time, return the departure position
		if (currentTime < departureTime) {
			return departurePosition;
		}

		// If the current time is after the arrival time, return the arrival position
		if (currentTime > arrivalTime) {
			return arrivalPosition;
		}

		const { projection } = useMapContext();
		// const [x1, y1] = projection(departurePosition);
		// const [x2, y2] = projection(arrivalPosition);
		const [x1, y1] = departurePosition;
		const [x2, y2] = arrivalPosition;

		// Calculate the position based on the time
		const totalDuration = arrivalTime.getTime() - departureTime.getTime();
		const elapsed = currentTime.getTime() - departureTime.getTime();

		const deltaX = x2 - x1;
		const deltaY = y2 - y1;

		const currentX = x1 + deltaX * (elapsed / totalDuration);
		const currentY = y1 + deltaY * (elapsed / totalDuration);

		return [currentX, currentY];
	}
}
export default PlaneMarker;

function Plane({
	onClick,
	capacity,
	originCoordinate,
	destinationCoordinate,
}: {
	onClick: () => void;
	capacity: number;
	originCoordinate: [number, number];
	destinationCoordinate: [number, number];
}) {
	const [rotation, setRotation] = useState(
		calculateAngle(originCoordinate, destinationCoordinate)
	);
	const [color, setColor] = useState(mapCapacity(capacity));

	function mapCapacity(_capacity: number) {
		if (_capacity >= 0 && _capacity < 30) {
			return "#61DC00";
		} else if (_capacity >= 30 && _capacity < 60) {
			return "#D3D800";
		} else return "#FF0000";
	}

	function calculateAngle(
		coord1: [number, number],
		coord2: [number, number]
	) {
		const { projection } = useMapContext();
		const [x1, y1] = projection(coord1);
		const [x2, y2] = projection(coord2);

		const deltaX = x2 - x1;
		const deltaY = y2 - y1;
		const radians = Math.atan2(deltaY, deltaX);
		const degrees = radians * (180 / Math.PI);

		return degrees + 45;
	}

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
					rotate: rotation.toString() + "deg",
					zIndex: "2",
				}}
				//onClick={onClick}
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
			<circle
				r={12}
				// stroke="#fff"
				// strokeWidth={1}
				className="fill-transparent"
				onClick={onClick}
			/>
		</>
	);
}
