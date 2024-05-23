"use client";
import React, { useEffect, useState } from "react";
import * as AlertDialog from "@radix-ui/react-alert-dialog";
import { FlightTable } from "./flight-table";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Paquete, Simulacion, Vuelo } from "@/lib/types";
import Chip from "../ui/chip";
import { toast } from "sonner";
import { api } from "@/lib/api";

interface FlightModalProps {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	vuelo: Vuelo | undefined;
	simulacion: Simulacion | undefined;
}

function FlightModal({ isOpen, setIsOpen, vuelo, simulacion }: FlightModalProps) {

	const [isLoading, setIsLoading] = useState(true);
	const [paquetes, setPaquetes] = useState<Paquete[]>([]);

	useEffect(() => {
		async function getAirportShipments() {
			if(vuelo === undefined || simulacion === undefined){
				toast.error("No se encontró aeropuerto o simulación");
				return;
			}
			
			console.log(`Fetching airport data with airport id ${vuelo.id} and simulation id ${simulacion.id}`)

			setIsLoading(true);
			await api(
				"GET",
				`http://localhost:8080/back/vuelo/${vuelo.id}/paquetes`,
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
					<DialogTitle className="text-2xl">Información de vuelo</DialogTitle>
				</DialogHeader>
				<div>
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
							(vuelo?.capacidadUtilizada <= 30 ? (
								<Chip color="green">Bajo</Chip>
							) : vuelo?.capacidadUtilizada <= 60 ? (
								<Chip color="yellow">Medio</Chip>
							) : (
								<Chip color="red">Alto</Chip>
							))}
					</div>
				</div>
				<FlightTable paquetes={paquetes}/>
			</DialogContent>
		</Dialog>
	);
}

export default FlightModal;
