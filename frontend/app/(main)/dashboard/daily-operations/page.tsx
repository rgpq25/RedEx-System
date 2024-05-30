"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import Map from "@/components/map/map";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { envios } from "@/lib/sample";
import { Aeropuerto, Envio, EstadoAlmacen, RespuestaAlgoritmo, Vuelo } from "@/lib/types";
import { Clock } from "lucide-react";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../../_components/main-container";
import { useEffect, useState } from "react";
import useApi from "@/components/hooks/useApi";
import { toast } from "sonner";
import { Client } from "@stomp/stompjs";
import { api } from "@/lib/api";
import useMapModals from "@/components/hooks/useMapModals";

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
	const { openFlightModal, openAirportModal } = mapModalAttributes;

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [estadoAlmacen, setEstadoAlmacen] = useState<EstadoAlmacen | null>(null);

	const [client, setClient] = useState<Client | null>(null);

	const { isLoading } = useApi(
		"GET",
		"http://localhost:8080/back/aeropuerto/",
		(data: Aeropuerto[]) => {
			console.log(data);
			setAirports(data);
		},
		(error) => {
			console.log(error);
		}
	);

	useEffect(() => {
		async function getData() {
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

					const newFlights = data.vuelos
						.map((vuelo: Vuelo) => {
							const vueloActualizado = vuelo;
							vueloActualizado.fechaSalida = new Date(vuelo.fechaSalida);
							vueloActualizado.fechaLlegada = new Date(vuelo.fechaLlegada);
							return vueloActualizado;
						})
						.filter((flight: Vuelo) => flight.capacidadUtilizada !== 0);

					setFlights(newFlights);
					setEstadoAlmacen(data.estadoAlmacen);
				});
				client.subscribe("/algoritmo/diaDiaEstado", (msg) => {
					console.log("MENSAJE DE /algoritmo/diaDiaEstado: ", msg.body);
				});
			};

			client.activate();
			setClient(client);

			await api(
				"GET",
				"http://localhost:8080/back/operacionesDiaDia/diaDiaRespuesta",
				(data: RespuestaAlgoritmo) => {
					setCurrentTimeNoSimulation();

					console.log(data);
					console.log(typeof data)
					if (data === null) {
						console.log("Data devuelta es NULL");
						return;
					}

					const newFlights = data.vuelos
						.map((vuelo: Vuelo) => {
							const vueloActualizado = vuelo;
							vueloActualizado.fechaSalida = new Date(vuelo.fechaSalida);
							vueloActualizado.fechaLlegada = new Date(vuelo.fechaLlegada);
							return vueloActualizado;
						})
						.filter((flight: Vuelo) => flight.capacidadUtilizada !== 0);

					setFlights(newFlights);
					setEstadoAlmacen(data.estadoAlmacen);
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
		<MainContainer>
			<BreadcrumbCustom items={breadcrumbItems} />
			<div className="flex flex-row justify-between items-center">
				<div className="flex flex-row gap-4 items-center ">
					<h1 className="text-4xl font-bold font-poppins">Operaciones día a día</h1>
					<CurrentTime currentTime={currentTime} />
				</div>
				<PlaneLegend />
			</div>
			<section className="relative mt-[10px] flex-1 flex overflow-hidden">
				<Sidebar
					aeropuertos={airports}
					envios={envios}
					vuelos={flights}
					onClickEnvio={(envio: Envio) => {
						toast.error("Pendiente de implementar");
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
					className="h-full w-full"
					airports={airports}
					flights={flights}
					estadoAlmacen={estadoAlmacen}
					simulation={undefined}
				/>
			</section>
		</MainContainer>
	);
}
export default DailyOperationsPage;
