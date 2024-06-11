//@ts-ignore
import { Envio, HistoricoValores, Paquete, RespuestaAlgoritmo, Vuelo } from "./types";

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

	const originCoordinate = [vuelo.planVuelo.ciudadOrigen.longitud, vuelo.planVuelo.ciudadOrigen.latitud] as [
		number,
		number
	];
	const destinationCoordinate = [vuelo.planVuelo.ciudadDestino.longitud, vuelo.planVuelo.ciudadDestino.latitud] as [
		number,
		number
	];

	const distance = Math.sqrt(
		Math.pow(destinationCoordinate[0] - originCoordinate[0], 2) +
			Math.pow(destinationCoordinate[1] - originCoordinate[1], 2)
	);

	steps = Math.floor(distance / 2);

	//we get 20 steps from the origin coordinate to the destination coordinate and return them in an array
	for (let i = 0; i < steps; i++) {
		const x = originCoordinate[0] + ((destinationCoordinate[0] - originCoordinate[0]) * i) / steps;
		const y = originCoordinate[1] + ((destinationCoordinate[1] - originCoordinate[1]) * i) / steps;
		dotPositions.push([x, y]);
	}
	return dotPositions;
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
			return vueloActualizado;
		})
		.filter((flight: Vuelo) => flight.capacidadUtilizada !== 0);

	// const newEnvios: Envio[] = data.paquetes.map((paquete: Paquete) => {
	// 	const _envio = paquete.envio;
	// 	_envio.fechaLimiteEntrega = new Date(_envio.fechaLimiteEntrega);
	// 	_envio.fechaRecepcion = new Date(_envio.fechaRecepcion);
	// 	return _envio;
	// });

	//remove duplicates from newEnvios
	// const newEnviosSet = new Set(newEnvios.map((envio) => envio));
	// const newEnviosNoDuplicates = Array.from(newEnviosSet);

	//assign data.paquetes to its corresponding envio
	// newEnviosNoDuplicates.forEach((envio) => {
	// 	envio.paquetes = data.paquetes.filter((paquete) => paquete.envio.id === envio.id) || [];
	// 	envio.cantidadPaquetes = envio.paquetes.length;
	// 	if(envio.cantidadPaquetes > 1) console.log("Envio con mas de un paquete", envio);
	// });

	return {
		db_vuelos: newFlights,
		db_envios: [],
		db_estadoAlmacen: data.estadoAlmacen,
	};
}



export function structureEnviosFromPaquetes(_paquetes: Paquete[]) {
	const envioMap = new Map<number, Envio>();

	_paquetes.forEach((paquete: Paquete)=>{
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

	})

	const newEnviosNoDuplicates = Array.from(envioMap.values());

	return {
		db_envios: newEnviosNoDuplicates
	};
}
