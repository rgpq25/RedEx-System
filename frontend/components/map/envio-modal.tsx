import { useEffect, useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Envio, Paquete, PlanRuta, Simulacion, Vuelo } from "@/lib/types";
import { ArrowUpDown, Loader2 } from "lucide-react";
import { EnvioTable } from "./envio-table";
import { ColumnDef } from "@tanstack/react-table";
import { Button } from "../ui/button";
import { toast } from "sonner";
import { api } from "@/lib/api";

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
		accessorKey: "envio",
		header: ({ column }) => {
			return (
				<div className="flex items-center gap-1">
					<p>Ubicación actual</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="text-start">PENDING</div>,
	},
	{
		accessorKey: "Origen",
		header: ({ column }) => {
			return (
				<div className="flex items-center ">
					<p>Proximo destino</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="truncate">PENDING</div>,
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
				<p>Pendiente</p>
				{/* <Chip color={row.original.statusVariant}>{row.getValue("statusAlmacen")}</Chip> */}
			</div>
		),
	},
];

interface EnvioModalProps {
	isSimulation: boolean;
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	envio: Envio | null;
	simulacion: Simulacion | undefined;
}

function EnvioModal({ isSimulation, isOpen, setIsOpen, envio, simulacion }: EnvioModalProps) {
	const [isLoading, setIsLoading] = useState(false);
	const [planesRuta, setPlanesRuta] = useState<Vuelo[]>([]);

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

			console.log(packagesIds)

			console.log(`Fetching packages of envio with id ${envio.id}`);
			setIsLoading(true);

			await api(
				"GET",
				`http://localhost:8080/back/vuelo/paqueteAll`,
				(data: Paquete[]) => {
					console.log(data);
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
							<DialogTitle className="text-2xl">Información de vuelo</DialogTitle>
						</DialogHeader>
						<div className="flex-1 flex flex-col">
							<p>{"Origen: " + envio?.ubicacionOrigen.ciudad + ", " + envio?.ubicacionOrigen.pais}</p>
							<p>{"Destino: " + envio?.ubicacionDestino.ciudad + ", " + envio?.ubicacionDestino.pais}</p>
							<p>
								{"Fecha recepcion: " +
									envio?.fechaRecepcion.toLocaleDateString() +
									" " +
									envio?.fechaRecepcion.toLocaleTimeString()}
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
