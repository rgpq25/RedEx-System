"use client"

import {
	AlertDialog,
	AlertDialogTrigger,
	AlertDialogContent,
	AlertDialogHeader,
	AlertDialogTitle,
	AlertDialogDescription,
	AlertDialogFooter,
	AlertDialogCancel,
	AlertDialogAction,
} from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";
import { Simulacion } from "@/lib/types";
import { useState } from "react";
import { toast } from "sonner";

function ModalStopSimulation({
	isOpen,
	setIsModalOpen,
	simulation,
	redirectToReport,
}: {
	isOpen: boolean;
	setIsModalOpen: (value: boolean) => void;
	simulation: Simulacion | undefined;
	redirectToReport: () => void;
}) {

	const [isLoading, setIsLoading] = useState(false);

	return (
		<AlertDialog open={isOpen} onOpenChange={setIsModalOpen}>
			<AlertDialogContent>
				<AlertDialogHeader>
					<AlertDialogTitle>Estas seguro de detener la simulación?</AlertDialogTitle>
					<AlertDialogDescription>Esto hará que la simulación no continue, y la misma sera eliminada del sistema.</AlertDialogDescription>
				</AlertDialogHeader>
				<AlertDialogFooter>
					<AlertDialogCancel disabled={isLoading}>Cancelar</AlertDialogCancel>
					<Button
						onClick={async () => {
							setIsLoading(true);
							
							if (simulation === undefined) {
								toast.error("Simmulation is undefined, can't stop it");
								console.log("Simmulation is undefined, can't stop it");
								return;
							}

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

							redirectToReport();
						}}
						isLoading={isLoading}
						disabled={isLoading}
					>
						Continuar
					</Button>
				</AlertDialogFooter>
			</AlertDialogContent>
		</AlertDialog>
	);
}
export default ModalStopSimulation;
