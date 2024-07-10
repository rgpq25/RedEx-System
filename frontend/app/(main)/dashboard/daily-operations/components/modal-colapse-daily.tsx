import {
	AlertDialog,
	AlertDialogAction,
	AlertDialogContent,
	AlertDialogDescription,
	AlertDialogFooter,
	AlertDialogHeader,
	AlertDialogTitle,
} from "@/components/ui/alert-dialog";

function ModalColapseDaily({ open, onOpenChange }: { open: boolean; onOpenChange: (isOpen: boolean) => void }) {
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
			</AlertDialogContent>
		</AlertDialog>
	);
}
export default ModalColapseDaily;
