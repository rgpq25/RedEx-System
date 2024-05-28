"use client";
import { useEffect, useState } from "react";
import Sidebar from "@/app/_components/sidebar";
import Map from "@/components/map/map";

import { envios } from "@/lib/sample";
import { Aeropuerto, Envio, RespuestaAlgoritmo, Simulacion, Vuelo } from "@/lib/types";
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
import { Button } from "@/components/ui/button";
import { CircleStop, Play, SkipBack, SkipForward } from "lucide-react";
import useMapModals from "@/components/hooks/useMapModals";

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
	const mapModalAttributes = useMapModals();
	const { currentTime, setCurrentTime, zoomToAirport, lockToFlight } = attributes;
	const { openFlightModal, openAirportModal } = mapModalAttributes;

	const [isModalOpen, setIsModalOpen] = useState(true);
	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [simulation, setSimulation] = useState<Simulacion | undefined>(undefined);
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
			toast.error("Error al cargar aeropuertos");
		}
	);

	const onSimulationRegister = async (simulacion: Simulacion) => {
		setSimulation(simulacion);

		//Connect to the socket
		const socket = new WebSocket("ws://localhost:8080/websocket");
		const client = new Client({
			webSocketFactory: () => socket,
		});

		client.onConnect = () => {
			console.log("Connected to WebSocket");
			client.subscribe("/algoritmo/respuesta", (msg) => {
				const data: RespuestaAlgoritmo = JSON.parse(msg.body);

				const simulation = data.simulacion;
				const fechaInicioSistema: Date = new Date(simulation.fechaInicioSistema);
				const fechaInicioSim: Date = new Date(simulation.fechaInicioSim);
				const multiplicadorTiempo: number = simulation.multiplicadorTiempo;

				const currentSimTime = new Date(
					fechaInicioSim.getTime() +
						multiplicadorTiempo * (new Date().getTime() - fechaInicioSistema.getTime())
				);

				setCurrentTime(currentSimTime, fechaInicioSistema, fechaInicioSim, multiplicadorTiempo);

				console.log("MENSAJE DE /algoritmo/respuesta: ", data);

				const newFlights = data.vuelos.map((vuelo: Vuelo) => {
					const vueloActualizado = vuelo;
					vueloActualizado.fechaSalida = new Date(vuelo.fechaSalida);
					vueloActualizado.fechaLlegada = new Date(vuelo.fechaLlegada);
					return vueloActualizado;
				});

				setFlights(newFlights);
			});
			client.subscribe("/algoritmo/estado", (msg) => {
				console.log("MENSAJE DE /algoritmo/estado: ", msg.body);
			});
		};

		client.activate();
		setClient(client);

		//Call api to run algorithm
		await api(
			"GET",
			"http://localhost:8080/back/simulacion/runAlgorithm/" + simulacion.id,
			(data) => {
				console.log(data);
			},
			(error) => {
				console.log(error);
			}
		);

	};

	useEffect(() => {
		return () => {
			if (client) {
				client.deactivate();
			}
		};
	},[])

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
					<div className="flex items-center gap-5">
						<PlaneLegend />
						<div className="flex items-center gap-1">
							<Button size={"icon"}>
								<CircleStop className="w-5 h-5" />
							</Button>
							{/* <Button size={"icon"}>
								<SkipBack className="w-5 h-5" />
							</Button> */}

							<Button size={"icon"}>
								<Play className="w-5 h-5" />
							</Button>
							{/* <Button size={"icon"}>
								<SkipForward className="w-5 h-5" />
							</Button> */}
						</div>
					</div>
				</div>

				<section className="relative flex-1 mt-[8px] overflow-hidden">
					<Map
						isSimulation={true}
						mapModalAttributes={mapModalAttributes}
						attributes={attributes}
						className="h-full w-full"
						airports={airports}
						flights={flights}
						simulation={simulation}
					/>
					<Sidebar
						envios={envios}
						vuelos={flights}
						aeropuertos={airports}
						onClickEnvio={(envio: Envio) => {
							toast.error("Pendiente de implementar");
						}}
						onClicksAeropuerto={{
							onClickLocation: (aeropuerto: Aeropuerto) => {
								zoomToAirport(aeropuerto)
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
				</section>
			</MainContainer>
		</>
	);
}

export default SimulationPage;
