"use client";
import React, { useEffect, useState } from "react";
import { AirportTable } from "./airport-table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog";
import { Aeropuerto, Envio, EstadoAlmacen, Paquete, Simulacion } from "@/lib/types";
import Chip from "../ui/chip";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { ColumnDef } from "@tanstack/react-table";
import { Button } from "../ui/button";
import { ArrowUpDown, Loader2 } from "lucide-react";
import { getPackagesFromAirport } from "@/lib/map-utils";

interface AirportModalProps {
	isSimulation: boolean;
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	aeropuerto: Aeropuerto | null;
	simulacion: Simulacion | undefined;
	currentTime: Date | undefined;
	getCurrentlyPausedTime: () => number;
	estadoAlmacen: EstadoAlmacen | null;
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
				<div className="flex items-center  gap-1">
					<p>Envío asociado</p>
					{/* <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
						<ArrowUpDown className="h-4 w-4" />
					</Button> */}
				</div>
			);
		},
		cell: ({ row }) => <div className=" text-start">{(row.getValue("envio") as Envio).id}</div>,
	},
	{
		accessorKey: "Origen",
		header: ({ column }) => {
			return (
				<div className="flex items-center flex-grow flex-1">
					<p>Origen</p>
					{/* <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
						<ArrowUpDown className="h-4 w-4" />
					</Button> */}
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="flex-1 truncate">
				{(row.getValue("envio") as Envio).ubicacionOrigen.ciudad +
					", " +
					(row.getValue("envio") as Envio).ubicacionOrigen.pais +
					" (" +
					(row.getValue("envio") as Envio).ubicacionOrigen.id +
					")"}
			</div>
		),
	},
	{
		accessorKey: "Destino",
		header: ({ column }) => {
			return (
				<div className="flex items-center  flex-grow flex-1">
					<p>Destino</p>
					{/* <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === "asc")} size={"icon"}>
						<ArrowUpDown className="h-4 w-4" />
					</Button> */}
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="flex-1 truncate">
				{(row.getValue("envio") as Envio).ubicacionDestino.ciudad +
					", " +
					(row.getValue("envio") as Envio).ubicacionDestino.pais +
					" (" +
					(row.getValue("envio") as Envio).ubicacionDestino.id +
					")"}
			</div>
		),
	},
];

function AirportModal({ isSimulation, isOpen, setIsOpen, aeropuerto, simulacion, currentTime, getCurrentlyPausedTime, estadoAlmacen }: AirportModalProps) {
	const [isLoading, setIsLoading] = useState(true);
	const [fechaConsulta, setFechaConsulta] = useState<Date | null>(null);
	const [paquetes, setPaquetes] = useState<Paquete[]>([]);

	useEffect(() => {
		async function getAirportShipments() {
			if (aeropuerto === null) {
				toast.error("No se encontró aeropuerto");
				return;
			}
			if (currentTime === undefined) {
				toast.error("No se encontró tiempo actual");
				return;
			}

			if (isSimulation === true) {
				if (simulacion === undefined) {
					toast.error("No se encontró simulación");
					return;
				}

				console.log(`Fetching airport data with airport id ${aeropuerto.id} and simulation id ${simulacion.id}`);

				const _temp_sim = {...simulacion};
				_temp_sim.milisegundosPausados = getCurrentlyPausedTime();
				setIsLoading(true);

				await api(
					"POST",
					`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/${aeropuerto.id}/paquetesfromsimulation`,
					(data: Paquete[]) => {
						const _paquetes_filtrados = getPackagesFromAirport(estadoAlmacen, aeropuerto.ubicacion.id, currentTime, data);
						setPaquetes(_paquetes_filtrados);
						setFechaConsulta(new Date(currentTime));
						setIsLoading(false);
					},
					(error) => {
						console.log(error);
						setIsOpen(false);
						toast.error("Error al cargar paquetes de aeropuerto");
					},
					_temp_sim
				);
			} else {
				console.log(`Fetching airport data with airport id ${aeropuerto.id}`);
				setIsLoading(true);
				await api(
					"GET",
					`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/${aeropuerto.id}/paquetesDiaDia`,
					(data: Paquete[]) => {
						const _paquetes_filtrados = getPackagesFromAirport(estadoAlmacen, aeropuerto.ubicacion.id, currentTime, data);
						setPaquetes(_paquetes_filtrados);
						setFechaConsulta(new Date(currentTime));
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
			getAirportShipments();
		}
	}, [isOpen]);

	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[900px] min-w-[900px] max-w-[900px] h-[617.5px] min-h-[617.5px] max-h-[617.5px]  flex flex-col gap-2">
				{isLoading ? (
					<div className="flex-1 flex justify-center items-center">
						<Loader2 className="animate-spin stroke-gray-400" />
					</div>
				) : (
					<>
						<DialogHeader>
							<DialogTitle className="text-2xl">
								Información de aeropuerto (al {fechaConsulta?.toLocaleDateString() + "-" + fechaConsulta?.toLocaleTimeString()})
							</DialogTitle>
						</DialogHeader>
						<div className="text-black flex-1 flex flex-col">
							<div>
								<p>{`Ubicación: ${aeropuerto?.ubicacion.ciudad}, ${aeropuerto?.ubicacion.pais} (${aeropuerto?.ubicacion.id})`}</p>
							</div>
							<div className="flex flex-row items-center gap-1">
								<p>{`Capacidad actual: ${paquetes.length}/${aeropuerto?.capacidadMaxima}`} </p>
								{aeropuerto &&
									(paquetes.length / aeropuerto?.capacidadMaxima <= 0.3 ? (
										<Chip color="green">Bajo</Chip>
									) : paquetes.length / aeropuerto?.capacidadMaxima <= 0.6 ? (
										<Chip color="yellow">Medio</Chip>
									) : (
										<Chip color="red">Alto</Chip>
									))}
							</div>
							<AirportTable data={paquetes} columns={columns} />
						</div>
					</>
				)}
			</DialogContent>
		</Dialog>
	);
}

export default AirportModal;
