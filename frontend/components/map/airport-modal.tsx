"use client";
import React, { useEffect, useState } from "react";
import { AirportTable } from "./airport-table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog";
import { Aeropuerto, Paquete, Simulacion } from "@/lib/types";
import Chip from "../ui/chip";
import { api } from "@/lib/api";
import { toast } from "sonner";

interface AirportModalProps {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	aeropuerto: Aeropuerto | undefined;
	simulacion: Simulacion | undefined;
}

function AirportModal({ isOpen, setIsOpen, aeropuerto, simulacion }: AirportModalProps) {

	const [isLoading, setIsLoading] = useState(true);
	const [paquetes, setPaquetes] = useState<Paquete[]>([]);

	useEffect(() => {
		async function getAirportShipments() {
			if(aeropuerto === undefined){
				toast.error("No se encontró aeropuerto");
				return;
			}

			if(simulacion === undefined){
				toast.error("No se encontró simulación");
				return;
			}
			
			console.log(`Fetching airport data with airport id ${aeropuerto.id} and simulation id ${simulacion.id}`)

			setIsLoading(true);
			await api(
				"GET",
				`http://localhost:8080/back/aeropuerto/${aeropuerto.id}/paquetesfromsimulation/${simulacion.id}`,
				(data: Paquete[]) => {
					console.log(data);
					setPaquetes(paquetes);
					setIsLoading(false);
				},
				(error) => {
					console.log(error);
					toast.error("Error al cargar paquetes de aeropuerto");
				}
			);
		}

		if(isOpen === true){
			getAirportShipments();
		}
	}, [isOpen]);
	
	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[900px] min-w-[900px]">
				<DialogHeader>
					<DialogTitle className="text-2xl">Información de aeropuerto</DialogTitle>
				</DialogHeader>
				<div className="text-black">
					<div>
						<p>{"Ubicación: " + aeropuerto?.ubicacion.ciudad + ", " + aeropuerto?.ubicacion.pais}</p>
					</div>
					<div className="flex flex-row items-center gap-1">
						<p>{"Capacidad actual: ?/" + aeropuerto?.capacidadMaxima} </p>
						<Chip color="green">Bueno</Chip>
					</div>
				</div>
				<AirportTable paquetes={paquetes}/>
			</DialogContent>
		</Dialog>
	);
}

export default AirportModal;
