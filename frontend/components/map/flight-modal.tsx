"use client";
import React, { useEffect, useState } from "react";
import * as AlertDialog from "@radix-ui/react-alert-dialog";
import { FlightTable } from "./flight-table";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Envio, Paquete, Simulacion, Ubicacion, Vuelo } from "@/lib/types";
import Chip from "../ui/chip";
import { toast } from "sonner";
import { api } from "@/lib/api";
import { ColumnDef } from "@tanstack/react-table";
import { Button } from "../ui/button";
import { ArrowUpDown, Loader2 } from "lucide-react";
import { AirportTable } from "./airport-table";
import { structureEnviosFromPaquetesOnlyCurrentPaquetes } from "@/lib/map-utils";

interface FlightModalProps {
	isSimulation: boolean;
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	vuelo: Vuelo | null;
	simulacion: Simulacion | undefined;
}

// const columns: ColumnDef<Paquete>[] = [
// 	{
// 		accessorKey: "numero",
// 		header: ({ column }) => {
// 			return (
// 				<div className="flex items-center w-[40px]">
// 					<p className="text-center w-full">Nro.</p>
// 				</div>
// 			);
// 		},
// 		cell: ({ row }) => <div className="text-muted-foreground text-center w-[40px]">{row.index + 1}</div>,
// 	},
// 	{
// 		accessorKey: "envio",
// 		header: ({ column }) => {
// 			return (
// 				<div className="flex items-center gap-1 flex-1">
// 					<p>Envío asociado</p>
// 					{/* <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
// 						<ArrowUpDown className="h-4 w-4" />
// 					</Button> */}
// 				</div>
// 			);
// 		},
// 		cell: ({ row }) => <div className="truncate text-start flex-1">{(row.getValue("envio") as Envio).id }</div>,
// 	},
// 	{
// 		accessorKey: "Origen",
// 		header: ({ column }) => {
// 			return (
// 				<div className="flex items-center flex-1 w-[250px]">
// 					<p>Origen</p>
// 					{/* <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
// 						<ArrowUpDown className="h-4 w-4" />
// 					</Button> */}
// 				</div>
// 			);
// 		},
// 		cell: ({ row }) => (
// 			<div className="flex-1 truncate w-[250px]">
// 				{(row.getValue("envio") as Envio).ubicacionOrigen.ciudad + ", " + (row.getValue("envio") as Envio).ubicacionOrigen.pais + " (" + (row.getValue("envio") as Envio).ubicacionOrigen.id + ")"}
// 			</div>
// 		),
// 	},
// 	{
// 		accessorKey: "Destino",
// 		header: ({ column }) => {
// 			return (
// 				<div className="flex items-center w-[250px]">
// 					<p>Destino</p>
// 					{/* <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
// 						<ArrowUpDown className="h-4 w-4" />
// 					</Button> */}
// 				</div>
// 			);
// 		},
// 		cell: ({ row }) => (
// 			<div className="w-[250px] truncate">
// 				{(row.getValue("envio") as Envio).ubicacionDestino.ciudad + ", " + (row.getValue("envio") as Envio).ubicacionDestino.pais + " (" + (row.getValue("envio") as Envio).ubicacionDestino.id + ")"}
// 			</div>
// 		),
// 	}
// ];

const columns: ColumnDef<Envio>[] = [
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
		accessorKey: "id",
		header: ({ column }) => {
			return (
				<div className="flex items-center  gap-1">
					<p>Envío</p>
				</div>
			);
		},
		cell: ({ row }) => <div className=" text-start">{row.getValue("id")}</div>,
	},

	{
		accessorKey: "cantidadPaquetes",
		header: ({ column }) => {
			return (
				<div className="flex items-center  gap-1">
					<p># Paquetes</p>
				</div>
			);
		},
		cell: ({ row }) => <div className=" text-start">{row.getValue("cantidadPaquetes")}</div>,
	},

	{
		accessorKey: "ubicacionOrigen",
		header: ({ column }) => {
			return (
				<div className="flex items-center flex-grow flex-1">
					<p>Origen</p>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="flex-1 truncate">
				{(row.getValue("ubicacionOrigen") as Ubicacion).ciudad +
					", " +
					(row.getValue("ubicacionOrigen") as Ubicacion).pais +
					" (" +
					(row.getValue("ubicacionOrigen") as Ubicacion).id +
					")"}
			</div>
		),
	},
	{
		accessorKey: "ubicacionDestino",
		header: ({ column }) => {
			return (
				<div className="flex items-center  flex-grow flex-1">
					<p>Destino</p>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="flex-1 truncate">
				{(row.getValue("ubicacionDestino") as Ubicacion).ciudad +
					", " +
					(row.getValue("ubicacionDestino") as Ubicacion).pais +
					" (" +
					(row.getValue("ubicacionDestino") as Ubicacion).id +
					")"}
			</div>
		),
	},
];

function FlightModal({ isSimulation, isOpen, setIsOpen, vuelo, simulacion }: FlightModalProps) {
	const [isLoading, setIsLoading] = useState(true);
	const [paquetes, setPaquetes] = useState<Paquete[]>([]);
	const [envios, setEnvios] = useState<Envio[]>([]);

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
						setPaquetes(data);

						const { db_envios } = structureEnviosFromPaquetesOnlyCurrentPaquetes(data);
						setEnvios(db_envios);
						console.log(db_envios);

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
						setPaquetes(data);

						const { db_envios } = structureEnviosFromPaquetesOnlyCurrentPaquetes(data);
						setEnvios(db_envios);
						console.log(db_envios);

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
			<DialogContent className="w-[950px] min-w-[950px] max-w-[950px] h-[662px] min-h-[662px] max-h-[662px]  flex flex-col gap-2">
				{isLoading ? (
					<div className="flex-1 flex justify-center items-center">
						<Loader2 className="animate-spin stroke-gray-400" />
					</div>
				) : (
					<>
						<DialogHeader>
							<DialogTitle className="text-2xl">Información de vuelo</DialogTitle>
						</DialogHeader>
						<div className="flex-1 flex flex-col 	">
							<p>
								{"Origen: " +
									vuelo?.planVuelo.ciudadOrigen.ciudad +
									", " +
									vuelo?.planVuelo.ciudadOrigen.pais +
									" (" +
									vuelo?.planVuelo.ciudadOrigen.id +
									") --> " +
									"Destino: " +
									vuelo?.planVuelo.ciudadDestino.ciudad +
									", " +
									vuelo?.planVuelo.ciudadDestino.pais +
									" (" +
									vuelo?.planVuelo.ciudadDestino.id +
									")"}
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
							<AirportTable data={envios} columns={columns} />
						</div>
					</>
				)}
			</DialogContent>
		</Dialog>
	);
}

export default FlightModal;
