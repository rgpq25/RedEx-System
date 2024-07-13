import {
	AlertDialog,
	AlertDialogAction,
	AlertDialogCancel,
	AlertDialogContent,
	AlertDialogDescription,
	AlertDialogFooter,
	AlertDialogHeader,
	AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";
import { useState } from "react";

function ModalColapseDaily({ open, onOpenChange }: { open: boolean; onOpenChange: (isOpen: boolean) => void }) {
	const [isLoading, setIsLoading] = useState(false);

	async function reiniciarOperaciones() {
		setIsLoading(true);
		await api(
			"POST",
			`${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/reiniciarOperacionesDiaDia`,
			(data) => {
				console.log(data);
			},
			(error) => {
				console.log(error);
			}
		);

		// setIsLoading(false);
		window.location.reload();
	}

	return (
		<AlertDialog open={open} onOpenChange={onOpenChange}>
			<AlertDialogContent>
				<AlertDialogHeader>
					<AlertDialogTitle>Las operaciones día a día han colapsado.</AlertDialogTitle>
					<AlertDialogDescription>
						La ultima planificacion no ha logrado completarse, debido a un exceso en la ocupacion de un aeropuerto o en una entrega no
						completada.
					</AlertDialogDescription>
				</AlertDialogHeader>
				<AlertDialogFooter>
					<AlertDialogCancel>Cerrar</AlertDialogCancel>
				</AlertDialogFooter>
			</AlertDialogContent>
		</AlertDialog>
	);
}
export default ModalColapseDaily;
