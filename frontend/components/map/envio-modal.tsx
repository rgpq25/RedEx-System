import { useEffect, useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Aeropuerto, Envio, EstadoAlmacen, EstadoPaquete, Paquete, PlanRuta, Simulacion, Ubicacion, Vuelo } from "@/lib/types";
import { ArrowUpDown, Eye, Loader2 } from "lucide-react";
import { EnvioTable } from "./envio-table";
import { ColumnDef } from "@tanstack/react-table";
import { Button } from "../ui/button";
import { toast } from "sonner";
import { api } from "@/lib/api";
import Chip from "../ui/chip";

function getCurrentLocation(planRutaVuelos: Vuelo[], _currentTime: Date, lockToFlight: (vuelo: Vuelo) => void) {
	const vuelo = planRutaVuelos.find((vuelo) => new Date(vuelo.fechaSalida) <= _currentTime && new Date(vuelo.fechaLlegada) >= _currentTime);

	if (vuelo !== undefined) {
		return (
			<>
				<p>{vuelo.planVuelo.ciudadOrigen.pais + " - " + vuelo.planVuelo.ciudadDestino.pais}</p>
				<Button size="icon" className="w-7 h-7" variant="outline" onClick={() => lockToFlight(vuelo)}>
					<Eye className="w-4 h-4" />
				</Button>
			</>
		);
	} else {
		return <p>---</p>;
	}
}

function getCurrentAirport(planRutaVuelos: Vuelo[], _currentTime: Date, zoomToUbicacion: (ubicacion: Ubicacion) => void) {
	if (planRutaVuelos.find((vuelo) => new Date(vuelo.fechaSalida) <= _currentTime && new Date(vuelo.fechaLlegada) >= _currentTime) !== undefined) {
		return <p>---</p>;
	}

	if (_currentTime <= new Date(planRutaVuelos[0].fechaSalida)) {
		return (
			<>
				<p>{planRutaVuelos[0].planVuelo.ciudadOrigen.pais}</p>
				<Button size="icon" className="w-7 h-7" variant="outline" onClick={() => zoomToUbicacion(planRutaVuelos[0].planVuelo.ciudadOrigen)}>
					<Eye className="w-4 h-4" />
				</Button>
			</>
		);
	}

	if (_currentTime >= new Date(planRutaVuelos[planRutaVuelos.length - 1].fechaLlegada)) {
		return (
			<>
				<p>{planRutaVuelos[planRutaVuelos.length - 1].planVuelo.ciudadDestino.pais}</p>
				<Button
					size="icon"
					className="w-7 h-7"
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
					<p>{vuelo.planVuelo.ciudadDestino.pais}</p>
					<Button size="icon" className="w-7 h-7" variant="outline" onClick={() => zoomToUbicacion(vuelo.planVuelo.ciudadDestino)}>
						<Eye className="w-4 h-4" />
					</Button>
				</>
			);
		}
	}
}

function getPackageState(planRutaVuelos: Vuelo[], _currentTime: Date): EstadoPaquete {
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

function EnvioModal({ currentTime, isSimulation, isOpen, setIsOpen, envio, simulacion, zoomToUbicacion, lockToFlight }: EnvioModalProps) {
	const [isLoading, setIsLoading] = useState(false);
	const [planeMap, setPlaneMap] = useState<DataType | null>(null);

	const columns: ColumnDef<Paquete>[] = [
		{
			accessorKey: "numero",
			header: ({ column }) => {
				return (
					<div className="flex items-center w-[40px]">
						<p className="text-center w-full">Paquete</p>
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
			cell: ({ row }) => (
				<div className="text-start flex flex-row gap-2 items-center">
					{planeMap !== null && currentTime !== undefined
						? getCurrentLocation(planeMap[row.original.id], currentTime, (vuelo: Vuelo) => {
								lockToFlight(vuelo);
								setIsOpen(false);
						  })
						: "---"}
				</div>
			),
		},
		{
			accessorKey: "Origen",
			header: ({ column }) => {
				return (
					<div className="flex items-center ">
						<p>Aeropuerto actual</p>
					</div>
				);
			},
			cell: ({ row }) => (
				<div className="truncate flex flex-row gap-2 items-center">
					{planeMap !== null && currentTime !== undefined
						? getCurrentAirport(planeMap[row.original.id], currentTime, (ubicacion: Ubicacion) => {
								zoomToUbicacion(ubicacion);
								setIsOpen(false);
						  })
						: "---"}
				</div>
			),
		},
		{
			accessorKey: "statusAlmacen",
			header: ({ column }) => {
				return (
					<div className="flex items-center ">
						<p>Estado</p>
					</div>
				);
			},
			cell: ({ row }) => (
				<div className="">
					{planeMap !== null && currentTime !== undefined
						? renderStatusChip(getPackageState(planeMap[row.original.id], currentTime))
						: "---"}
				</div>
			),
		},
	];

	useEffect(() => {
		async function getPlanRutaPaquetes() {
			if (envio === null) {
				toast.error("No se encontró aeropuerto");
				return;
			}

			const packagesIds = envio.paquetes.map((paquete) => {
				return {
					id: paquete.id,
				};
			});

			console.log(packagesIds);

			console.log(`Fetching packages of envio with id ${envio.id}`);
			setIsLoading(true);

			await api(
				"POST",
				`${process.env.NEXT_PUBLIC_API}/back/vuelo/paqueteAll`,
				(data: DataType) => {
					console.log(data);

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

	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[900px] min-w-[900px] max-w-[900px] h-[650px] min-h-[650px] max-h-[650px]  flex flex-col gap-2">
				{isLoading ? (
					<div className="flex-1 flex justify-center items-center">
						<Loader2 className="animate-spin stroke-gray-400" />
					</div>
				) : (
					<>
						<DialogHeader>
							<DialogTitle className="text-2xl">Información de envío</DialogTitle>
						</DialogHeader>
						<div className="flex-1 flex flex-col">
							<p>{"Origen: " + envio?.ubicacionOrigen.ciudad + ", " + envio?.ubicacionOrigen.pais}</p>
							<p>{"Destino: " + envio?.ubicacionDestino.ciudad + ", " + envio?.ubicacionDestino.pais}</p>
							<p>
								{"Fecha recepcion: " + envio?.fechaRecepcion.toLocaleDateString() + " " + envio?.fechaRecepcion.toLocaleTimeString()}
							</p>
							<p>
								{"Fecha limite entrega: " +
									envio?.fechaLimiteEntrega.toLocaleDateString() +
									" " +
									envio?.fechaLimiteEntrega.toLocaleTimeString()}
							</p>
							<EnvioTable data={envio?.paquetes || []} columns={columns} />
						</div>
					</>
				)}
			</DialogContent>
		</Dialog>
	);
}
export default EnvioModal;
