"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { Aeropuerto, Envio, EstadoAlmacen, Paquete, RespuestaAlgoritmo, Vuelo } from "@/lib/types";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../../_components/main-container";
import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import { api } from "@/lib/api";
import useMapModals from "@/components/hooks/useMapModals";
import MapHeader from "../../_components/map-header";
import { calculateAngle, structureDataFromRespuestaAlgoritmo } from "@/lib/map-utils";
import { Map } from "@/components/map/map";

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
	const attributes = useMapZoom();
	const mapModalAttributes = useMapModals();
	const { currentTime, setCurrentTimeNoSimulation, zoomToAirport, lockToFlight } = attributes;
	const { openFlightModal, openAirportModal, openEnvioModal } = mapModalAttributes;

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [envios, setEnvios] = useState<Envio[]>([]);
	const [estadoAlmacen, setEstadoAlmacen] = useState<EstadoAlmacen | null>(null);

	const [client, setClient] = useState<Client | null>(null);

	const [isLoadingFirstTime, setIsLoadingFirstTime] = useState(false);

	useEffect(() => {
		async function getData() {
			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
				(data: Aeropuerto[]) => {
					console.log("Fetched airport data succesfully");
					setAirports(data);
				},
				(error) => {
					console.log(error);
				}
			);

			//Connect to the socket
			const socket = new WebSocket("ws://localhost:8080/websocket");
			const client = new Client({
				webSocketFactory: () => socket,
			});

			client.onConnect = () => {
				console.log("Connected to WebSocket");
				client.subscribe("/algoritmo/diaDiaRespuesta", (msg) => {
					console.log("MENSAJE DE /algoritmo/diaDiaRespuesta: ", JSON.parse(msg.body));
					const data: RespuestaAlgoritmo = JSON.parse(msg.body);

					if (data.iniciandoPrimeraPlanificacionDiaDia === false) {
						setIsLoadingFirstTime(false);
					}

					const { db_vuelos, db_envios, db_estadoAlmacen } = structureDataFromRespuestaAlgoritmo(data);

					setFlights(db_vuelos);
					setEnvios(db_envios);
					setEstadoAlmacen(db_estadoAlmacen);
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
				(data: RespuestaAlgoritmo) => {
					console.log("DATA DE operacionesDiaDia/diaDiaRespuesta: ", data);
					setCurrentTimeNoSimulation();

					if (data.iniciandoPrimeraPlanificacionDiaDia === true) {
						setIsLoadingFirstTime(true);
						return;
					}

					const { db_vuelos, db_envios, db_estadoAlmacen } = structureDataFromRespuestaAlgoritmo(data);

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

	return (
		<MainContainer className="relative">
			<MapHeader>
				<BreadcrumbCustom items={breadcrumbItems} />
				<div className="flex flex-row gap-4 items-center ">
					<h1 className="text-4xl font-bold font-poppins">Operaciones día a día</h1>
					<CurrentTime currentTime={currentTime} />
				</div>
			</MapHeader>

			<PlaneLegend className="absolute top-10 right-14 z-[50]" />

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
				vuelos={flights}
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
				//airports={airports}
				flights={flights}
				estadoAlmacen={estadoAlmacen}
				simulation={undefined}
			/>
		</MainContainer>
	);
}
export default DailyOperationsPage;
