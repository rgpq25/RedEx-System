import Chip from "@/components/ui/chip";
import { Combobox } from "@/components/ui/combobox";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import { formatDateTimeLongShort } from "@/lib/date";
import { Aeropuerto, Envio, EstadoPaquete, Paquete, PaqueteConVuelos, ReporteData, Ubicacion, Vuelo } from "@/lib/types";
import { cn } from "@/lib/utils";
import { Step, StepIcon, StepIndicator, StepNumber, StepSeparator, StepStatus, Stepper, useSteps } from "@chakra-ui/react";
import { MoveRight } from "lucide-react";
import { Fragment, useEffect, useState } from "react";
import { toast } from "sonner";

type TrackingLineType = {
	type: "aeropuerto" | "vuelo" | "entrega";
	almacen: Ubicacion | null;
	vuelo: Vuelo | null;
	fecha1: Date | null;
	fecha2: Date | null;
	chip: EstadoPaquete;
};

function PlanRutaEnvios({ className, reporteData, finishSimTime }: { className: string; reporteData: ReporteData[]; finishSimTime: Date | null }) {
	const [selectedEnvio, setSelectedEnvio] = useState<ReporteData | null>(null);
	const [selectedPaquete, setSelectedPaquete] = useState<PaqueteConVuelos | null>(null);
	const [totalArray, setTotalArray] = useState<TrackingLineType[]>([]);

	const { activeStep, setActiveStep } = useSteps({
		index: 0,
		count: selectedEnvio === null ? 0 : selectedEnvio.infoPaquete.length * 2 + 2,
	});

	const getTotalArray = (array_vuelos: Vuelo[]) => {
		if (selectedEnvio === null || selectedPaquete === null) return [] as TrackingLineType[];

		const arrayFinal: TrackingLineType[] = [];

		for (let i = 0; i < array_vuelos.length; i++) {
			const vuelo = array_vuelos[i];

			if (i === 0) {
				arrayFinal.push({
					type: "aeropuerto",
					almacen: vuelo.planVuelo.ciudadOrigen,
					vuelo: null,
					fecha1: selectedEnvio?.envio.fechaRecepcion,
					fecha2: vuelo.fechaSalida,
					chip: { color: "gray", descripcion: "En aeropuerto origen" },
				});
			} else {
				arrayFinal.push({
					type: "aeropuerto",
					almacen: vuelo.planVuelo.ciudadOrigen,
					vuelo: null,
					fecha1: array_vuelos[i - 1].fechaLlegada,
					fecha2: vuelo.fechaSalida,
					chip: { color: "yellow", descripcion: "En espera" },
				});
			}

			arrayFinal.push({
				type: "vuelo",
				almacen: null,
				vuelo: vuelo,
				fecha1: vuelo.fechaSalida,
				fecha2: vuelo.fechaLlegada,
				chip: { color: "blue", descripcion: "En vuelo" },
			});
		}

		arrayFinal.push({
			type: "aeropuerto",
			almacen: array_vuelos[array_vuelos.length - 1].planVuelo.ciudadDestino,
			vuelo: null,
			fecha1: array_vuelos[array_vuelos.length - 1].fechaLlegada,
			fecha2: new Date(new Date(array_vuelos[array_vuelos.length - 1].fechaLlegada).getTime() + 5 * 60 * 1000),
			chip: { color: "purple", descripcion: "En aeropuerto destino" },
		});

		arrayFinal.push({
			type: "entrega",
			almacen: null,
			vuelo: null,
			fecha1: new Date(new Date(array_vuelos[array_vuelos.length - 1].fechaLlegada).getTime() + 5 * 60 * 1000),
			fecha2: null,
			chip: { color: "green", descripcion: "Entregado" },
		});

		return arrayFinal;
	};

	const findCurrentStep = (array_total: TrackingLineType[]) => {
		console.log("mira el array total: ", array_total);
		if (finishSimTime === null) return 0;
		if (array_total.length === 0) return 0;
		if (array_total[0].fecha1 === null) return 0;

		if (new Date(finishSimTime).getTime() < new Date(array_total[0].fecha1).getTime()) return 0;

		for (let i = 0; i < array_total.length; i++) {
			const trackingLine = array_total[i];
			if (trackingLine.fecha1 === null || trackingLine.fecha2 === null) continue;
			if (
				new Date(trackingLine.fecha1).getTime() <= new Date(finishSimTime).getTime() &&
				new Date(finishSimTime) < new Date(trackingLine.fecha2)
			)
				return i;
		}

		return array_total.length;
	};

	useEffect(() => {
		if (selectedEnvio !== null && selectedEnvio.infoPaquete.length > 0) {
			setSelectedPaquete(selectedEnvio.infoPaquete[0]);
		}

		if (selectedEnvio === null) {
			setSelectedPaquete(null);
		}
	}, [selectedEnvio]);

	useEffect(() => {
		if (selectedPaquete !== null) {
			const newTotalArray = getTotalArray(selectedPaquete.vuelos);
			setTotalArray(newTotalArray);

			const newStep = findCurrentStep(newTotalArray);
			console.log(newStep);
			setActiveStep(newStep);
		}
	}, [selectedPaquete]);

	return (
		<div className={cn("flex flex-col gap-2 p-5 overflow-hidden", className)}>
			<p className="font-poppins text-2xl font-medium">Planes de ruta de envíos (Última planificación)</p>
			<div className="flex flex-row items-center gap-1">
				<Combobox value={selectedEnvio} setValue={setSelectedEnvio} envios={reporteData} />
				<SelectPaquete selectedPaquete={selectedPaquete} setSelectedPaquete={setSelectedPaquete} selectedEnvio={selectedEnvio} />
			</div>

			{selectedEnvio !== null && (
				<>
					<div className="px-4 mt-2">
						<p className="text-xl font-medium">Informacion del envío:</p>
						<p>{`Origen - Destino: ${selectedEnvio.envio.ubicacionOrigen.ciudad}, ${selectedEnvio.envio.ubicacionOrigen.pais} (${selectedEnvio.envio.ubicacionOrigen.id}) - ${selectedEnvio.envio.ubicacionDestino.ciudad}, ${selectedEnvio.envio.ubicacionDestino.pais} (${selectedEnvio.envio.ubicacionDestino.id})`}</p>
						<p>{`Fecha de recepcion: ${formatDateTimeLongShort(selectedEnvio.envio.fechaRecepcion)}`}</p>
						<p>{`Cantidad de paquetes: ${selectedEnvio.envio.cantidadPaquetes}`}</p>
					</div>

					<div className="px-4 mt-3">
						<Separator orientation="horizontal" className="bg-gray-400" />
					</div>
				</>
			)}

			<div className="flex-1 flex p-4 overflow-hidden">
				<div className="flex flex-col gap-6 w-full h-full pr-2 overflow-auto" style={{ gridRow: selectedPaquete?.vuelos.length }}>
					{selectedEnvio && selectedPaquete ? (
						<Stepper orientation="vertical" index={activeStep} className="w-full">
							{totalArray.map((trackingLine, idx) => (
								<Step key={idx} className="w-full">
									<StepIndicator>
										<StepStatus complete={<StepIcon />} incomplete={<StepNumber />} active={<StepNumber />} />
									</StepIndicator>
									<TrackingLine key={idx} trackingLine={trackingLine} />

									<StepSeparator />
								</Step>
							))}
						</Stepper>
					) : (
						<p className="m-auto text-muted-foreground">Seleccione un envío para ver su plan de ruta</p>
					)}
				</div>
			</div>
		</div>
	);
}
export default PlanRutaEnvios;

function TrackingLine({ trackingLine }: { trackingLine: TrackingLineType }) {
	return (
		<>
			{trackingLine.type === "aeropuerto" ? (
				<div className="flex flex-row items-center gap-10 w-full">
					<div className="flex flex-col justify-center items-start w-[60%]">
						<p className="font-medium">{`Almacen de ${trackingLine.almacen?.ciudad}, ${trackingLine.almacen?.pais} (${trackingLine.almacen?.id})`}</p>
						<p className="text-muted-foreground leading-[1.1] text-[14px]">
							{`${formatDateTimeLongShort(trackingLine.fecha1 || new Date())} - ${formatDateTimeLongShort(
								trackingLine.fecha2 || new Date()
							)}`}
						</p>
					</div>

					<div className="flex items-center justify-end flex-1">
						<Chip color={trackingLine.chip.color}>{trackingLine.chip.descripcion}</Chip>
					</div>
				</div>
			) : trackingLine.type === "vuelo" ? (
				<div className="flex flex-row items-center gap-10 w-full">
					<div className="flex flex-row items-center w-[60%] gap-3">
						<div className="flex flex-col justify-center items-start flex-1">
							<p className="font-medium line-clamp-1 truncate">{`${trackingLine.vuelo?.planVuelo.ciudadOrigen.ciudad}, ${trackingLine.vuelo?.planVuelo.ciudadOrigen.pais} (${trackingLine.vuelo?.planVuelo.ciudadOrigen.id})`}</p>
							<p className="text-muted-foreground leading-[1.1] text-[14px]">
								{formatDateTimeLongShort(trackingLine.fecha1 || new Date())}
							</p>
						</div>

						<div className="w-[40px]">
							<MoveRight className="stroke-[1.2px] w-8 h-8 shrink-0" />
						</div>

						<div className="flex flex-col justify-center items-end flex-1">
							<p className="font-medium line-clamp-1 truncate">{`${trackingLine.vuelo?.planVuelo.ciudadDestino.ciudad}, ${trackingLine.vuelo?.planVuelo.ciudadDestino.pais} (${trackingLine.vuelo?.planVuelo.ciudadDestino.id})`}</p>
							<p className="text-muted-foreground leading-[1.1] text-[14px]">
								{formatDateTimeLongShort(trackingLine.fecha2 || new Date())}
							</p>
						</div>
					</div>

					<div className="flex items-center justify-end flex-1">
						<Chip color={trackingLine.chip.color}>{trackingLine.chip.descripcion}</Chip>
					</div>
				</div>
			) : (
				<div className="flex flex-row items-center gap-10 w-full">
					<div className="flex flex-col justify-center items-start w-[60%]">
						<p className="font-medium">{`Se entregó paquete a cliente`}</p>
						<p className="text-muted-foreground leading-[1.1] text-[14px]">
							{formatDateTimeLongShort(trackingLine.fecha1 || new Date())}
						</p>
					</div>
					<div className="flex items-center justify-end flex-1">
						<Chip color={trackingLine.chip.color}>{trackingLine.chip.descripcion}</Chip>
					</div>
				</div>
			)}
		</>
	);
}

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
