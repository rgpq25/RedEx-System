"use client";
import { useEffect, useState } from "react";
import Sidebar from "@/app/_components/sidebar";
import { Aeropuerto, Envio, EstadoAlmacen, Paquete, RespuestaAlgoritmo, RespuestaEstado, Simulacion, Vuelo } from "@/lib/types";
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
import { CircleStop, Pause, Play } from "lucide-react";
import useMapModals from "@/components/hooks/useMapModals";
import MapHeader from "../_components/map-header";
import { Map } from "@/components/map/map";
import { structureDataFromRespuestaAlgoritmo, structureEnviosFromPaquetes } from "@/lib/map-utils";
import ModalStopSimulation from "./_components/modal-stop-simulation";

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
	const { currentTime, elapsedRealTime, elapsedSimulationTime, setCurrentTime, zoomToAirport, lockToFlight, pauseSimulation, playSimulation } = attributes;
	const { openFlightModal, openAirportModal, openEnvioModal } = mapModalAttributes;

	const [isSimulationLoading, setIsSimulationLoading] = useState<boolean>(false);
	const [loadingMessages, setLoadingMessages] = useState<string[]>(["Cargando simulación"]);

	const [isPaused, setIsPaused] = useState<boolean>(false);
	const [isPauseLoading, setIsPauseLoading] = useState<boolean>(false);

	const [isModalOpen, setIsModalOpen] = useState(true);
	const [isStoppingModalOpen, setIsStoppingModalOpen] = useState(false);

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [envios, setEnvios] = useState<Envio[]>([]);
	const [simulation, setSimulation] = useState<Simulacion | undefined>(undefined);
	const [estadoAlmacen, setEstadoAlmacen] = useState<EstadoAlmacen | null>(null);

	const [client, setClient] = useState<Client | null>(null);

	const { isLoading } = useApi(
		"GET",
		`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
		(data: Aeropuerto[]) => {
			console.log("Fetched airport data succesfully");
			setAirports(data);
		},
		(error) => {
			console.log(error);
			toast.error("Error al cargar aeropuertos");
		}
	);

	const onSimulationRegister = async (simulacion: Simulacion) => {
		setIsSimulationLoading(true);

		//Connect to the socket
		const socket = new WebSocket(`${process.env.NEXT_PUBLIC_SOCKET}/websocket`);
		const client = new Client({
			webSocketFactory: () => socket,
		});

		client.onConnect = () => {
			console.log("Connected to WebSocket");
			client.subscribe("/algoritmo/respuesta", async (msg) => {
				setIsSimulationLoading(false);
				if ((JSON.parse(msg.body) as RespuestaAlgoritmo).simulacion.id !== simulacion.id) return;

				console.log("MENSAJE DE /algoritmo/respuesta: ", JSON.parse(msg.body) as RespuestaAlgoritmo);
				const data: RespuestaAlgoritmo = JSON.parse(msg.body);

				setSimulation((prev) => {
					if (prev === undefined) {
						console.log("Setting simulation");
						return { ...data.simulacion };
					} else {
						console.log("Simulation already set, not updating it again.");
						return prev;
					}
				});

				setCurrentTime(data.simulacion);

				let _paquetes: Paquete[] = [];
				await api(
					"GET",
					`${process.env.NEXT_PUBLIC_API}/back/simulacion/obtenerPaquetesSimulacionEnCurso/${simulacion.id}`,
					(data: Paquete[]) => {
						console.log(`Data from /back/simulacion/obtenerPaquetesSimulacionEnCurso/${simulacion.id}: `, data);
						_paquetes = [...data];
					},
					(error) => {
						console.log(`Error from /back/simulacion/obtenerPaquetesSimulacionEnCurso/${simulacion.id}: `, error);
						_paquetes = [];
					}
				);


				const { db_envios } = structureEnviosFromPaquetes(_paquetes);
				const { db_vuelos, db_estadoAlmacen } = structureDataFromRespuestaAlgoritmo(data);

				setFlights(db_vuelos);
				setEnvios(db_envios);
				setEstadoAlmacen(db_estadoAlmacen);
			});
			client.subscribe("/algoritmo/estado", (msg) => {
				if ((JSON.parse(msg.body) as RespuestaEstado).simulacion.id !== simulacion.id) return;

				console.log("MENSAJE DE /algoritmo/estado: ", JSON.parse(msg.body) as RespuestaEstado);
				const data: RespuestaEstado = JSON.parse(msg.body);
				setLoadingMessages((prev) => [...prev, data.estado]);
			});
		};

		client.activate();
		setClient(client);

		//Call api to run algorithm
		await api(
			"GET",
			`${process.env.NEXT_PUBLIC_API}/back/simulacion/runAlgorithm/` + simulacion.id,
			(data) => {
				console.log(data);
			},
			(error) => {
				console.log(error);
			}
		);
	};

	useEffect(() => {
		const handleBeforeUnload = (event: BeforeUnloadEvent) => {
			event.preventDefault();
			event.returnValue = 'Mensaje de prueba'; // Standard for most browsers
			return 'Mensaje de prueba'; // Some browsers may require this for the dialog to show up
		  };
	  
		  window.addEventListener('beforeunload', handleBeforeUnload);

		return () => {
			window.removeEventListener('beforeunload', handleBeforeUnload);

			if (client) {
				client.deactivate();
			}
		};
	}, []);

	return (
		<>
			<ModalIntro
				isOpen={isModalOpen}
				setIsModalOpen={setIsModalOpen}
				onSimulationRegister={(idSimulacion) => onSimulationRegister(idSimulacion)}
			/>
			<ModalStopSimulation isOpen={isStoppingModalOpen} setIsModalOpen={setIsStoppingModalOpen} simulation={simulation} />
			<MainContainer className="relative">
				<MapHeader>
					<BreadcrumbCustom items={breadcrumbItems} />
					<div className="flex flex-row gap-4 items-center ">
						<h1 className="text-4xl font-bold font-poppins">Visualizador de simulación</h1>
						<CurrentTime currentTime={currentTime} />
					</div>
				</MapHeader>

				<div className="flex items-center gap-5 absolute top-10 right-14 z-[20]">
					{/* <PlaneLegend /> */}
					<p className="text-lg font-medium">{elapsedRealTime}</p>
					{simulation !== null && simulation !== undefined && (
						<div className="flex items-center gap-1">
							<Button size={"icon"} onClick={() => setIsStoppingModalOpen(true)}>
								<CircleStop className="w-5 h-5" />
							</Button>

							{isPaused === true ? (
								<Button
									size={"icon"}
									onClick={async () => {
										setIsPauseLoading(true);
										await playSimulation(simulation, setSimulation);
										setIsPauseLoading(false);
										setIsPaused(false);
									}}
									disabled={isPauseLoading}
									isLoading={isPauseLoading}
								>
									<Play className="w-5 h-5" />
								</Button>
							) : (
								<Button
									size={"icon"}
									onClick={async () => {
										setIsPauseLoading(true);
										await pauseSimulation(simulation);
										setIsPauseLoading(false);
										setIsPaused(true);
									}}
									disabled={isPauseLoading}
									isLoading={isPauseLoading}
								>
									<Pause className="w-5 h-5" />
								</Button>
							)}
						</div>
					)}
				</div>

				{isSimulationLoading && (
					<>
						<div className="absolute top-1 bottom-3 left-3 right-3 bg-black z-[30] opacity-70 rounded-md flex justify-center items-center"></div>

						<div className="absolute top-1 bottom-3 left-3 right-3 flex flex-col justify-center items-center z-[100] gap-1">
							<img src="/plane_loading.gif" className="w-[10%] opacity-100" />
							{loadingMessages.map((msg, index) => (
								<p key={index} className="font-bold text-md text-white">
									{msg}
								</p>
							))}
						</div>
					</>
				)}

				<Sidebar
					envios={envios}
					vuelos={flights}
					aeropuertos={airports}
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
					isSimulation={true}
					mapModalAttributes={mapModalAttributes}
					attributes={attributes}
					className="absolute top-1 bottom-3 left-3 right-3"
					//airports={airports}
					flights={flights}
					estadoAlmacen={estadoAlmacen}
					simulation={simulation}
				/>
			</MainContainer>
		</>
	);
}

export default SimulationPage;
