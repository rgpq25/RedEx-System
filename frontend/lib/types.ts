export type PackageStatusName = "En almacen origen" | "Volando" | "En espera" | "En almacen destino" | "Entregado";

export type AlmacenStatusName = "En almacen origen" | "Volando" | "En espera" | "En almacen destino" | "Entregado";

export type PackageStatusVariant = "gray" | "blue" | "yellow" | "purple" | "green";

export type AlmacenStatusVariant = "gray" | "blue" | "yellow" | "purple" | "green";

export type RowPackageType = {
    id: string;
    origin: string;
    currentLocation: string;
    destination: string;
    statusName: PackageStatusName;
    statusVariant: PackageStatusVariant;
};

export type RowShipmentType = {
    id: string;
    origin: string;
    dateTimeShipment: Date;
    destination: string;
    amountPackages: number;
};

export type RowAlmacenType = {
    id: string;
    packets: number;
    origin: string;
    destination: string;
    statusAlmacen: AlmacenStatusName;
    statusVariant: AlmacenStatusVariant;
};

export type RowVuelosType = {
    id: string;
    packets: number;
    origin: string;
    destination: string;
};

export type RowPackageRouteType = {
    id: string;
    origin: string;
    timeOrigin: string | null;
    destination: string;
    timeDestination: string | null;
    isCurrent: boolean;
};

export type RoleType = "admin" | "operario" | "user" | null;

export enum Operacion {
    Envios = "Envios",
    Aeropuertos = "Aeropuertos",
    Vuelos = "Vuelos",
}

export type Coordenadas = {
    latitud: number;
    longitud: number;
};

export type Ubicacion = {
    id: string;
    latitud: number;
    longitud: number;
    continente: string;
    pais: string;
    ciudad: string;
    ciudadAbreviada: string;
    zonaHoraria: string;
};

export type Aeropuerto = {
    id: number;
    ubicacion: Ubicacion;
    capacidadMaxima: number;
};

export type PlanVuelo = {
    id: number;
    ciudadOrigen: Ubicacion;
    ciudadDestino: Ubicacion;
    horaCiudadOrigen: string;
    horaCiudadDestino: string;
    capacidadMaxima: number;
};

export type Vuelo = {
    id: number;
    planVuelo: PlanVuelo;
    fechaSalida: Date;
    fechaLlegada: Date;
    anguloAvion: number;
    capacidadUtilizada: number;
    simulacionActual: Simulacion | null;
    posicionesRuta: [number,number][];
};

export type PlanRuta = {
    id: number;
    estado: string;
    paquete: Paquete;
    vuelos: Vuelo[];
};

export type Paquete = {
	id: number;
	aeropuertoActual: Aeropuerto;
	enAeropuerto: boolean;
	fechaRecepcion: Date;
	fechaDeEntrega: Date | null;
	entregado: boolean;
	envio: Envio;
	simulacionActual: Simulacion | null;
	planRutaActual: PlanRuta | null;
    planRutaVuelos?: Vuelo[];
	estado: string;
};

export type Envio = {
    id: number;
    ubicacionOrigen: Ubicacion;
    ubicacionDestino: Ubicacion;
    fechaRecepcion: Date;
    fechaLimiteEntrega: Date;
    estado: string;
    cantidadPaquetes: number;
    codigoSeguridad: string;
    paquetes: Paquete[];
};

export type Simulacion = {
    id: number;
    estado: number;
    multiplicadorTiempo: number;
    fechaInicioSistema: Date;
    fechaInicioSim: Date;
    fechaFinSim: Date;
    milisegundosPausados: number;
};

export type RespuestaAlgoritmo = {
	correcta: boolean;
	estadoAlmacen: EstadoAlmacen;
	planesRutas: PlanRuta[];
	simulacion: Simulacion;
	vuelos: Vuelo[];
	paquetes: Paquete[];
	iniciandoPrimeraPlanificacionDiaDia: boolean;
};

export type RespuestaEstado = {
    estado: string;
    simulacion: Simulacion;
}

export type EstadoAlmacen = {
    uso_historico: UsoHistorico;
};

export type UsoHistorico = {
    [key: string]: HistoricoValores;
};

export type HistoricoValores = {
    [key: string]: number;
};

export type ChipColors = "gray" | "blue" | "yellow" | "purple" | "green" | "red";

export type EstadoPaquete = {
    descripcion: "En aeropuerto origen" | "En aeropuerto destino" | "En vuelo" | "En espera" | "Entregado";
    color: ChipColors;
};