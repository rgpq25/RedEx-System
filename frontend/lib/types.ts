export type PackageStatusName =
    | "En almacen origen"
    | "Volando"
    | "En espera"
    | "En almacen destino"
    | "Entregado";

export type PackageStatusVariant = "gray" | "blue" | "yellow" | "purple" | "green";

export type RowPackageType = {
    id: string;
    origin: string;
    currentLocation: string;
    destination: string;
    statusName: PackageStatusName;
    statusVariant: PackageStatusVariant;
};

export type RowPackageRouteType = {
    id: string;
    origin: string;
    timeOrigin: string | null;
    destination: string;
    timeDestination: string | null;
    isCurrent: boolean;
}

export type RoleType = "admin" | "operator" | "user" | null;

export enum Operacion {
    Envios = "Envios",
    Aeropuertos = "Aeropuertos",
    Vuelos = "Vuelos"
}

export type Coordenadas = {
    latitud: number;
    longitud: number;
};

export type Ubicacion = {
    id: number;
    coordenadas: {
        latitud: number;
        longitud: number;
    };
    continente: string;
    pais: string;
    ciudad: string;
    ciudad_abreviada: string;
    zona_horaria: string;
}

export type Aeropuerto = {
    id: number;
    ubicacion: Ubicacion;
    capacidad_maxima: number;
}

export type PlanVuelo = {
    id: number;
    ubicacion_origen: Ubicacion;
    ubicacion_destino: Ubicacion;
    hora_ciudad_origen: string;
    hora_ciudad_destino: string;
    capacidad_maxima: number;
}

export type Vuelo = {
    id: number;
    plan_vuelo: PlanVuelo;
    fecha_origen: Date;
    fecha_destino: Date;
    coordenadas_actual : Coordenadas;
    tiempo_estimado: number;
    capacidad_utilizada: number;
    estado: string;
}

export type PlanRuta = {
    id: number;
    ubicacion_origen: Ubicacion;
    ubicacion_destino: Ubicacion;
    fecha_inicio: Date;
    fecha_fin: Date;
    vuelos: Vuelo[];
}

export type Paquete = {
    id: number;
    aeropuerto_actual: Aeropuerto;
    entregado: boolean;
    envio: Envio;
    fecha_entrega: string;
}

export type Envio = {
    id: number;
    ubicacion_origen: Ubicacion;
    ubicacion_destino: Ubicacion;
    fecha_recepcion: Date;
    fecha_limite_entrega: Date;
    estado: string;
    cantidad_paquetes: number;
    codigo_seguridad: string;
}