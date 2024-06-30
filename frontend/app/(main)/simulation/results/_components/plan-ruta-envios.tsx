import Chip from "@/components/ui/chip";
import { Combobox } from "@/components/ui/combobox";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { formatDateTimeLongShort } from "@/lib/date";
import { Envio, Paquete, PaqueteConVuelos, ReporteData } from "@/lib/types";
import { cn } from "@/lib/utils";
import { MoveRight } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";

function PlanRutaEnvios({ className, reporteData }: { className: string; reporteData: ReporteData[] }) {
	const [selectedEnvio, setSelectedEnvio] = useState<ReporteData | null>(null);
	const [selectedPaquete, setSelectedPaquete] = useState<PaqueteConVuelos | null>(null);

	useEffect(() => {
		if (selectedEnvio !== null && selectedEnvio.infoPaquete.length > 0) {
			setSelectedPaquete(selectedEnvio.infoPaquete[0]);
		}

		if (selectedEnvio === null) {
			setSelectedPaquete(null);
		}
	}, [selectedEnvio]);

	return (
		<div className={cn("flex flex-col gap-2 p-5", className)}>
			<p className="font-poppins text-2xl font-medium">Planes de ruta de envios</p>
			<div className="flex flex-row items-center gap-1">
				<Combobox value={selectedEnvio} setValue={setSelectedEnvio} envios={reporteData} />
				<SelectPaquete selectedPaquete={selectedPaquete} setSelectedPaquete={setSelectedPaquete} selectedEnvio={selectedEnvio} />
			</div>

			<div
				className="flex-1 grid grid-cols-[minmax(0,_1fr)_40px_minmax(0,_1fr)_200px] space-y-6 overflow-y-hidden"
				style={{ gridRow: selectedPaquete?.vuelos.length }}
			>
				{selectedEnvio && selectedPaquete ? (
					<>
						{selectedPaquete.vuelos.map((vuelo, idx) => {
							const ciudadOrigen = vuelo.planVuelo.ciudadOrigen;
							const ciudadDestino = vuelo.planVuelo.ciudadDestino;

							return (
								<>
									<div className="flex flex-col justify-center items-start col-span-3">
										<p className="">{`Almacen de ${ciudadOrigen.ciudad}, ${ciudadOrigen.pais} (${ciudadOrigen.id})`}</p>
										<p className="text-muted-foreground leading-[1.1]">
											{idx === 0
												? `${formatDateTimeLongShort(selectedEnvio.envio.fechaRecepcion)} - ${formatDateTimeLongShort(
														vuelo.fechaSalida
												  )}`
												: `${formatDateTimeLongShort(
														selectedPaquete.vuelos[idx - 1].fechaLlegada
												  )} - ${formatDateTimeLongShort(vuelo.fechaSalida)}`}
										</p>
									</div>
									<div className="flex items-center justify-end">
										{idx === 0 ? <Chip color="gray">En aeropuerto origen</Chip> : <Chip color="yellow">En espera</Chip>}
									</div>

									<div className="flex flex-col justify-center items-start">
										<p className="">{`${ciudadOrigen.ciudad}, ${ciudadOrigen.pais} (${ciudadOrigen.id})`}</p>
										<p className="text-muted-foreground leading-[1.1]">{formatDateTimeLongShort(vuelo.fechaSalida)}</p>
									</div>

									<div className="w-[45px]">
										<MoveRight className="stroke-[1.2px] w-8 h-8 shrink-0" />
									</div>

									<div className="flex flex-col justify-center items-end">
										<p className="">{`${ciudadDestino.ciudad}, ${ciudadDestino.pais} (${ciudadDestino.id})`}</p>
										<p className="text-muted-foreground leading-[1.1]">{formatDateTimeLongShort(vuelo.fechaLlegada)}</p>
									</div>

									<div className="flex items-center justify-end">
										<Chip color="blue">En vuelo</Chip>
									</div>
								</>
							);
						})}
						<div className="flex flex-col justify-center items-start col-span-3">
							<p className="">{`Almacen de ${selectedEnvio.envio.ubicacionDestino.ciudad}, ${selectedEnvio.envio.ubicacionDestino.pais} (${selectedEnvio.envio.ubicacionDestino.id})`}</p>
							<p className="text-muted-foreground leading-[1.1]">
								{`${formatDateTimeLongShort(selectedPaquete.vuelos[selectedPaquete.vuelos.length - 1].fechaLlegada)} - 
								${formatDateTimeLongShort(new Date(new Date(selectedPaquete.vuelos[selectedPaquete.vuelos.length - 1].fechaLlegada).getTime() + 5 * 60 * 1000))}
								`}
							</p>
						</div>
						<div className="flex items-center justify-end">
							<Chip color="purple">En aeropuerto destino</Chip>
						</div>

						<div className="flex flex-col justify-center items-start col-span-3">
							<p className="">{`Se entregó paquete a cliente`}</p>
							<p className="text-muted-foreground leading-[1.1]">
								{formatDateTimeLongShort(
									new Date(
										new Date(selectedPaquete.vuelos[selectedPaquete.vuelos.length - 1].fechaLlegada).getTime() + 5 * 60 * 1000
									)
								)}
							</p>
						</div>
						<div className="flex items-center justify-end">
							<Chip color="green">Entregado</Chip>
						</div>
					</>
				) : (
					<p className="m-auto text-muted-foreground">Seleccione un envio para ver su plan de ruta</p>
				)}
			</div>
		</div>
	);
}
export default PlanRutaEnvios;

function SelectPaquete({
	selectedPaquete,
	setSelectedPaquete,
	selectedEnvio,
}: {
	selectedPaquete: PaqueteConVuelos | null;
	setSelectedPaquete: (paquete: PaqueteConVuelos | null) => void;
	selectedEnvio: ReporteData | null;
}) {
	return (
		<Select
			onValueChange={(value) => {
				const selectedPaquete = selectedEnvio?.infoPaquete.find(({ paquete }) => paquete.id.toString() === value);
				if (selectedPaquete === undefined) {
					toast.error("Error al seleccionar paquete");
					return;
				}
				setSelectedPaquete(value === "" ? null : selectedPaquete);
			}}
			value={selectedPaquete?.paquete.id.toString()}
			disabled={!selectedEnvio}
		>
			<SelectTrigger>
				<SelectValue placeholder={"Seleccione un paquete"} />
			</SelectTrigger>
			<SelectContent>
				<SelectGroup>
					{selectedEnvio?.infoPaquete?.map(({ paquete }, idx) => (
						<SelectItem key={paquete.id} value={paquete.id.toString()}>
							{`Paquete #${idx} - (${paquete.id})`}
						</SelectItem>
					))}
				</SelectGroup>
			</SelectContent>
		</Select>
	);
}
