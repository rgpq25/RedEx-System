"use client";
import { useEffect, useState } from "react";
import Sidebar from "@/app/_components/sidebar";
import Map from "@/components/map/map";

type TabType = "weekly" | "colapse";

import { envios } from "@/lib/sample";
import { Aeropuerto, Envio, RespuestaAlgoritmo, Vuelo } from "@/lib/types";
import useMapZoom from "@/components/hooks/useMapZoom";
import { ModalIntro } from "./_components/modal-intro";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../_components/main-container";
import useApi from "@/components/hooks/useApi";
import { toast } from "sonner";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { Client } from "@stomp/stompjs";
import { api } from "@/lib/api";

const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Acceso",
		link: "/security-code",
	},
	{
		label: "Simulación",
		link: "/simulation",
	},
];

function SimulationPage() {
	const attributes = useMapZoom();
	const { currentTime, setCurrentTime, zoom, centerLongitude, centerLatitude, zoomIn, lockInFlight, unlockFlight } = attributes;

	const [isModalOpen, setIsModalOpen] = useState(true);
	const [currentAirportModal, setCurrentAirportModal] = useState<Aeropuerto | undefined>(undefined);
	const [currentFlightModal, setCurrentFlightModal] = useState<Vuelo | undefined>(undefined);

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [flights, setFlights] = useState<Vuelo[]>([]);

	const { isLoading } = useApi(
		"GET",
		"http://localhost:8080/back/aeropuerto/",
		(data: Aeropuerto[]) => {
			console.log(data);
			setAirports(data);
		},
		(error) => {
			console.log(error);
			toast.error("Error al cargar aeropuertos");
		}
	);

	const onSimulationRegister = async (idSimulacion: number) => {
		//Connect to the socket
		const socket = new WebSocket("ws://localhost:8080/websocket");
		const client = new Client({
			webSocketFactory: () => socket,
		});

		client.onConnect = () => {
			console.log("Connected to WebSocket");
			client.subscribe("/algoritmo/respuesta", (msg) => {
				const data : RespuestaAlgoritmo = JSON.parse(msg.body);

				const simulation = data.simulacion;
				const fechaInicioSistema: Date = new Date(simulation.fechaInicioSistema);
				const fechaInicioSim: Date = new Date(simulation.fechaInicioSim);
				const multiplicadorTiempo: number = simulation.multiplicadorTiempo;

				const currentSimTime = new Date(fechaInicioSim.getTime() + multiplicadorTiempo * (new Date().getTime() - fechaInicioSistema.getTime()));
				setCurrentTime(currentSimTime, fechaInicioSistema, fechaInicioSim, multiplicadorTiempo);

				// showNewMessage(JSON.parse(msg.body));
				
				console.log("MENSAJE DE /algoritmo/respuesta: ", data);

				const newFlights = data.vuelos
				//	.slice(0, 100);
				
				setFlights(newFlights.map((vuelo: Vuelo) => {
					const vueloActualizado = vuelo;
					vueloActualizado.fechaSalida = new Date(vuelo.fechaSalida);
					vueloActualizado.fechaLlegada = new Date(vuelo.fechaLlegada);
					return vueloActualizado;
				}));
			});
			client.subscribe("/algoritmo/estado", (msg) => {
				// showNewMessage(JSON.parse(msg.body));
				console.log("MENSAJE DE /algoritmo/estado: ", msg.body);
			});
		};

		client.activate();

		//Call api to run algorithm
		await api(
			"GET",
			"http://localhost:8080/back/simulacion/runAlgorithm/" + idSimulacion,
			(data) => {
				console.log(data);
			},
			(error) => {
				console.log(error);
			}
		);
	};

	// useEffect(()=>{
	// 	const stompCliente = new StompJs.Client({
	// 		webSocketFactory: () => new WebSocket('ws://localhost:8080/websocket')
	// 	});
	// },[])

	return (
		<>
			<ModalIntro
				isOpen={isModalOpen}
				setIsModalOpen={setIsModalOpen}
				onSimulationRegister={(idSimulacion) => onSimulationRegister(idSimulacion)}
			/>
			<MainContainer>
				<BreadcrumbCustom items={breadcrumbItems} />
				<div className="flex flex-row justify-between items-center">
					<div className="flex flex-row gap-4 items-center ">
						<h1 className="text-4xl font-bold font-poppins">Visualizador de simulación</h1>
						<CurrentTime currentTime={currentTime} />
					</div>
					<PlaneLegend />
				</div>

				<section className="relative flex-1 mt-[10px] overflow-hidden">
					<Map
						currentAirportModal={currentAirportModal}
						currentFlightModal={currentFlightModal}
						setCurrentAirportModal={setCurrentAirportModal}
						setCurrentFlightModal={setCurrentFlightModal}
						attributes={attributes}
						className="h-full w-full"
						airports={airports}
						flights={flights}
					/>
					<Sidebar
						envios={envios}
						vuelos={flights}
						aeropuertos={airports}
						onClickEnvio={(envio: Envio) => {
							console.log("PENDIENTE HACER ZOOM EN VUELO DONDE SE ENCUENTRA PAQUETE");
						}}
						onClicksAeropuerto={{
							onClickLocation: (aeropuerto: Aeropuerto) => {
								unlockFlight();
								zoomIn([aeropuerto.ubicacion.longitud, aeropuerto.ubicacion.latitud] as [
									number,
									number
								]);
							},
							onClickInfo: (aeropuerto: Aeropuerto) => {
								setCurrentAirportModal(aeropuerto);
							},
						}}
						onClickVuelo={(vuelo) => {
							lockInFlight(vuelo);
							setCurrentFlightModal(vuelo);
						}}
					/>
				</section>
			</MainContainer>
		</>
	);
}

export default SimulationPage;
