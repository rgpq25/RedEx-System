//@ts-ignore
import { Aeropuerto, Envio, EstadoAlmacen, HistoricoValores, Paquete, RespuestaAlgoritmo, UsoHistorico, Vuelo } from "./types";

export function getFlightPosition(
	departureTime: Date,
	departurePosition: [number, number],
	arrivalTime: Date,
	arrivalPosition: [number, number],
	currentTime: Date
): [number, number] {
	// If the current time is before the departure time, return the departure position
	if (currentTime < new Date(departureTime)) {
		return departurePosition;
	}

	// If the current time is after the arrival time, return the arrival position
	if (currentTime > new Date(arrivalTime)) {
		return arrivalPosition;
	}

	const [x1, y1] = departurePosition;
	const [x2, y2] = arrivalPosition;

	// Calculate the position based on the time
	const totalDuration = new Date(arrivalTime).getTime() - new Date(departureTime).getTime();
	const elapsed = currentTime.getTime() - new Date(departureTime).getTime();

	const deltaX = x2 - x1;
	const deltaY = y2 - y1;

	const currentX = x1 + deltaX * (elapsed / totalDuration);
	const currentY = y1 + deltaY * (elapsed / totalDuration);

	return [currentX, currentY];
}

export function calculateAngle(coord1: [number, number], coord2: [number, number]) {
	const [x1, y1] = coord1;
	const [x2, y2] = coord2;

	const deltaX = x2 - x1;
	const deltaY = y2 - y1;
	const radians = Math.atan2(deltaY, deltaX);
	const degrees = radians * (180 / Math.PI);

	return degrees * -1;
}

export function getTrayectory(vuelo: Vuelo) {
	const dotPositions = [];
	let steps = 50;

	const originCoordinate = [vuelo.planVuelo.ciudadOrigen.latitud, vuelo.planVuelo.ciudadOrigen.longitud] as [number, number];
	const destinationCoordinate = [vuelo.planVuelo.ciudadDestino.latitud, vuelo.planVuelo.ciudadDestino.longitud] as [number, number];

	const distance = Math.sqrt(
		Math.pow(destinationCoordinate[0] - originCoordinate[0], 2) + Math.pow(destinationCoordinate[1] - originCoordinate[1], 2)
	);

	steps = Math.floor(distance / 2);

	dotPositions.push(originCoordinate);
	//we get 20 steps from the origin coordinate to the destination coordinate and return them in an array
	for (let i = 0; i < steps; i++) {
		const x = originCoordinate[0] + ((destinationCoordinate[0] - originCoordinate[0]) * i) / steps;
		const y = originCoordinate[1] + ((destinationCoordinate[1] - originCoordinate[1]) * i) / steps;
		dotPositions.push([x, y]);
	}
	dotPositions.push(destinationCoordinate);

	return dotPositions as [number, number][];
}

export function getCurrentAirportOcupation_Old(estadoAlmacen: HistoricoValores, fecha: Date | undefined): number {
	if (fecha === undefined) {
		//console.log("Fecha no definida");
		return 0;
	}

	const fechas = Object.keys(estadoAlmacen);

	if (fechas.length === 0) {
		//console.log("No hay fechas para este estadoAlmacen");
		return 0; //basicamente no tiene historico
	}

	// Si la fecha dada es menor que la primera fecha en el arreglo
	if (fecha < new Date(fechas[0])) {
		//console.log("Fecha menor a la primera fecha en el arreglo");
		return estadoAlmacen[fechas[0].toString()];
	}

	for (let i = 0; i < fechas.length - 1; i++) {
		if (fecha >= new Date(fechas[i]) && fecha < new Date(fechas[i + 1])) {
			//console.log("Fecha entre dos fechas en el arreglo");
			return estadoAlmacen[fechas[i + 1]];
		}
	}

	//console.log("Fecha mayor a la última fecha en el arreglo");
	// Si la fecha es mayor o igual a la última fecha en el arreglo
	return estadoAlmacen[fechas[fechas.length - 1]];
}

export function getCurrentAirportOcupation(estadoAlmacen: HistoricoValores, fecha: Date | undefined): number {
	if (fecha === undefined) {
		//console.log("Fecha no definida");
		return 0;
	}

	const fechas = Object.keys(estadoAlmacen);

	if (fechas.length === 0) {
		//console.log("No hay fechas para este estadoAlmacen");
		return 0; //basicamente no tiene historico
	}

	// Si la fecha dada es menor que la primera fecha en el arreglo
	if (fecha < new Date(fechas[0])) {
		//console.log("Fecha menor a la primera fecha en el arreglo");
		return 0;
	}

	if (fecha >= new Date(fechas[fechas.length - 1])) {
		return estadoAlmacen[fechas[fechas.length - 1]];
	}

	for (let i = 0; i < fechas.length - 1; i++) {
		if (fecha >= new Date(fechas[i]) && fecha < new Date(fechas[i + 1])) {
			//console.log("Fecha entre dos fechas en el arreglo");
			return estadoAlmacen[fechas[i]];
		}
	}

	//console.log("Fecha mayor a la última fecha en el arreglo");
	// Si la fecha es mayor o igual a la última fecha en el arreglo
	return estadoAlmacen[fechas[fechas.length - 1]];
}

export function structureDataFromRespuestaAlgoritmo(data: RespuestaAlgoritmo) {
	const newFlights = data.vuelos
		.map((vuelo: Vuelo) => {
			const vueloActualizado = vuelo;
			vueloActualizado.fechaSalida = new Date(vuelo.fechaSalida);
			vueloActualizado.fechaLlegada = new Date(vuelo.fechaLlegada);
			vueloActualizado.anguloAvion = calculateAngle(
				[vuelo.planVuelo.ciudadOrigen.longitud, vuelo.planVuelo.ciudadOrigen.latitud],
				[vuelo.planVuelo.ciudadDestino.longitud, vuelo.planVuelo.ciudadDestino.latitud]
			);
			vueloActualizado.posicionesRuta = getTrayectory(vuelo);
			return vueloActualizado;
		})
		.filter((flight: Vuelo) => flight.capacidadUtilizada !== 0);

	return {
		db_vuelos: newFlights,
		db_envios: [],
		db_estadoAlmacen: data.estadoAlmacen,
	};
}

export function structureEnviosFromPaquetes(_paquetes: Paquete[]) {
	const envioMap = new Map<number, Envio>();

	for (const paquete of _paquetes) {
		const envio = paquete.envio;

		if (envioMap.has(envio.id) === false) {
			envio.fechaLimiteEntrega = new Date(envio.fechaLimiteEntrega);
			envio.fechaRecepcion = new Date(envio.fechaRecepcion);
			envio.paquetes = [];
			//envio.cantidadPaquetes = 0;
			envioMap.set(envio.id, envio);
		}

		const currentEnvio = envioMap.get(envio.id)!;
		currentEnvio.paquetes.push(paquete);
		//currentEnvio.cantidadPaquetes++;
	}

	const newEnviosNoDuplicates = Array.from(envioMap.values());

	// newEnviosNoDuplicates.map((envio) => {
	// 	if (envio.cantidadPaquetes !== envio.paquetes.length) {
	// 		const missingPaquetes = envio.cantidadPaquetes - envio.paquetes.length;

	// 		for (let i = 0; i < missingPaquetes; i++) {
	// 			envio.paquetes.push({
	// 				//@ts-ignore
	// 				isFill: true,
	// 			});
	// 		}

	// 		// console.log(`Envio ${envio.id} tiene ${missingPaquetes} paquetes faltantes`)
	// 	}
	// });

	return {
		db_envios: newEnviosNoDuplicates,
	};
}

export function getAirportHashmap(aeropuertos: Aeropuerto[]) {
	const airportMap = new Map<string, Aeropuerto>();

	for (const aeropuerto of aeropuertos) {
		airportMap.set(aeropuerto.ubicacion.id, aeropuerto);
	}

	return airportMap;
}

export function getPorcentajeOcupacionAeropuertos(
	aeropuertosMap: Map<string, Aeropuerto> | null,
	estadoAlmacen: EstadoAlmacen | null,
	currentTime: Date | undefined
) {
	//esta funcion recorre el estado almacen de todos los aeropuertos, sacando su porcentaje de ocupacion actual. luego, suma todos los porcentajes y los divide por la cantidad de aeropuertos
	if (aeropuertosMap === null || estadoAlmacen === null || currentTime === undefined) return 0;

	let totalOcupacion = 0;
	const arrayOcupaciones = [];

	const uso_historico = estadoAlmacen.uso_historico;

	for (const [key, value] of Object.entries(uso_historico)) {
		const currentOcupation = getCurrentAirportOcupation(value, currentTime);
		const currentAirport = aeropuertosMap.get(key);

		if (currentAirport === undefined) {
			throw new Error(`No se encontró aeropuerto con id ${key}`);
		}

		arrayOcupaciones.push((currentOcupation / currentAirport.capacidadMaxima) * 100);
		totalOcupacion += (currentOcupation / currentAirport.capacidadMaxima) * 100;
	}

	return (totalOcupacion / aeropuertosMap.size).toFixed(2);
}

export function getPorcentajeOcupacionVuelos(vuelos: Vuelo[], currentTime: Date | undefined) {
	if (currentTime === undefined) return 0;

	if (vuelos.length === 0) return 0;

	let totalOcupacion = 0;

	for (const vuelo of vuelos) {
		const currentOcupation = vuelo.capacidadUtilizada;
		const maxOcupation = vuelo.planVuelo.capacidadMaxima;

		totalOcupacion += (currentOcupation / maxOcupation) * 100;
	}

	return (totalOcupacion / vuelos.length).toFixed(2);
}

export function getPackagesFromAirport(estadoAlmacen: EstadoAlmacen | null, idAeropuerto: string, currentTime: Date | undefined, data: Paquete[]) {
	if (estadoAlmacen === null) return data;
	if (estadoAlmacen.uso_historico[idAeropuerto] === undefined) return data;
	if (currentTime === undefined) return data;
	if (data.length === 0) return [] as Paquete[];

	// we get the current airport ocupation
	const currentOcupation = getCurrentAirportOcupation(estadoAlmacen.uso_historico[idAeropuerto], currentTime);

	if (currentOcupation === data.length) return data;

	let new_data = [...data];

	if (currentOcupation > new_data.length) {
		const missingPackages = currentOcupation - new_data.length;

		const shuffled_data = new_data.sort(() => 0.5 - Math.random());
		const selectedPackages = shuffled_data.slice(0, missingPackages);

		for (let i = 0; i < missingPackages; i++) {
			new_data.push(selectedPackages[i]);
		}
		// console.log(`Original airport ocupation was ${currentOcupation}, added ${missingPackages} packages`);
	} else {
		new_data = new_data.slice(0, currentOcupation);
		// console.log(`Original airport ocupation was ${currentOcupation}, removed ${data.length - currentOcupation} packages`);
	}

	return new_data;
}
