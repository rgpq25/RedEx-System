"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import Map from "@/components/map/map";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { envios } from "@/lib/sample";
import { Aeropuerto, Envio, RespuestaAlgoritmo, Vuelo } from "@/lib/types";
import { Clock } from "lucide-react";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../../_components/main-container";
import { useEffect, useState } from "react";
import useApi from "@/components/hooks/useApi";
import { toast } from "sonner";
import { Client } from "@stomp/stompjs";
import { api } from "@/lib/api";

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
	const { centerLatitude, centerLongitude, currentTime, setCurrentTime, setCurrentTimeNoSimulation, lockInFlight, unlockFlight, zoom, zoomIn } = attributes;

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [currentAirportModal, setCurrentAirportModal] = useState<Aeropuerto | undefined>(undefined);
	const [currentFlightModal, setCurrentFlightModal] = useState<Vuelo | undefined>(undefined);

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

					const newFlights = data.vuelos.map((vuelo: Vuelo) => {
						const vueloActualizado = vuelo;
						vueloActualizado.fechaSalida = new Date(vuelo.fechaSalida);
						vueloActualizado.fechaLlegada = new Date(vuelo.fechaLlegada);
						return vueloActualizado;
					});

					setFlights(newFlights);
				});
				client.subscribe("/algoritmo/diaDiaEstado", (msg) => {
					console.log("MENSAJE DE /algoritmo/diaDiaEstado: ", msg.body);
				});
			};

			client.activate();

			await api(
				"GET",
				"http://localhost:8080/back/operacionesDiaDia/diaDiaRespuesta",
				(data: RespuestaAlgoritmo) => {
					setCurrentTimeNoSimulation();
					console.log(data);
					const newFlights = data.vuelos.map((vuelo: Vuelo) => {
						const vueloActualizado = vuelo;
						vueloActualizado.fechaSalida = new Date(vuelo.fechaSalida);
						vueloActualizado.fechaLlegada = new Date(vuelo.fechaLlegada);
						return vueloActualizado;
					});

					setFlights(newFlights);
				},
				(error) => {
					console.log(error);
				}
			);

			return () => {
				client.deactivate();
			};
		}

		getData();
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
							unlockFlight();
							zoomIn([aeropuerto.ubicacion.longitud, aeropuerto.ubicacion.latitud] as [number, number]);
						},
						onClickInfo: (aeropuerto: Aeropuerto) => {
							setCurrentAirportModal(aeropuerto);
						},
					}}
					onClickVuelo={(vuelo: Vuelo) => {
						lockInFlight(vuelo);
						setCurrentFlightModal(vuelo);
					}}
				/>
				<Map
					currentAirportModal={currentAirportModal}
					currentFlightModal={currentFlightModal}
					setCurrentAirportModal={setCurrentAirportModal}
					setCurrentFlightModal={setCurrentFlightModal}
					attributes={attributes}
					className="h-full w-full"
					airports={airports}
					flights={flights}
					simulation={undefined}
				/>
			</section>
		</MainContainer>
	);
}
export default DailyOperationsPage;
