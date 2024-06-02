import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Envio, Simulacion, Vuelo } from "@/lib/types";
import { Loader2 } from "lucide-react";

interface EnvioModalProps {
	isSimulation: boolean;
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	envio: Envio | null;
	simulacion: Simulacion | undefined;
}

function EnvioModal({ isSimulation, isOpen, setIsOpen, envio, simulacion }: EnvioModalProps) {
	const [isLoading, setIsLoading] = useState(false);

	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[900px] min-w-[900px] max-w-[900px] h-[650px] min-h-[650px] max-h-[650px]  flex flex-col gap-2">
				{isLoading ? (
					<div className="flex-1 flex justify-center items-center">
						<Loader2 className="animate-spin stroke-gray-400" />
					</div>
				) : (
					<>
						<DialogHeader>
							<DialogTitle className="text-2xl">Información de vuelo</DialogTitle>
						</DialogHeader>
						<div className="flex-1 flex flex-col">
							
						</div>
					</>
				)}
			</DialogContent>
		</Dialog>
	);
}
export default EnvioModal;
