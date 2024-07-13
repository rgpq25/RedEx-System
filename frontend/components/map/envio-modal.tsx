import { useCallback, useEffect, useMemo, useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Aeropuerto, Envio, EstadoAlmacen, EstadoPaquete, Paquete, PlanRuta, Simulacion, Ubicacion, Vuelo } from "@/lib/types";
import { ArrowUpDown, Eye, Loader2, MoveRight, X } from "lucide-react";
import { EnvioTable } from "./envio-table";
import { ColumnDef, Row } from "@tanstack/react-table";
import { Button } from "../ui/button";
import { toast } from "sonner";
import { api } from "@/lib/api";
import Chip from "../ui/chip";
import { AirportTable } from "./airport-table";
import { formatDateTimeLongShort } from "@/lib/date";
import { cn } from "@/lib/utils";

function getCurrentLocation(paquete: Paquete | undefined, planRutaVuelos: Vuelo[], _currentTime: Date, lockToFlight: (vuelo: Vuelo) => void) {
	if (paquete === undefined) {
		toast.error("No se encontró paquete");
		return <p>---</p>;
	}

	//@ts-ignore
	if (paquete.isFill === true) {
		return <p>---</p>;
	}

	if (planRutaVuelos.length === 0) {
		return <p>---</p>;
	}

	const vuelo = planRutaVuelos.find((vuelo) => new Date(vuelo.fechaSalida) <= _currentTime && new Date(vuelo.fechaLlegada) >= _currentTime);

	if (vuelo !== undefined) {
		return (
			<>
				<p className="truncate">{vuelo.planVuelo.ciudadOrigen.pais + " - " + vuelo.planVuelo.ciudadDestino.pais}</p>
				<Button size="icon" className="w-7 h-7 shrink-0" variant="outline" onClick={() => lockToFlight(vuelo)}>
					<Eye className="w-4 h-4" />
				</Button>
			</>
		);
	} else {
		return <p>---</p>;
	}
}

function getCurrentAirport(
	paquete: Paquete | undefined,
	planRutaVuelos: Vuelo[],
	_currentTime: Date,
	zoomToUbicacion: (ubicacion: Ubicacion) => void
) {
	if (paquete === undefined) {
		toast.error("No se encontró paquete");
		return <p>---</p>;
	}

	//@ts-ignore
	if (paquete.isFill === true) {
		return <p>---</p>;
	}

	if (planRutaVuelos.length === 0) {
		return (
			<>
				<p className="truncate">{`${paquete.aeropuertoActual.ubicacion.ciudad}, ${paquete.aeropuertoActual.ubicacion.pais} (${paquete.aeropuertoActual.ubicacion.id})`}</p>
				<Button
					size="icon"
					className="w-7 h-7 shrink-0"
					variant="outline"
					onClick={() => zoomToUbicacion(paquete.aeropuertoActual.ubicacion)}
				>
					<Eye className="w-4 h-4" />
				</Button>
			</>
		);
	}

	if (planRutaVuelos.find((vuelo) => new Date(vuelo.fechaSalida) <= _currentTime && new Date(vuelo.fechaLlegada) >= _currentTime) !== undefined) {
		return <p>---</p>;
	}

	if (_currentTime <= new Date(planRutaVuelos[0].fechaSalida)) {
		return (
			<>
				<p className="truncate">{`${planRutaVuelos[0].planVuelo.ciudadOrigen.ciudad}, ${planRutaVuelos[0].planVuelo.ciudadOrigen.pais} (${planRutaVuelos[0].planVuelo.ciudadOrigen.id})`}</p>
				<Button
					size="icon"
					className="w-7 h-7 shrink-0"
					variant="outline"
					onClick={() => zoomToUbicacion(planRutaVuelos[0].planVuelo.ciudadOrigen)}
				>
					<Eye className="w-4 h-4" />
				</Button>
			</>
		);
	}

	if (_currentTime >= new Date(planRutaVuelos[planRutaVuelos.length - 1].fechaLlegada)) {
		if (_currentTime.getTime() >= new Date(planRutaVuelos[planRutaVuelos.length - 1].fechaLlegada).getTime() + 1 * 60 * 1000) {
			return <p>---</p>;
		}

		return (
			<>
				<p className="truncate">{`${planRutaVuelos[planRutaVuelos.length - 1].planVuelo.ciudadDestino.ciudad}, ${
					planRutaVuelos[planRutaVuelos.length - 1].planVuelo.ciudadDestino.pais
				} (${planRutaVuelos[planRutaVuelos.length - 1].planVuelo.ciudadDestino.id})`}</p>
				<Button
					size="icon"
					className="w-7 h-7 shrink-0"
					variant="outline"
					onClick={() => zoomToUbicacion(planRutaVuelos[planRutaVuelos.length - 1].planVuelo.ciudadDestino)}
				>
					<Eye className="w-4 h-4" />
				</Button>
			</>
		);
	}

	for (let i = 0; i < planRutaVuelos.length; i++) {
		const vuelo = planRutaVuelos[i];
		const nextVuelo = planRutaVuelos[i + 1];

		if (nextVuelo && new Date(vuelo.fechaLlegada) <= _currentTime && new Date(nextVuelo.fechaSalida) >= _currentTime) {
			return (
				<>
					<p className="truncate">{`${vuelo.planVuelo.ciudadDestino.ciudad}, ${vuelo.planVuelo.ciudadDestino.pais} (${vuelo.planVuelo.ciudadDestino.id})`}</p>
					<Button size="icon" className="w-7 h-7 shrink-0" variant="outline" onClick={() => zoomToUbicacion(vuelo.planVuelo.ciudadDestino)}>
						<Eye className="w-4 h-4" />
					</Button>
				</>
			);
		}
	}

	return <p>---</p>;
}

export function getPackageState(paquete: Paquete | undefined, planRutaVuelos: Vuelo[], _currentTime: Date): EstadoPaquete {
	if (paquete === undefined) {
		return {
			descripcion: "Sin plan de ruta",
			color: "red",
		};
	}

	//@ts-ignore
	if (paquete.isFill === true) {
		return {
			descripcion: "Entregado",
			color: "green",
		};
	}

	if (planRutaVuelos.length === 0) {
		return {
			descripcion: "En aeropuerto origen",
			color: "gray",
		};
	}

	if (_currentTime <= new Date(planRutaVuelos[0].fechaSalida)) {
		return {
			descripcion: "En aeropuerto origen",
			color: "gray",
		};
	}

	if (_currentTime >= new Date(planRutaVuelos[planRutaVuelos.length - 1].fechaLlegada)) {
		if (_currentTime.getTime() >= new Date(planRutaVuelos[planRutaVuelos.length - 1].fechaLlegada).getTime() + 1 * 60 * 1000) {
			return {
				descripcion: "Entregado",
				color: "green",
			};
		}

		return {
			descripcion: "En aeropuerto destino",
			color: "purple",
		};
	}

	if (planRutaVuelos.find((vuelo) => new Date(vuelo.fechaSalida) <= _currentTime && _currentTime <= new Date(vuelo.fechaLlegada)) !== undefined) {
		return {
			descripcion: "En vuelo",
			color: "blue",
		};
	}

	return {
		descripcion: "En espera",
		color: "yellow",
	};
}

const renderStatusChip = (status: EstadoPaquete) => {
	return <Chip color={status.color}>{status.descripcion}</Chip>;
};

interface EnvioModalProps {
	currentTime: Date | undefined;
	isSimulation: boolean;
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	envio: Envio | null;
	simulacion: Simulacion | undefined;
	zoomToUbicacion: (ubicacion: Ubicacion) => void;
	lockToFlight: (vuelo: Vuelo) => void;
}

type DataType = {
	[key: number]: Vuelo[];
};

type ColumnPropType = {
	getCurrentAirport_Callback: (row: Row<Paquete>) => JSX.Element;
	getCurrentFlight_Callback: (row: Row<Paquete>) => JSX.Element;
	getCurrentStatus_Callback: (row: Row<Paquete>) => JSX.Element;
	setCurrentlyViewingPlanRuta: (row: Row<Paquete>) => void;
};

export const getColumns = ({
	getCurrentAirport_Callback,
	getCurrentFlight_Callback,
	getCurrentStatus_Callback,
	setCurrentlyViewingPlanRuta,
}: ColumnPropType): ColumnDef<Paquete>[] => [
	{
		accessorKey: "numero",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[40px]">
					<p className="text-center w-full">#</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="text-muted-foreground text-center w-[40px]">{row.index + 1}</div>,
	},
	{
		accessorKey: "planRutaVuelos",
		header: ({ column }) => {
			return (
				<div className="flex items-center">
					<p>Vuelo actual</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="text-start flex flex-row gap-2 items-centerline-clamp-1 truncate">{getCurrentFlight_Callback(row)}</div>,
	},
	{
		accessorKey: "Origen",
		header: ({ column }) => {
			return (
				<div className="flex items-center">
					<p>Aeropuerto actual</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="truncate flex flex-row gap-2 items-center">{getCurrentAirport_Callback(row)}</div>,
	},
	{
		accessorKey: "statusAlmacen",
		header: ({ column }) => {
			return (
				<div className="flex items-center">
					<p>Estado</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="">{getCurrentStatus_Callback(row)}</div>,
	},
	{
		accessorKey: "Ruta",
		header: ({ column }) => {
			return (
				<div className="flex items-center">
					<p>Ruta</p>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="">
				<Button size="icon" className="w-7 h-7 shrink-0" variant="outline" onClick={() => setCurrentlyViewingPlanRuta(row)}>
					<Eye className="w-4 h-4" />
				</Button>
			</div>
		),
	},
];

type TrackingLineType = {
	type: "aeropuerto" | "vuelo" | "entrega";
	almacen: Ubicacion | null;
	vuelo: Vuelo | null;
	fecha1: Date | null;
	fecha2: Date | null;
};

const getTotalArray = (array_vuelos: Vuelo[], _envio: Envio) => {
	if (array_vuelos.length === 0) return [];
	const arrayFinal: TrackingLineType[] = [];

	for (let i = 0; i < array_vuelos.length; i++) {
		const vuelo = array_vuelos[i];

		if (i === 0) {
			arrayFinal.push({
				type: "aeropuerto",
				almacen: vuelo.planVuelo.ciudadOrigen,
				vuelo: null,
				fecha1: _envio.fechaRecepcion,
				fecha2: vuelo.fechaSalida,
			});
		} else {
			arrayFinal.push({
				type: "aeropuerto",
				almacen: vuelo.planVuelo.ciudadOrigen,
				vuelo: null,
				fecha1: array_vuelos[i - 1].fechaLlegada,
				fecha2: vuelo.fechaSalida,
			});
		}

		arrayFinal.push({
			type: "vuelo",
			almacen: null,
			vuelo: vuelo,
			fecha1: vuelo.fechaSalida,
			fecha2: vuelo.fechaLlegada,
		});
	}

	arrayFinal.push({
		type: "aeropuerto",
		almacen: array_vuelos[array_vuelos.length - 1].planVuelo.ciudadDestino,
		vuelo: null,
		fecha1: array_vuelos[array_vuelos.length - 1].fechaLlegada,
		fecha2: new Date(new Date(array_vuelos[array_vuelos.length - 1].fechaLlegada).getTime() + 1 * 60 * 1000),
	});

	arrayFinal.push({
		type: "entrega",
		almacen: null,
		vuelo: null,
		fecha1: new Date(new Date(array_vuelos[array_vuelos.length - 1].fechaLlegada).getTime() + 1 * 60 * 1000),
		fecha2: null,
	});

	return arrayFinal;
};

function EnvioModal({ currentTime, isSimulation, isOpen, setIsOpen, envio, simulacion, zoomToUbicacion, lockToFlight }: EnvioModalProps) {
	const [isLoading, setIsLoading] = useState(false);
	const [planeMap, setPlaneMap] = useState<DataType | null>(null);
	const [currentPlanRuta, setCurrentPlanRuta] = useState<Vuelo[] | null>(null);
	const [totalArray, setTotalArray] = useState<TrackingLineType[]>([]);
	const [numPaqueteSelected, setNumPaqueteSelected] = useState<number | null>(null);
	const [paquetesEnvio, setPaquetesEnvio] = useState<Paquete[]>([]);

	const getCurrentAirport_Callback = (row: Row<Paquete>) => {
		if (planeMap === null || currentTime === undefined || planeMap[row.original.id] === undefined) return <p>---</p>;
		return getCurrentAirport(paquetesEnvio[row.index], planeMap[row.original.id], currentTime, (ubicacion: Ubicacion) => {
			zoomToUbicacion(ubicacion);
			setIsOpen(false);
		});
	};

	const getCurrentFlight_Callback = (row: Row<Paquete>) => {
		if (planeMap === null || currentTime === undefined || planeMap[row.original.id] === undefined) return <p>---</p>;
		return getCurrentLocation(paquetesEnvio[row.index], planeMap[row.original.id], currentTime, (vuelo: Vuelo) => {
			lockToFlight(vuelo);
			setIsOpen(false);
		});
	};

	const getCurrentStatus_Callback = (row: Row<Paquete>) => {
		if (planeMap === null || currentTime === undefined || planeMap[row.original.id] === undefined) return <p>---</p>;
		return renderStatusChip(getPackageState(paquetesEnvio[row.index], planeMap[row.original.id], currentTime));
	};

	const setCurrentlyViewingPlanRuta = (row: Row<Paquete>) => {
		if (planeMap === null) return;
		setCurrentPlanRuta(planeMap[row.original.id]);
		setNumPaqueteSelected(row.index + 1);
	};

	const columns = getColumns({
		getCurrentAirport_Callback,
		getCurrentFlight_Callback,
		getCurrentStatus_Callback,
		setCurrentlyViewingPlanRuta,
	});

	const findCurrentStep = (array_total: TrackingLineType[]) => {
		if (currentTime === undefined) return 0;
		if (array_total.length === 0) return 0;
		if (array_total[0].fecha1 === null) return 0;

		if (new Date(currentTime).getTime() < new Date(array_total[0].fecha1).getTime()) return 0;

		for (let i = 0; i < array_total.length; i++) {
			const trackingLine = array_total[i];
			if (trackingLine.fecha1 === null || trackingLine.fecha2 === null) continue;
			if (
				new Date(trackingLine.fecha1).getTime() <= new Date(currentTime).getTime() &&
				new Date(currentTime).getTime() < new Date(trackingLine.fecha2).getTime()
			)
				return i;
		}

		return array_total.length;
	};

	useEffect(() => {
		async function getPlanRutaPaquetes() {
			if (envio === null) {
				toast.error("No se encontró aeropuerto");
				return;
			}

			setIsLoading(true);
			setCurrentPlanRuta(null);

			let newEnvio: Envio = { ...envio };

			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/paquete/envio/${envio.id}`,
				(data: Paquete[]) => {
					// console.log("Paquetes de envio: ", data);
					newEnvio.paquetes = data;
					setPaquetesEnvio(data);
				},
				(error) => {
					console.log(error);
					setIsOpen(false);
					toast.error("Error al cargar paquetes de envio");
				}
			);

			const packagesIds = newEnvio.paquetes.map((paquete) => {
				return {
					id: paquete.id,
				};
			});

			console.log(`Fetching packages of envio with id ${newEnvio.id}`);

			await api(
				"POST",
				`${process.env.NEXT_PUBLIC_API}/back/vuelo/paqueteAll`,
				(data: DataType) => {
					// console.log(data);

					setPlaneMap(data);

					setIsLoading(false);
				},
				(error) => {
					console.log(error);
					setIsOpen(false);
					toast.error("Error al cargar paquetes de aeropuerto");
				},
				packagesIds
			);
		}

		if (isOpen === true) {
			getPlanRutaPaquetes();
		}
	}, [isOpen]);

	useEffect(() => {
		if (currentPlanRuta !== null && envio !== null) {
			console.log(currentPlanRuta);
			const totalArray = getTotalArray(currentPlanRuta, envio);
			setTotalArray(totalArray);
		} else {
			setTotalArray([]);
		}
	}, [currentPlanRuta]);

	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent
				className={cn(
					"w-[900px] min-w-[900px] max-w-[900px] h-[687px] min-h-[687px] max-h-[687px]  flex flex-col gap-2",
					currentPlanRuta && "-translate-x-[650px]"
				)}
			>
				{isLoading ? (
					<div className="flex-1 flex justify-center items-center">
						<Loader2 className="animate-spin stroke-gray-400" />
					</div>
				) : (
					<>
						<DialogHeader>
							<DialogTitle className="text-2xl">Información de envío</DialogTitle>
						</DialogHeader>
						<div className="flex-1 flex flex-col relative">
							<p>
								{"Origen: " +
									envio?.ubicacionOrigen.ciudad +
									", " +
									envio?.ubicacionOrigen.pais +
									" (" +
									envio?.ubicacionOrigen.id +
									") --> " +
									"Destino: " +
									envio?.ubicacionDestino.ciudad +
									", " +
									envio?.ubicacionDestino.pais +
									" (" +
									envio?.ubicacionDestino.id +
									")"}
							</p>

							<p>{"Fecha recepcion: " + formatDateTimeLongShort(envio?.fechaRecepcion || new Date())}</p>
							<p>{"Fecha limite entrega: " + formatDateTimeLongShort(envio?.fechaLimiteEntrega || new Date())}</p>
							<AirportTable data={paquetesEnvio} columns={columns} />

							{currentPlanRuta && (
								<div className="w-[420px] p-4 absolute top-10 -right-[460px] bottom-10 bg-white rounded-lg flex flex-col gap-2">
									<div className="flex flex-row items-center justify-between">
										<p className="text-lg font-medium leading-5">{`Ruta de paquete seleccionado: (${numPaqueteSelected})`}</p>
										<X
											className="w-4 h-4 stroke-muted-foreground hover:stroke-black transition-colors  cursor-pointer"
											onClick={() => {
												setNumPaqueteSelected(null);
												setCurrentPlanRuta(null);
												setTotalArray([]);
											}}
										/>
									</div>
									<div className="flex-1 flex flex-col gap-3 mt-1 overflow-auto">
										{/* {currentPlanRuta.length !== 0 ? (
											currentPlanRuta.map((vuelo, index) => {
												return (
													<div className="flex flex-row justify-between items-center overflow-hidden" key={vuelo.id}>
														<div className="flex flex-col items-start justify-center flex-1 overflow-hidden">
															<p className="line-clamp-1">{`${vuelo.planVuelo.ciudadOrigen.pais} (${vuelo.planVuelo.ciudadOrigen.id})`}</p>
															<p className="text-sm text-muted-foreground leading-3">{`${formatDateTimeLongShort(
																vuelo.fechaSalida
															)}`}</p>
														</div>
														<div className="flex flex-col items-end justify-center flex-1 overflow-hidden">
															<p className="line-clamp-1">{`${vuelo.planVuelo.ciudadDestino.pais} (${vuelo.planVuelo.ciudadDestino.id})`}</p>
															<p className="text-sm text-muted-foreground leading-3">{`${formatDateTimeLongShort(
																vuelo.fechaLlegada
															)}`}</p>
														</div>
													</div>
												);
											})
										) : (
											<div className="text-muted-foreground m-auto text-center  leading-5">
												Este paquete aun no tiene una ruta definida
											</div>
										)} */}

										{totalArray.length !== 0 ? (
											totalArray.map((trackingLine, index) => {
												const isCurrentStep = findCurrentStep(totalArray) === index;

												return <TrackingLine key={index} trackingLine={trackingLine} isCurrentStep={isCurrentStep} />;
											})
										) : (
											<div className="text-muted-foreground m-auto text-center  leading-5 text-sm">
												Este paquete aun no tiene una ruta definida
											</div>
										)}
									</div>
								</div>
							)}
						</div>
					</>
				)}
			</DialogContent>
		</Dialog>
	);
}
export default EnvioModal;

function TrackingLine({ trackingLine, isCurrentStep }: { trackingLine: TrackingLineType; isCurrentStep: boolean }) {
	return (
		<>
			{trackingLine.type === "aeropuerto" ? (
				<div className={cn("flex flex-col justify-center items-start w-full px-2 py-1", isCurrentStep === true && "bg-gray-200 rounded-lg")}>
					<p className="font-medium text-sm">{`Almacen de ${trackingLine.almacen?.ciudad}, ${trackingLine.almacen?.pais} (${trackingLine.almacen?.id})`}</p>
					<p className="text-muted-foreground leading-[1.1] text-xs">
						{`${formatDateTimeLongShort(trackingLine.fecha1 || new Date())} - ${formatDateTimeLongShort(
							trackingLine.fecha2 || new Date()
						)}`}
					</p>
				</div>
			) : trackingLine.type === "vuelo" ? (
				<div className={cn("flex flex-row items-center gap-3 w-full px-2 py-1", isCurrentStep === true && "bg-gray-200 rounded-lg")}>
					<div className="flex flex-col justify-center items-start flex-1">
						<p className="font-medium line-clamp-1 text-sm">{`${trackingLine.vuelo?.planVuelo.ciudadOrigen.ciudad}, ${trackingLine.vuelo?.planVuelo.ciudadOrigen.pais} (${trackingLine.vuelo?.planVuelo.ciudadOrigen.id})`}</p>
						<p className="text-muted-foreground leading-[1.1] text-xs">{formatDateTimeLongShort(trackingLine.fecha1 || new Date())}</p>
					</div>

					<div className="flex flex-col justify-center items-end flex-1">
						<p className="font-medium text-sm line-clamp-1">{`${trackingLine.vuelo?.planVuelo.ciudadDestino.ciudad}, ${trackingLine.vuelo?.planVuelo.ciudadDestino.pais} (${trackingLine.vuelo?.planVuelo.ciudadDestino.id})`}</p>
						<p className="text-muted-foreground leading-[1.1] text-xs">{formatDateTimeLongShort(trackingLine.fecha2 || new Date())}</p>
					</div>
				</div>
			) : (
				<div className={cn("flex flex-col justify-center items-start w-full px-2 py-1", isCurrentStep === true && "bg-gray-200 rounded-lg")}>
					<p className="font-medium text-sm">{`Se entregó paquete a cliente`}</p>
					<p className="text-muted-foreground leading-[1.1] text-xs">{formatDateTimeLongShort(trackingLine.fecha1 || new Date())}</p>
				</div>
			)}
		</>
	);
}
