"use client";
import { startWeeklySimulation } from "@/actions/simulation";
import { Button, buttonVariants } from "@/components/ui/button";
import { DatePicker } from "@/components/ui/date-picker";
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
import { cn } from "@/lib/utils";
import { FileUp } from "lucide-react";
import Link from "next/link";
import { useRef, useState } from "react";
import { toast } from "sonner";

import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { api } from "@/lib/api";
import { Simulacion } from "@/lib/types";

export function ModalIntro({
	isOpen,
	setIsModalOpen,
	onSimulationRegister,
}: {
	isOpen: boolean;
	setIsModalOpen: (value: boolean) => void;
	onSimulationRegister: (simulacion: Simulacion) => Promise<void>;
}) {
	const fileInputRef = useRef<HTMLInputElement>(null);
	const [file, setFile] = useState<File | undefined>();
	const [selectedDate, setSelectedDate] = useState<Date | undefined>();

	const openFilePicker = () => {
		if (fileInputRef.current) {
			fileInputRef.current.click();
		}
	};

	const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		const file = event.target.files?.[0];
		if (file) {
			setFile(file);
		} else {
			setFile(undefined);
		}
	};

	const handleStartSimulation = async () => {
		if (!file || !selectedDate) {
			toast.error("Seleccione un archivo y una fecha de inicio");
			return;
		}

		//Register new simulation and all shipments
		const simulacion = await startWeeklySimulation(file, selectedDate);

		if (simulacion === undefined || simulacion.id === 0) {
			toast.error("Error al registrar la simulación");
			return;
		}

		await onSimulationRegister(simulacion);

		setIsModalOpen(false);
	};

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
							<SelectTrigger className="w-[77%]">
								<SelectValue placeholder="Seleccione un tipo de simulación" />
							</SelectTrigger>
							<SelectContent>
								<SelectItem value="light">Semanal</SelectItem>
								<SelectItem value="dark">Hasta colapso</SelectItem>
							</SelectContent>
						</Select>
					</div>
					<div className="flex flex-row items-center justify-end gap-4">
						<Label className="text-right">Datos</Label>
						<div className="w-[77%] flex items-center gap-1">
							<Input
								placeholder="No ha seleccionado ningun archivo"
								readOnly
								value={file !== undefined ? file!.name : "No ha seleccionado ningun archivo"}
							/>
							<Input
								id="username"
								placeholder="No ha seleccionado ningun archivo"
								readOnly
								ref={fileInputRef}
								type="file"
								onChange={handleFileChange}
								className="hidden"
							/>
							<Button size={"icon"} className="shrink-0" onClick={openFilePicker}>
								<FileUp className="w-5 h-5 shrink-0" />
							</Button>
						</div>
					</div>
					<div className="flex flex-row items-center justify-end gap-4">
						<Label className="text-right">Fecha inicio</Label>
						<DatePicker
							className="w-[77%]"
							date={selectedDate}
							setDate={setSelectedDate}
							placeholder="Selecciona una fecha"
						/>
					</div>
				</div>
				<DialogFooter className="flex flex-row items-center">
					<Link href="/security-code" className={cn(buttonVariants({ variant: "outline" }), "w-[100px]")}>
						Cancelar
					</Link>
					<Button
						className="w-[100px]"
						onClick={() => handleStartSimulation()}
						disabled={file === undefined || selectedDate === undefined}
					>
						Iniciar
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
