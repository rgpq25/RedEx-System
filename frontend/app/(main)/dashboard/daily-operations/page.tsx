"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { Aeropuerto, Envio, EstadoAlmacen, Paquete, RespuestaAlgoritmo, RespuestaDiaDia, Vuelo } from "@/lib/types";
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
import { Plane, Warehouse, ZoomIn, ZoomOut } from "lucide-react";
import { useFilteredFlightsContext } from "@/components/contexts/flights-filter";
import AverageOcupation from "../../simulation/_components/average-ocupation";
import AirplaneRouteHandler from "@/app/_components/airplane-route-handler";
import { Button } from "@/components/ui/button";
import ModalColapseDaily from "./components/modal-colapse-daily";

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
	const {
		currentTime,
		setCurrentTimeNoSimulation,
		zoomToAirport,
		lockToFlight,
		isAllRoutesVisible,
		setIsAllRoutesVisible,
		zoomInSlightly,
		zoomOutSlightly,
		pauseSimulationOnlyFrontend,
	} = attributes;
	const { openFlightModal, openAirportModal, openEnvioModal } = mapModalAttributes;

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [airportsHash, setAirportsHash] = useState<Map<string, Aeropuerto> | null>(null);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [envios, setEnvios] = useState<Envio[]>([]);
	const [estadoAlmacen, setEstadoAlmacen] = useState<EstadoAlmacen | null>(null);

	const [client, setClient] = useState<Client | null>(null);

	const [isLoadingFirstTime, setIsLoadingFirstTime] = useState(false);
	const [isModalColapseDailyOperations, setIsModalColapseDailyOperations] = useState(false);

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
					console.log("MENSAJE DE /algoritmo/diaDiaRespuesta: ", JSON.parse(msg.body));
					const data: RespuestaAlgoritmo = JSON.parse(msg.body);

					if (data.iniciandoPrimeraPlanificacionDiaDia === false) {
						setIsLoadingFirstTime(false);
					}

					if (data.correcta === false) {
						console.log("Se llego al colapso");
						setIsModalColapseDailyOperations(true);
						pauseSimulationOnlyFrontend();
						//return;
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
							console.log("Se fetchearon los envios en /algoritmo/diaDiaRespuesta");
							console.log("Envios: ", db_envios);
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

			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/diaDiaRespuesta`,
				async (data: RespuestaDiaDia) => {
					console.log("DATA DE operacionesDiaDia/diaDiaRespuesta: ", data);
					setCurrentTimeNoSimulation(new Date(data.fechaActualDiaDia));

					if (data.iniciandoPrimeraPlanificacionDiaDia === true) {
						setIsLoadingFirstTime(true);
						return;
					}

					if (data.correcta === false) {
						console.log("Se llego al colapso");
						setIsModalColapseDailyOperations(true);
						pauseSimulationOnlyFrontend();
						// return;
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
				},
				(error) => {
					console.log(error);
				}
			);

			// const interval = setInterval(async () => {
			// 	let db_envios_inter: Envio[] = [];
			// 	await api(
			// 		"GET",
			// 		`${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/obtenerEnvios`,
			// 		(data: Envio[]) => {
			// 			db_envios_inter = data.map((envio) => {
			// 				envio.fechaLimiteEntrega = new Date(envio.fechaLimiteEntrega);
			// 				envio.fechaRecepcion = new Date(envio.fechaRecepcion);
			// 				return envio;
			// 			});
			// 		},
			// 		(error) => {
			// 			console.log(`Error from /back/operacionesDiaDia/obtenerEnvios: `, error);
			// 			db_envios_inter = [];
			// 		}
			// 	);
			// 	console.log(" ===== Finished fetching envios (from interval) ===== ");
			// 	setEnvios(db_envios_inter);
			// }, 1000 * 60);

			// return () => {
			// 	clearInterval(interval);
			// };
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

	useEffect(() => {
		//if the current time has 05 seconds, then we fetch the envios

		async function getEnviosFromBack() {
			let db_envios_inter: Envio[] = [];
			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/obtenerEnvios`,
				(data: Envio[]) => {
					db_envios_inter = data.map((envio) => {
						envio.fechaLimiteEntrega = new Date(envio.fechaLimiteEntrega);
						envio.fechaRecepcion = new Date(envio.fechaRecepcion);
						return envio;
					});
				},
				(error) => {
					console.log(`Error from /back/operacionesDiaDia/obtenerEnvios: `, error);
					db_envios_inter = [];
				}
			);
			console.log(" ===== Finished fetching envios (at 05 seconds) ===== ");
			setEnvios(db_envios_inter);
		}

		if (currentTime && (currentTime.getSeconds() === 5 || currentTime.getSeconds() === 0)) {
			getEnviosFromBack();
		}
	}, [currentTime]);

	const filtered_vuelos = useMemo(() => {
		if (currentTime === undefined) return [];
		const current_flights = flights.filter((vuelo) => vuelo.fechaSalida <= currentTime && vuelo.fechaLlegada >= currentTime);
		return getFilteredFlights(current_flights);
	}, [flights, currentTime, search, continentesFilter, paisOrigenFilter, paisDestinoFilter, rangoCapacidadFilter, minCapacidad, maxCapacidad]);

	return (
		<>
			<ModalColapseDaily open={isModalColapseDailyOperations} onOpenChange={(isOpen) => setIsModalColapseDailyOperations(isOpen)} />
			<MainContainer className="relative">
				<MapHeader>
					<BreadcrumbCustom
						items={breadcrumbItems}
						onClickAnyLink={async () => {
							console.log("Desactivando conexion a socket");

							if (client) {
								client.deactivate();
								client.forceDisconnect();
							}
						}}
					/>
					<div className="flex flex-row gap-4 items-center ">
						<h1 className="text-4xl font-bold font-poppins">Operaciones día a día</h1>
						<CurrentTime currentTime={currentTime} />
					</div>
				</MapHeader>

				<PlaneLegend className="absolute bottom-16 right-14 z-[50]" />

				<AirplaneRouteHandler
					className="absolute top-28 right-14 z-[20]"
					isAllRoutesVisible={isAllRoutesVisible}
					setIsAllRoutesVisible={setIsAllRoutesVisible}
				/>

				<div className="absolute top-[158px] right-14 z-[20] flex flex-row gap-1">
					<Button size={"icon"} onClick={() => zoomInSlightly(0.1)}>
						<ZoomIn className="w-5 h-5 shrink-0" />
					</Button>
					<Button size={"icon"} onClick={() => zoomOutSlightly(0.1)}>
						<ZoomOut className="w-5 h-5 shrink-0" />
					</Button>
				</div>

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
		</>
	);
}
export default DailyOperationsPage;
