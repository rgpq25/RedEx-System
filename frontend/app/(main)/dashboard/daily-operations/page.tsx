"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { Aeropuerto, Envio, EstadoAlmacen, Paquete, RespuestaAlgoritmo, Vuelo } from "@/lib/types";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../../_components/main-container";
import { useEffect, useMemo, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import { api } from "@/lib/api";
import useMapModals from "@/components/hooks/useMapModals";
import MapHeader from "../../_components/map-header";
import {
	calculateAngle,
	getAirportHashmap,
	getPorcentajeOcupacionAeropuertos,
	getPorcentajeOcupacionVuelos,
	structureDataFromRespuestaAlgoritmo,
	structureEnviosFromPaquetes,
} from "@/lib/map-utils";
import { Map } from "@/components/map/map";
import { Plane, Warehouse } from "lucide-react";
import { useFilteredFlightsContext } from "@/components/contexts/flights-filter";
import AverageOcupation from "../../simulation/_components/average-ocupation";

const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Acceso",
		link: "/security-code",
	},
	{
		label: "Dashboard",
		link: "/dashboard",
	},
	{
		label: "Operaciones día a día",
		link: "/dashboard/daily-operations",
	},
];

function DailyOperationsPage() {
	const {
		search,
		setSearch,
		hasSearchFilter,
		continentesFilter,
		setContinentesFilter,
		paisOrigenFilter,
		setPaisOrigenFilter,
		paisDestinoFilter,
		setPaisDestinoFilter,
		rangoCapacidadFilter,
		setRangoCapacidadFilter,
		minCapacidad,
		maxCapacidad,
		getFilteredFlights,
	} = useFilteredFlightsContext();

	const attributes = useMapZoom();
	const mapModalAttributes = useMapModals();
	const { currentTime, setCurrentTimeNoSimulation, zoomToAirport, lockToFlight } = attributes;
	const { openFlightModal, openAirportModal, openEnvioModal } = mapModalAttributes;

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [airportsHash, setAirportsHash] = useState<Map<string, Aeropuerto> | null>(null);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [envios, setEnvios] = useState<Envio[]>([]);
	const [estadoAlmacen, setEstadoAlmacen] = useState<EstadoAlmacen | null>(null);

	const [client, setClient] = useState<Client | null>(null);

	const [isLoadingFirstTime, setIsLoadingFirstTime] = useState(false);

	const hasRun = useRef(false);

	useEffect(() => {
		if (hasRun.current) return;
		hasRun.current = true;

		async function getData() {
			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
				(data: Aeropuerto[]) => {
					console.log("DATA DE /back/aeropuerto/: ", data);
					setAirports(data);
					const airportHash = getAirportHashmap(data);
					setAirportsHash(airportHash);
				},
				(error) => {
					console.log(error);
				}
			);

			//Connect to the socket
			const socket = new WebSocket(`${process.env.NEXT_PUBLIC_SOCKET}/websocket`);
			const client = new Client({
				webSocketFactory: () => socket,
			});

			client.onConnect = () => {
				console.log("Connected to WebSocket");
				client.subscribe("/algoritmo/diaDiaRespuesta", async (msg) => {
					// console.log("MENSAJE DE /algoritmo/diaDiaRespuesta: ", JSON.parse(msg.body));
					const data: RespuestaAlgoritmo = JSON.parse(msg.body);

					if (data.iniciandoPrimeraPlanificacionDiaDia === false) {
						setIsLoadingFirstTime(false);
					}

					let db_envios: Envio[] = [];
					await api(
						"GET",
						`${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/obtenerEnvios`,
						(data: Envio[]) => {
							db_envios = data.map((envio) => {
								envio.fechaLimiteEntrega = new Date(envio.fechaLimiteEntrega);
								envio.fechaRecepcion = new Date(envio.fechaRecepcion);
								return envio;
							});
						},
						(error) => {
							console.log(`Error from /back/operacionesDiaDia/obtenerEnvios: `, error);
							db_envios = [];
						}
					);

					const { db_vuelos, db_estadoAlmacen } = structureDataFromRespuestaAlgoritmo(data);

					setFlights(db_vuelos);
					setEnvios(db_envios);
					setEstadoAlmacen(db_estadoAlmacen);
					console.log("Se actualizó el mapa por mensaje de /algoritmo/diaDiaRespuesta");
				});
				client.subscribe("/algoritmo/diaDiaEstado", (msg) => {
					console.log("MENSAJE DE /algoritmo/diaDiaEstado: ", msg.body);
				});
			};

			client.activate();
			setClient(client);

			console.log("Getting data");
			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/diaDiaRespuesta`,
				async (data: RespuestaAlgoritmo) => {
					console.log("DATA DE operacionesDiaDia/diaDiaRespuesta: ", data);
					setCurrentTimeNoSimulation();

					if (data.iniciandoPrimeraPlanificacionDiaDia === true) {
						setIsLoadingFirstTime(true);
						return;
					}

					let _paquetes: Paquete[] = [];
					await api(
						"GET",
						`${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/obtenerPaquetes`,
						(data: Paquete[]) => {
							console.log("DATA DE operacionesDiaDia/obtenerPaquetes: ", data);
							_paquetes = [...data];
						},
						(error) => {
							console.log(`Error from /back/operacionesDiaDia/obtenerPaquetes: `, error);
							_paquetes = [];
						}
					);
					console.log(" ===== Finished fetching paquetes");

					const { db_envios } = structureEnviosFromPaquetes(_paquetes);
					const { db_vuelos, db_estadoAlmacen } = structureDataFromRespuestaAlgoritmo(data);

					setFlights(db_vuelos);
					setEnvios(db_envios);
					setEstadoAlmacen(db_estadoAlmacen);
				},
				(error) => {
					console.log(error);
				}
			);
		}

		getData();
	}, []);

	useEffect(() => {
		return () => {
			if (client) {
				client.deactivate();
			}
		};
	}, []);

	const filtered_vuelos = useMemo(() => {
		if (currentTime === undefined) return [];
		const current_flights = flights.filter((vuelo) => vuelo.fechaSalida <= currentTime && vuelo.fechaLlegada >= currentTime);
		return getFilteredFlights(current_flights);
	}, [flights, currentTime, search, continentesFilter, paisOrigenFilter, paisDestinoFilter, rangoCapacidadFilter, minCapacidad, maxCapacidad]);

	return (
		<MainContainer className="relative">
			<MapHeader>
				<BreadcrumbCustom items={breadcrumbItems} />
				<div className="flex flex-row gap-4 items-center ">
					<h1 className="text-4xl font-bold font-poppins">Operaciones día a día</h1>
					<CurrentTime currentTime={currentTime} />
				</div>
			</MapHeader>

			<PlaneLegend className="absolute bottom-16 right-14 z-[50]" />

			<AverageOcupation
				className="absolute top-10 right-14 z-[20]"
				airportsHash={airportsHash}
				currentTime={currentTime}
				estadoAlmacen={estadoAlmacen}
				filtered_vuelos={filtered_vuelos}
			/>

			{isLoadingFirstTime && (
				<>
					<div className="absolute top-1 bottom-3 left-3 right-3 bg-black z-[30] opacity-70 rounded-md flex justify-center items-center"></div>

					<div className="absolute top-1 bottom-3 left-3 right-3 flex flex-col justify-center items-center z-[100] gap-1">
						<img src="/plane_loading.gif" className="w-[10%] opacity-100" />
						<p className="font-bold text-md text-white">Iniciando primera planificacion</p>
					</div>
				</>
			)}

			<Sidebar
				aeropuertos={airports}
				envios={envios}
				vuelos={filtered_vuelos}
				estadoAlmacen={estadoAlmacen}
				onClicksEnvio={{
					onClickLocation: (envio: Envio) => {},
					onClickInfo: (envio: Envio) => {
						openEnvioModal(envio);
					},
				}}
				onClicksAeropuerto={{
					onClickLocation: (aeropuerto: Aeropuerto) => {
						zoomToAirport(aeropuerto);
					},
					onClickInfo: (aeropuerto: Aeropuerto) => {
						openAirportModal(aeropuerto);
					},
				}}
				onClickVuelo={(vuelo: Vuelo) => {
					lockToFlight(vuelo);
					openFlightModal(vuelo);
				}}
				tiempoActual={currentTime}
			/>
			<Map
				isSimulation={false}
				mapModalAttributes={mapModalAttributes}
				attributes={attributes}
				className="absolute top-1 bottom-3 left-3 right-3"
				flights={filtered_vuelos}
				estadoAlmacen={estadoAlmacen}
				simulation={undefined}
			/>
		</MainContainer>
	);
}
export default DailyOperationsPage;
