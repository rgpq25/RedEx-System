"use client";
import React, { useEffect, useState } from "react";
import * as AlertDialog from "@radix-ui/react-alert-dialog";
import { FlightTable } from "./flight-table";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Envio, Paquete, Simulacion, Vuelo } from "@/lib/types";
import Chip from "../ui/chip";
import { toast } from "sonner";
import { api } from "@/lib/api";
import { ColumnDef } from "@tanstack/react-table";
import { Button } from "../ui/button";
import { ArrowUpDown, Loader2 } from "lucide-react";

interface FlightModalProps {
	isSimulation: boolean;
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	vuelo: Vuelo | null;
	simulacion: Simulacion | undefined;
}

const columns: ColumnDef<Paquete>[] = [
	{
		accessorKey: "numero",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[40px]">
					<p className="text-center w-full">Nro.</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="text-muted-foreground text-center w-[40px]">{row.index + 1}</div>,
	},
	{
		accessorKey: "envio",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[150px] gap-1">
					<p>Envío asociado</p>
					<Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => <div className="w-[150px] text-start">{(row.getValue("envio") as Envio).id}</div>,
	},
	{
		accessorKey: "Origen",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[150px]">
					<p>Origen</p>
					<Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="w-[150px] truncate">
				{(row.getValue("envio") as Envio).ubicacionOrigen.ciudad + ", " + (row.getValue("envio") as Envio).ubicacionOrigen.pais}
			</div>
		),
	},
	{
		accessorKey: "Destino",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[150px]">
					<p>Destino</p>
					<Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="w-[150px] truncate">
				{(row.getValue("envio") as Envio).ubicacionDestino.ciudad + ", " + (row.getValue("envio") as Envio).ubicacionDestino.pais}
			</div>
		),
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
		cell: ({ row }) => (
			<div className="">
				<p>Pendiente</p>
				{/* <Chip color={row.original.statusVariant}>{row.getValue("statusAlmacen")}</Chip> */}
			</div>
		),
	},
];

function FlightModal({ isSimulation, isOpen, setIsOpen, vuelo, simulacion }: FlightModalProps) {
	const [isLoading, setIsLoading] = useState(true);
	const [paquetes, setPaquetes] = useState<Paquete[]>([]);

	useEffect(() => {
		async function getFlightShipments() {
			if (vuelo === null) {
				toast.error("No se encontró aeropuerto");
				return;
			}

			if (isSimulation === true) {
				if (simulacion === undefined) {
					toast.error("No se encontró simulación");
					return;
				}

				console.log(
					`Fetching airport data with flight id ${vuelo.id}, current used capacity ${vuelo.capacidadUtilizada} and simulation id ${simulacion.id}`
				);
				setIsLoading(true);
				await api(
					"GET",
					`${process.env.NEXT_PUBLIC_API}/back/vuelo/${vuelo.id}/paquetes`,
					(data: Paquete[]) => {
						console.log(data);
						setPaquetes(data);
						setIsLoading(false);
					},
					(error) => {
						console.log(error);
						setIsOpen(false);
						toast.error("Error al cargar paquetes de aeropuerto");
					}
				);
			} else {
				console.log(`Fetching airport data with flight id ${vuelo.id} and current used capacity ${vuelo.capacidadUtilizada}`);
				setIsLoading(true);
				await api(
					"GET",
					`${process.env.NEXT_PUBLIC_API}/back/vuelo/${vuelo.id}/paquetes`,
					(data: Paquete[]) => {
						console.log(data);
						setPaquetes(data);
						setIsLoading(false);
					},
					(error) => {
						console.log(error);
						setIsOpen(false);
						toast.error("Error al cargar paquetes de aeropuerto");
					}
				);
			}
		}

		if (isOpen === true) {
			getFlightShipments();
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
							<p>
								{"Origen - Destino: " +
									vuelo?.planVuelo.ciudadOrigen.ciudad +
									", " +
									vuelo?.planVuelo.ciudadOrigen.pais +
									" - " +
									vuelo?.planVuelo.ciudadDestino.ciudad +
									", " +
									vuelo?.planVuelo.ciudadDestino.pais}
							</p>
							<p>{"Fecha salida: " + vuelo?.fechaSalida.toLocaleDateString() + " " + vuelo?.fechaSalida.toLocaleTimeString()}</p>
							<p>{"Fecha llegada: " + vuelo?.fechaLlegada.toLocaleDateString() + " " + vuelo?.fechaLlegada.toLocaleTimeString()}</p>
							<div className="flex flex-row items-center gap-1">
								<p>Capacidad actual: {vuelo?.capacidadUtilizada + " / " + vuelo?.planVuelo.capacidadMaxima}</p>
								{vuelo?.capacidadUtilizada &&
									(vuelo?.capacidadUtilizada / vuelo?.planVuelo.capacidadMaxima <= 0.3 ? (
										<Chip color="green">Bajo</Chip>
									) : vuelo?.capacidadUtilizada / vuelo?.planVuelo.capacidadMaxima <= 0.6 ? (
										<Chip color="yellow">Medio</Chip>
									) : (
										<Chip color="red">Alto</Chip>
									))}
							</div>
							<FlightTable data={paquetes} columns={columns} />
						</div>
					</>
				)}
			</DialogContent>
		</Dialog>
	);
}

export default FlightModal;
