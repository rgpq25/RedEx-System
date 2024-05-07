import { Button } from "@/components/ui/button";
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
	DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

export function ModalIntro({ isOpen, setIsModalOpen }: { isOpen: boolean; setIsModalOpen: (value: boolean) => void }) {
	return (
		<Dialog open={isOpen} onOpenChange={(_isOpen: boolean) => setIsModalOpen(_isOpen)}>
			<DialogContent className="sm:max-w-[500px]" hasCloseButton={false} disableOutsideEventsToClose={true}>
				<DialogHeader>
					<DialogTitle>Inicia una simulación</DialogTitle>
					<DialogDescription>
						Usa datos de envios variados para simular su projección semanal o hasta el colapso de las
						operaciones
					</DialogDescription>
				</DialogHeader>

				<div className="flex flex-col gap-3 mt-1">
					<div className="flex flex-row items-center justify-end gap-4">
						<Label htmlFor="name" className="text-right">
							Tipo
						</Label>
						<Select>
							<SelectTrigger className="w-[82%]">
								<SelectValue placeholder="Seleccione un tipo de simulación" />
							</SelectTrigger>
							<SelectContent>
								<SelectItem value="light">Semanal</SelectItem>
								<SelectItem value="dark">Hasta colapso</SelectItem>
							</SelectContent>
						</Select>
					</div>
					<div className="flex flex-row items-center justify-end gap-4">
						<Label htmlFor="username" className="text-right">
							Datos
						</Label>
						<Input id="username" placeholder="input_data.txt" className="w-[82%]" />
					</div>
				</div>
				<DialogFooter className="flex flex-row items-center">
					<Button type="submit" variant={"outline"} className="w-[100px]">
						Cancelar
					</Button>
					<Button type="submit" className="w-[100px]">
						Iniciar
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
