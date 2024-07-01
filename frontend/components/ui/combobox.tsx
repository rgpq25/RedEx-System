"use client";
import { Check, ChevronsUpDown } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { useState } from "react";
import { Envio, ReporteData } from "@/lib/types";
import { toast } from "sonner";

export function Combobox({
	value,
	setValue,
	envios,
}: {
	value: ReporteData | null;
	setValue: (envio: ReporteData | null) => void;
	envios: ReporteData[];
}) {
	const [open, setOpen] = useState(false);

	return (
		<Popover open={open} onOpenChange={setOpen}>
			<PopoverTrigger asChild>
				<Button variant="outline" role="combobox" aria-expanded={open} className="w-full justify-between">
					{value ? "Envio de ID: " + value.envio.id : "Selecciona un envio..."}
					<ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
				</Button>
			</PopoverTrigger>
			<PopoverContent className="w-[--radix-popover-trigger-width] p-0">
				<Command>
					<CommandInput placeholder="Search framework..." />
					<CommandList>
						<CommandEmpty>No framework found.</CommandEmpty>
						<CommandGroup>
							{envios.map((envio) => (
								<CommandItem
									key={envio.envio.id}
									value={envio.envio.id.toString()}
									onSelect={(currentValue) => {
										const newEnvio = envios.find((envio) => envio.envio.id.toString() === currentValue);
										if (newEnvio === undefined) {
											toast.error("Error al seleccionar envio");
											return;
										}

										setValue(newEnvio === value ? null : newEnvio);
										setOpen(false);
									}}
								>
									<Check className={cn("mr-2 h-4 w-4", value?.envio.id === envio.envio.id ? "opacity-100" : "opacity-0")} />
									{"Envio de ID: " + envio.envio.id}
								</CommandItem>
							))}
						</CommandGroup>
					</CommandList>
				</Command>
			</PopoverContent>
		</Popover>
	);
}
