import { Aeropuerto, Envio, Ubicacion, Vuelo } from "./types";

const ubicaciones: Ubicacion[] = [
    {
        id: 1,
        coordenadas: {
            latitud: 40.7128,
            longitud: -74.0060
        },
        continente: "América del Norte",
        pais: "Estados Unidos",
        ciudad: "Nueva York",
        ciudad_abreviada: "NY",
        zona_horaria: "GMT-4"
    },
    {
        id: 2,
        coordenadas: {
            latitud: 34.0522,
            longitud: -118.2437
        },
        continente: "América del Norte",
        pais: "Estados Unidos",
        ciudad: "Los Ángeles",
        ciudad_abreviada: "LA",
        zona_horaria: "GMT-7"
    },
    {
        id: 3,
        coordenadas: {
            latitud: 41.8781,
            longitud: -87.6298
        },
        continente: "América del Norte",
        pais: "Estados Unidos",
        ciudad: "Chicago",
        ciudad_abreviada: "CH",
        zona_horaria: "GMT-5"
    },
    {
        id: 4,
        coordenadas: {
            latitud: 29.7604,
            longitud: -95.3698
        },
        continente: "América del Norte",
        pais: "Estados Unidos",
        ciudad: "Houston",
        ciudad_abreviada: "HO",
        zona_horaria: "GMT-5"
    },
    {
        id: 5,
        coordenadas: {
            latitud: 33.4484,
            longitud: -112.0740
        },
        continente: "América del Norte",
        pais: "Estados Unidos",
        ciudad: "Phoenix",
        ciudad_abreviada: "PH",
        zona_horaria: "GMT-7"
    },
    {
        id: 6,
        coordenadas: {
            latitud: 39.7392,
            longitud: -104.9903
        },
        continente: "América del Norte",
        pais: "Estados Unidos",
        ciudad: "Denver",
        ciudad_abreviada: "DE",
        zona_horaria: "GMT-6"
    },
    {
        id: 7,
        coordenadas: {
            latitud: 37.7749,
            longitud: -122.4194
        },
        continente: "América del Norte",
        pais: "Estados Unidos",
        ciudad: "San Francisco",
        ciudad_abreviada: "SF",
        zona_horaria: "GMT-7"
    },
];

const aeropuertos: Aeropuerto[] = [
    {
        id: 1,
        ubicacion: ubicaciones[0],
        capacidad_maxima: 100
    },
    {
        id: 2,
        ubicacion: ubicaciones[1],
        capacidad_maxima: 200
    },
    {
        id: 3,
        ubicacion: ubicaciones[2],
        capacidad_maxima: 300
    },
    {
        id: 4,
        ubicacion: ubicaciones[3],
        capacidad_maxima: 400
    },
    {
        id: 5,
        ubicacion: ubicaciones[4],
        capacidad_maxima: 500
    },
    {
        id: 6,
        ubicacion: ubicaciones[5],
        capacidad_maxima: 600
    },
    {
        id: 7,
        ubicacion: ubicaciones[6],
        capacidad_maxima: 700
    },
    {
        id: 8,
        ubicacion: ubicaciones[0],
        capacidad_maxima: 800
    },
    {
        id: 9,
        ubicacion: ubicaciones[1],
        capacidad_maxima: 900
    },
    {
        id: 10,
        ubicacion: ubicaciones[2],
        capacidad_maxima: 1000
    },
    {
        id: 11,
        ubicacion: ubicaciones[3],
        capacidad_maxima: 1100
    },
    {
        id: 12,
        ubicacion: ubicaciones[4],
        capacidad_maxima: 1200
    },
    {
        id: 13,
        ubicacion: ubicaciones[5],
        capacidad_maxima: 1300
    },
    {
        id: 14,
        ubicacion: ubicaciones[6],
        capacidad_maxima: 1400
    },
    {
        id: 15,
        ubicacion: ubicaciones[0],
        capacidad_maxima: 1500
    },
    {
        id: 16,
        ubicacion: ubicaciones[1],
        capacidad_maxima: 1600
    },
    {
        id: 17,
        ubicacion: ubicaciones[2],
        capacidad_maxima: 1700
    },
    {
        id: 18,
        ubicacion: ubicaciones[3],
        capacidad_maxima: 1800
    },
    {
        id: 19,
        ubicacion: ubicaciones[4],
        capacidad_maxima: 1900
    },
    {
        id: 20,
        ubicacion: ubicaciones[5],
        capacidad_maxima: 2000
    },
    {
        id: 21,
        ubicacion: ubicaciones[6],
        capacidad_maxima: 2100
    },
    {
        id: 22,
        ubicacion: ubicaciones[0],
        capacidad_maxima: 2200
    },
    {
        id: 23,
        ubicacion: ubicaciones[1],
        capacidad_maxima: 2300
    },
    {
        id: 24,
        ubicacion: ubicaciones[2],
        capacidad_maxima: 2400
    },
    {
        id: 25,
        ubicacion: ubicaciones[3],
        capacidad_maxima: 2500
    }
];

const vuelos: Vuelo[] = [
    {
        id: 1,
        plan_vuelo: {
            id: 1,
            ubicacion_origen: ubicaciones[0],
            ubicacion_destino: ubicaciones[1],
            hora_ciudad_origen: "10:00",
            hora_ciudad_destino: "12:00",
            capacidad_maxima: 100
        },
        fecha_origen: "2022-10-10",
        fecha_destino: "2022-10-10",
        coordenadas_actual: {
            latitud: 40.7128,
            longitud: -74.0060
        },
        tiempo_estimado: 2,
        capacidad_utilizada: 50,
        estado: "En vuelo"
    },
    {
        id: 2,
        plan_vuelo: {
            id: 2,
            ubicacion_origen: ubicaciones[2],
            ubicacion_destino: ubicaciones[3],
            hora_ciudad_origen: "10:00",
            hora_ciudad_destino: "12:00",
            capacidad_maxima: 200
        },
        fecha_origen: "2022-10-10",
        fecha_destino: "2022-10-10",
        coordenadas_actual: {
            latitud: 41.8781,
            longitud: -87.6298
        },
        tiempo_estimado: 2,
        capacidad_utilizada: 100,
        estado: "En vuelo"
    },
    {
        id: 3,
        plan_vuelo: {
            id: 3,
            ubicacion_origen: ubicaciones[4],
            ubicacion_destino: ubicaciones[5],
            hora_ciudad_origen: "10:00",
            hora_ciudad_destino: "12:00",
            capacidad_maxima: 300
        },
        fecha_origen: "2022-10-10",
        fecha_destino: "2022-10-10",
        coordenadas_actual: {
            latitud: 33.4484,
            longitud: -112.0740
        },
        tiempo_estimado: 2,
        capacidad_utilizada: 150,
        estado: "En vuelo"
    },
];

const envios: Envio[] = [
    {
        id: 1,
        ubicacion_origen: ubicaciones[0],
        ubicacion_destino: ubicaciones[1],
        fecha_recepcion: "2022-10-10",
        fecha_limite_entrega: "2022-10-10",
        estado: "En camino",
        cantidad_paquetes: 50,
        codigo_seguridad: "123456"
    },
    {
        id: 2,
        ubicacion_origen: ubicaciones[2],
        ubicacion_destino: ubicaciones[3],
        fecha_recepcion: "2022-10-10",
        fecha_limite_entrega: "2022-10-10",
        estado: "En camino",
        cantidad_paquetes: 100,
        codigo_seguridad: "654321"
    },
    {
        id: 3,
        ubicacion_origen: ubicaciones[4],
        ubicacion_destino: ubicaciones[5],
        fecha_recepcion: "2022-10-10",
        fecha_limite_entrega: "2022-10-10",
        estado: "En camino",
        cantidad_paquetes: 150,
        codigo_seguridad: "987654"
    },
];

export { ubicaciones, aeropuertos, vuelos, envios };