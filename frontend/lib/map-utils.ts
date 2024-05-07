//@ts-ignore
import { useMapContext } from "react-simple-maps";
import { Vuelo } from "./types";

export function getFlightPosition(
	departureTime: Date,
	departurePosition: [number, number],
	arrivalTime: Date,
	arrivalPosition: [number, number],
	currentTime: Date
): [number, number] {
	// If the current time is before the departure time, return the departure position
	if (currentTime < departureTime) {
		return departurePosition;
	}

	// If the current time is after the arrival time, return the arrival position
	if (currentTime > arrivalTime) {
		return arrivalPosition;
	}
	
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


export function calculateAngle(coord1: [number, number], coord2: [number, number]) {
	const { projection } = useMapContext();
	const [x1, y1] = projection(coord1);
	const [x2, y2] = projection(coord2);

	const deltaX = x2 - x1;
	const deltaY = y2 - y1;
	const radians = Math.atan2(deltaY, deltaX);
	const degrees = radians * (180 / Math.PI);

	return degrees + 45;
}


export function getTrayectory(vuelo: Vuelo) {
	const dotPositions = [];
	const steps = 50;

	const originCoordinate = [
		vuelo.plan_vuelo.ubicacion_origen.longitud,
		vuelo.plan_vuelo.ubicacion_origen.latitud,
	] as [number, number];
	const destinationCoordinate = [
		vuelo.plan_vuelo.ubicacion_destino.longitud,
		vuelo.plan_vuelo.ubicacion_destino.latitud,
	] as [number, number];
	
	//we get 20 steps from the origin coordinate to the destination coordinate and return them in an array
	for (let i = 0; i < steps; i++) {
		const x = originCoordinate[0] + ((destinationCoordinate[0] - originCoordinate[0]) * i) / steps;
		const y = originCoordinate[1] + ((destinationCoordinate[1] - originCoordinate[1]) * i) / steps;
		dotPositions.push([x, y]);
	}
	return dotPositions;
}