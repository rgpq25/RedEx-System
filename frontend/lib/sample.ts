import { Aeropuerto, Envio, Ubicacion, Vuelo } from "./types";

const ubicaciones: Ubicacion[] = [
	{
		id: 1,
		latitud: 40.7128,
		longitud: -74.006,
		continente: "América del Norte",
		pais: "Estados Unidos",
		ciudad: "Nueva York",
		ciudad_abreviada: "NY",
		zona_horaria: "GMT-4",
	},
	{
		id: 2,
		latitud: 34.0522,
		longitud: -118.2437,
		continente: "América del Norte",
		pais: "Estados Unidos",
		ciudad: "Los Ángeles",
		ciudad_abreviada: "LA",
		zona_horaria: "GMT-7",
	},
	{
		id: 3,
		latitud: 41.8781,
		longitud: -87.6298,
		continente: "América del Norte",
		pais: "Estados Unidos",
		ciudad: "Chicago",
		ciudad_abreviada: "CH",
		zona_horaria: "GMT-5",
	},
	{
		id: 4,
		latitud: 29.7604,
		longitud: -95.3698,
		continente: "América del Norte",
		pais: "Estados Unidos",
		ciudad: "Houston",
		ciudad_abreviada: "HO",
		zona_horaria: "GMT-5",
	},
	{
		id: 5,
		latitud: 33.4484,
		longitud: -112.074,
		continente: "América del Norte",
		pais: "Estados Unidos",
		ciudad: "Phoenix",
		ciudad_abreviada: "PH",
		zona_horaria: "GMT-7",
	},
	{
		id: 6,
		latitud: 39.7392,
		longitud: -104.9903,
		continente: "América del Norte",
		pais: "Estados Unidos",
		ciudad: "Denver",
		ciudad_abreviada: "DE",
		zona_horaria: "GMT-6",
	},
	{
		id: 7,
		latitud: 37.7749,
		longitud: -122.4194,
		continente: "América del Norte",
		pais: "Estados Unidos",
		ciudad: "San Francisco",
		ciudad_abreviada: "SF",
		zona_horaria: "GMT-7",
	},
];

const continentes: string[] = [
    "America del Sur",
    "Europa",
    "Asia",
];

const aeropuertos: Aeropuerto[] = [
	{
		id: 1,
		ubicacion: ubicaciones[0],
		capacidadMaxima: 100,
	},
	{
		id: 2,
		ubicacion: ubicaciones[1],
		capacidadMaxima: 200,
	},
	{
		id: 3,
		ubicacion: ubicaciones[2],
		capacidadMaxima: 300,
	},
	{
		id: 4,
		ubicacion: ubicaciones[3],
		capacidadMaxima: 400,
	},
	{
		id: 5,
		ubicacion: ubicaciones[4],
		capacidadMaxima: 500,
	},
	{
		id: 6,
		ubicacion: ubicaciones[5],
		capacidadMaxima: 600,
	},
	{
		id: 7,
		ubicacion: ubicaciones[6],
		capacidadMaxima: 700,
	},
];

const vuelos: Vuelo[] = [
	{
		id: 1,
		planVuelo: {
			id: 1,
			ubicacionOrigen: {
				id: "SLLP",
				continente: "America del Sur",
				pais: "Bolivia",
				ciudad: "La Paz",
				ciudadAbreviada: "lapa",
				zonaHoraria: "GMT-4",
				latitud: -16.5,
				longitud: -68.15,
			},
			ubicacionDestino: {
				id: "RJTT",
				continente: "Asia",
				pais: "Japon",
				ciudad: "Tokyo",
				ciudadAbreviada: "toky",
				zonaHoraria: "GMT+9",
				latitud: 35.6895,
				longitud: 139.6917,
			},
			horaCiudadOrigen: "10:00",
			horaCiudadDestino: "12:00",
			capacidadMaxima: 100,
		},
		fechaOrigen: new Date("2024-05-04T00:00:03"),
		fechaDestino: new Date("2024-05-10T00:00:03"),
		tiempoEstimado: 2,
		capacidadUtilizada: 50,
		estado: "En vuelo",
	},
	{
		id: 2,
		planVuelo: {
			id: 2,
			ubicacionOrigen: {
				id: "RKSI",
				continente: "Asia",
				pais: "Corea del Sur",
				ciudad: "Seul",
				ciudadAbreviada: "seul",
				zonaHoraria: "GMT+9",
				latitud: 37.5665,
				longitud: 126.978,
			},
			ubicacionDestino: {
				id: "SKBO",
				continente: "America del Sur",
				pais: "Colombia",
				ciudad: "Bogota",
				ciudadAbreviada: "bogo",
				zonaHoraria: "GMT-5",
				latitud: 4.711,
				longitud: -74.0721,
			},
			horaCiudadOrigen: "10:00",
			horaCiudadDestino: "12:00",
			capacidadMaxima: 200,
		},
		fechaOrigen: new Date("2024-05-04T00:00:03"),
		fechaDestino: new Date("2024-05-10T00:03:03"),
		tiempoEstimado: 2,
		capacidadUtilizada: 100,
		estado: "En vuelo",
	},
	{
		id: 3,
		planVuelo: {
			id: 3,
			ubicacionOrigen: {
				id: "EHAM",
				continente: "Europa",
				pais: "Holanda",
				ciudad: "Amsterdam",
				ciudadAbreviada: "amst",
				zonaHoraria: "GMT+2",
				latitud: 52.3676,
				longitud: 4.9041,
			},
			ubicacionDestino: {
				id: "SGAS",
				continente: "America del Sur",
				pais: "Paraguay",
				ciudad: "Asunción",
				ciudadAbreviada: "asun",
				zonaHoraria: "GMT-4",
				latitud: -25.2637,
				longitud: -57.5759,
			},
			horaCiudadOrigen: "10:00",
			horaCiudadDestino: "12:00",
			capacidadMaxima: 300,
		},
		fechaOrigen: new Date("2024-05-04T00:00:03"),
		fechaDestino: new Date("2024-05-10T00:15:03"),
		tiempoEstimado: 2,
		capacidadUtilizada: 150,
		estado: "En vuelo",
	},
];

const envios: Envio[] = [
	{
		id: 1,
		ubicacionOrigen: ubicaciones[0],
		ubicacionDestino: ubicaciones[1],
		fechaRecepcion: new Date("2024-05-04T00:00:03"),
		fechaLimiteEntrega: new Date("2024-05-07T00:00:03"),
		estado: "En camino",
		cantidadPaquetes: 50,
		codigoSeguridad: "123456",
		tiempoEntregaEstimada: "3 días",
	},
	{
		id: 2,
		ubicacionOrigen: ubicaciones[2],
		ubicacionDestino: ubicaciones[3],
		fechaRecepcion: new Date("2024-05-04T00:00:03"),
		fechaLimiteEntrega: new Date("2024-05-07T00:00:03"),
		estado: "En camino",
		cantidadPaquetes: 100,
		codigoSeguridad: "654321",
		tiempoEntregaEstimada: "3 días",
	},
	{
		id: 3,
		ubicacionOrigen: ubicaciones[4],
		ubicacionDestino: ubicaciones[5],
		fechaRecepcion: new Date("2024-05-04T00:00:03"),
		fechaLimiteEntrega: new Date("2024-05-07T00:00:03"),
		estado: "En camino",
		cantidadPaquetes: 150,
		codigoSeguridad: "987654",
		tiempoEntregaEstimada: "3 días",
	},
    {
        id: 4,
        ubicacionOrigen: ubicaciones[6],
        ubicacionDestino: ubicaciones[0],
        fechaRecepcion: new Date("2024-05-10T00:00:03"),
        fechaLimiteEntrega: new Date("2024-05-13T00:00:03"),
        estado: "En camino",
        cantidadPaquetes: 200,
        codigoSeguridad: "456789",
        tiempoEntregaEstimada: "3 días",
    }
];

export { ubicaciones, continentes, aeropuertos, vuelos, envios };
