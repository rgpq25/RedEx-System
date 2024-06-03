"use client";
import React, { useEffect, useState } from "react";
import { AirportTable } from "./airport-table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog";
import { Aeropuerto, Envio, Paquete, Simulacion } from "@/lib/types";
import Chip from "../ui/chip";
import { api } from "@/lib/api";
import { toast } from "sonner";
import { ColumnDef } from "@tanstack/react-table";
import { Button } from "../ui/button";
import { ArrowUpDown, Loader2 } from "lucide-react";

interface AirportModalProps {
	isSimulation: boolean;
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	aeropuerto: Aeropuerto | null;
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
					<Button
						variant="ghost"
						onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
						size={"icon"}
					>
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
					<Button
						variant="ghost"
						onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
						size={"icon"}
					>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="w-[150px] truncate">
				{(row.getValue("envio") as Envio).ubicacionOrigen.ciudad +
					", " +
					(row.getValue("envio") as Envio).ubicacionOrigen.pais}
			</div>
		),
	},
	{
		accessorKey: "Destino",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[150px]">
					<p>Destino</p>
					<Button
						variant="ghost"
						onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
						size={"icon"}
					>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="w-[150px] truncate">
				{(row.getValue("envio") as Envio).ubicacionDestino.ciudad +
					", " +
					(row.getValue("envio") as Envio).ubicacionDestino.pais}
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

function AirportModal({ isSimulation, isOpen, setIsOpen, aeropuerto, simulacion }: AirportModalProps) {
	const [isLoading, setIsLoading] = useState(true);
	const [fechaConsulta, setFechaConsulta] = useState<Date | null>(null);
	const [paquetes, setPaquetes] = useState<Paquete[]>([]);

	useEffect(() => {
		async function getAirportShipments() {
			if (aeropuerto === null) {
				toast.error("No se encontró aeropuerto");
				return;
			}

			if (isSimulation === true) {
				if (simulacion === undefined) {
					toast.error("No se encontró simulación");
					return;
				}

				console.log(
					`Fetching airport data with airport id ${aeropuerto.id} and simulation id ${simulacion.id}`
				);
				setIsLoading(true);

				await api(
					"GET",
					`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/${aeropuerto.id}/paquetesfromsimulation/${simulacion.id}`,
					(data: Paquete[]) => {
						console.log(data);
						setPaquetes(data);
						setFechaConsulta(new Date());
						setIsLoading(false);
					},
					(error) => {
						console.log(error);
						setIsOpen(false);
						toast.error("Error al cargar paquetes de aeropuerto");
					}
				);
			} else {
				console.log(`Fetching airport data with airport id ${aeropuerto.id}`);
				setIsLoading(true);
				await api(
					"GET",
					`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/${aeropuerto.id}/paquetes`,
					(data: Paquete[]) => {
						console.log("Finished fetch")
						console.log(data);
						setPaquetes(data);
						setFechaConsulta(new Date());
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
						<Loader2 className="animate-spin stroke-gray-400"/>
					</div>
				) : (
					<>
						<DialogHeader>
							<DialogTitle className="text-2xl">
								Información de aeropuerto (al{" "}
								{fechaConsulta?.toLocaleDateString() + "-" + fechaConsulta?.toLocaleTimeString()})
							</DialogTitle>
						</DialogHeader>
						<div className="text-black flex-1 flex flex-col">
							<div>
								<p>
									{"Ubicación: " + aeropuerto?.ubicacion.ciudad + ", " + aeropuerto?.ubicacion.pais}
								</p>
							</div>
							<div className="flex flex-row items-center gap-1">
								<p>{`Capacidad actual: ${paquetes.length}/${aeropuerto?.capacidadMaxima}`} </p>
								<Chip color="green">Bueno</Chip>
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
