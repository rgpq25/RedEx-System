"use client";
import { useCallback, useContext, useEffect, useState } from "react";
import Sidebar from "@/app/_components/sidebar";
import { Aeropuerto, AeropuertoHash, Envio, EstadoAlmacen, Paquete, RespuestaAlgoritmo, RespuestaEstado, Simulacion, Vuelo } from "@/lib/types";
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
import { CircleStop, Pause, Plane, Play, Warehouse } from "lucide-react";
import useMapModals from "@/components/hooks/useMapModals";
import MapHeader from "../_components/map-header";
import { Map } from "@/components/map/map";
import {
	getAirportHashmap,
	getPorcentajeOcupacionAeropuertos,
	getPorcentajeOcupacionVuelos,
	structureDataFromRespuestaAlgoritmo,
	structureEnviosFromPaquetes,
} from "@/lib/map-utils";
import ModalStopSimulation from "./_components/modal-stop-simulation";
import ElapsedRealTime from "@/app/_components/elapsed-real-time";
import ElapsedSimuTime from "@/app/_components/elapsed-simu-time";
import { useRouter } from "next/navigation";
import { SimulationContext } from "@/components/contexts/simulation-provider";

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
	const router = useRouter();
	const { setSimulation: setSimulationContext } = useContext(SimulationContext);

	const attributes = useMapZoom();
	const mapModalAttributes = useMapModals();
	const { currentTime, elapsedRealTime, elapsedSimulationTime, setCurrentTime, zoomToAirport, lockToFlight, pauseSimulation, playSimulation } =
		attributes;
	const { openFlightModal, openAirportModal, openEnvioModal } = mapModalAttributes;

	const [isSimulationLoading, setIsSimulationLoading] = useState<boolean>(false);
	const [loadingMessages, setLoadingMessages] = useState<string[]>(["Cargando simulación"]);

	const [isPaused, setIsPaused] = useState<boolean>(false);
	const [isPauseLoading, setIsPauseLoading] = useState<boolean>(false);

	const [isModalOpen, setIsModalOpen] = useState(true);
	const [isStoppingModalOpen, setIsStoppingModalOpen] = useState(false);

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [airportsHash, setAirportsHash] = useState<Map<string, Aeropuerto> | null>(null);
	const [flights, setFlights] = useState<Vuelo[]>([]);
	const [envios, setEnvios] = useState<Envio[]>([]);
	const [simulation, setSimulation] = useState<Simulacion | undefined>(undefined);
	const [estadoAlmacen, setEstadoAlmacen] = useState<EstadoAlmacen | null>(null);

	const [client, setClient] = useState<Client | null>(null);

	const getTimeByMs = useCallback((timeInMiliseconds: number) => {
		const h = Math.floor(timeInMiliseconds / 1000 / 60 / 60);
		const m = Math.floor((timeInMiliseconds / 1000 / 60 / 60 - h) * 60);
		const s = Math.floor(((timeInMiliseconds / 1000 / 60 / 60 - h) * 60 - m) * 60);

		const h_string = h < 10 ? `0${h}` : `${h}`;
		const m_string = m < 10 ? `0${m}` : `${m}`;
		const s_string = s < 10 ? `0${s}` : `${s}`;

		return `${h_string}:${m_string}:${s_string}`;
	}, []);

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
						return { ...data.simulacion };
					} else {
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
		async function getAirports(){
			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
				(data: Aeropuerto[]) => {
					console.log("Data from /back/aeropuerto/: ", data);
					setAirports(data);
					const airportHash = getAirportHashmap(data);
					setAirportsHash(airportHash);
				},
				(error) => {
					console.log(error);
					toast.error("Error al cargar aeropuertos");
				}
			);
		}

		getAirports();

		return () => {

			if (client) {
				client.deactivate();
			}
		};
	}, []);

	useEffect(() => {
		async function finishSimulation() {
			if (currentTime && simulation) {
				const date1 = new Date(currentTime);
				const date2 = new Date(simulation.fechaInicioSim);
				const diffTime = Math.abs(date2.getTime() - date1.getTime());
				const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
				if (diffDays > 7) {
					toast.info("Simulation ended successfully", {
						position: "bottom-center"
					});

					await api(
						"PUT",
						`${process.env.NEXT_PUBLIC_API}/back/simulacion/detener`,
						(data: any) => {
							console.log("Respuesta de /back/simulacion/detener: ", data);
						},
						(error) => {
							console.log("Error en /back/simulacion/detener: ", error);
						},
						simulation
					);

					await pauseSimulation(simulation);

					const saved_simu = { ...simulation };
					saved_simu.fechaDondeParoSimulacion = new Date();
					setSimulationContext(saved_simu);
					router.push("/simulation/results");
				}
			}
		}

		finishSimulation();
	}, [currentTime, simulation]);

	return (
		<>
			<ModalIntro
				isOpen={isModalOpen}
				setIsModalOpen={setIsModalOpen}
				onSimulationRegister={(idSimulacion) => onSimulationRegister(idSimulacion)}
			/>
			<ModalStopSimulation
				isOpen={isStoppingModalOpen}
				setIsModalOpen={setIsStoppingModalOpen}
				simulation={simulation}
				redirectToReport={async () => {
					if (simulation === undefined) {
						toast.error("No se ha cargado la simulación");
						return;
					}

					await pauseSimulation(simulation);

					const saved_simu = { ...simulation };
					saved_simu.fechaDondeParoSimulacion = new Date();
					setSimulationContext(saved_simu);
					router.push("/simulation/results");
				}}
			/>
			<MainContainer className="relative">
				<MapHeader>
					<BreadcrumbCustom items={breadcrumbItems} />
					<div className="flex flex-row gap-4 items-center ">
						<h1 className="text-4xl font-bold font-poppins">Visualizador de simulación</h1>
						<CurrentTime currentTime={currentTime} />
					</div>
				</MapHeader>

				<div className="flex flex-col items-end justify-center gap-1 absolute top-24 right-14 z-[20]">
					<div className="border rounded-xl border-purple-700 text-purple-700 bg-purple-200/70 py-1 proportional-nums w-fit text-start shadow-md px-3 flex flex-col gap-1 items-center justify-end">
						<a className="font-medium text-right w-full">Ocupacion promedio: </a>{" "}
						<div className="flex flex-row items-center gap-1 justify-end w-full">
							<p className="proportional-nums text-lg">{`${getPorcentajeOcupacionAeropuertos(airportsHash, estadoAlmacen, currentTime)}%`}</p>
							<Warehouse className="stroke-[1.1px] w-5 h-5"/>
						</div>
						<div className="flex flex-row items-center gap-1 justify-end w-full">
						<p className="proportional-nums text-lg">{`${getPorcentajeOcupacionVuelos(flights, currentTime)}%`}</p>
							<Plane className="stroke-[1.2px] w-5 h-5"/></div>
					</div>
				</div>

				<div className="flex flex-col items-end justify-center gap-1 absolute bottom-10 right-6 z-[20]">
					<ElapsedRealTime>{getTimeByMs(elapsedRealTime)}</ElapsedRealTime>
					<ElapsedSimuTime>{getTimeByMs(elapsedSimulationTime)}</ElapsedSimuTime>
				</div>

				<div className="flex items-center gap-5 absolute top-10 right-14 z-[20]">
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
