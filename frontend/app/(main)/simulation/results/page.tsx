"use client";
import { useCallback, useContext, useEffect, useState } from "react";
import PercentageStorage from "./_components/percentage-storage";
import { api } from "@/lib/api";
import { SimulationContext } from "@/components/contexts/simulation-provider";
import { toast } from "sonner";
import PlanRutaEnvios from "./_components/plan-ruta-envios";
import { ReporteData } from "@/lib/types";
import MainContainer from "../../_components/main-container";
import { formatDateTimeLongShort } from "@/lib/date";
import { useRouter } from "next/navigation";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { Loader2 } from "lucide-react";

const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Acceso",
		link: "/security-code",
	},
	{
		label: "Simulación",
		link: "/simulation",
	},
	{
		label: "Reporte",
		link: "/simulation/results",
	},
];

function ResultsPage() {
	const router = useRouter();
	const { simulation } = useContext(SimulationContext);
	const [results, setResults] = useState<ReporteData[]>([]);
	const [finishSimTime, setFinishSimTime] = useState<Date | null>(null);
	const [totalSimTime, setTotalSimTime] = useState<number | null>(null);
	const [elapsedRealTime, setElapsedRealTime] = useState<number | null>(null);
	const [isLoading, setIsLoading] = useState<boolean>(true);

	if (simulation === null) {
		router.push("/simulation");
		return;
	}

	const getTimeByMs = useCallback((timeInMiliseconds: number | null) => {
		if (timeInMiliseconds === null) return "00h 00min";

		const h = Math.floor(timeInMiliseconds / 1000 / 60 / 60);
		const m = Math.floor((timeInMiliseconds / 1000 / 60 / 60 - h) * 60);
		const s = Math.floor(((timeInMiliseconds / 1000 / 60 / 60 - h) * 60 - m) * 60);

		const h_string = h < 10 ? `0${h}` : `${h}`;
		const m_string = m < 10 ? `0${m}` : `${m}`;
		const s_string = s < 10 ? `0${s}` : `${s}`;

		return `${h_string}h ${m_string}min`;
	}, []);

	useEffect(() => {
		async function getResults() {
			setIsLoading(true);
			if (simulation === null) {
				toast.error("No se ha encontrado una simulacion");
				return;
			}

			await api(
				"GET",
				`${process.env.NEXT_PUBLIC_API}/back/simulacion/obtenerReporteUltimaPlanificacion`,
				(data: ReporteData[]) => {
					console.log("Data: ", data);
					setResults(data);
				},
				(error) => {
					console.log("Error: ", error);
				}
			);

			const fechaInicioSistema: Date = new Date(simulation.fechaInicioSistema);
			const fechaInicioSim: Date = new Date(simulation.fechaInicioSim);
			const multiplicadorTiempo: number = simulation.multiplicadorTiempo;
			const milisegundosPausados: number = simulation.milisegundosPausados;
			const fechaDondeParoSimulacion: Date = new Date(simulation.fechaDondeParoSimulacion || new Date());

			setFinishSimTime(
				new Date(
					fechaInicioSim.getTime() +
						multiplicadorTiempo * (fechaDondeParoSimulacion.getTime() - fechaInicioSistema.getTime() - milisegundosPausados)
				)
			);

			setTotalSimTime(
				new Date(multiplicadorTiempo * (fechaDondeParoSimulacion.getTime() - fechaInicioSistema.getTime() - milisegundosPausados)).getTime()
			);
			setElapsedRealTime(new Date(fechaDondeParoSimulacion.getTime() - fechaInicioSistema.getTime() - milisegundosPausados).getTime());
			setIsLoading(false);
		}

		getResults();
	}, []);

	useEffect(() => {
		const handleBeforeUnload = (event: BeforeUnloadEvent) => {
			event.preventDefault();
			event.returnValue = "Mensaje de prueba"; // Standard for most browsers
			return "Mensaje de prueba"; // Some browsers may require this for the dialog to show up
		};

		window.addEventListener("beforeunload", handleBeforeUnload);

		return () => {
			window.removeEventListener("beforeunload", handleBeforeUnload);
		};
	}, []);

	return (
		<MainContainer>
			<BreadcrumbCustom items={breadcrumbItems} />
			<h1 className="text-4xl font-bold font-poppins">Reporte de simulación</h1>

			{isLoading === true ? (
				<div className="flex-1 rounded-lg bg-resultsPieceBackground/50 border border-resultsPieceBorder/10 mt-3 flex justify-center items-center">
					<Loader2 className="animate-spin stroke-gray-400"/>
				</div>
			) : (
				<div className="flex-1 grid grid-cols-6 grid-rows-4 gap-[20px] overflow-hidden mt-3">
					<div className="border col-span-2 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10 flex justify-center items-center">
						<div className="flex flex-col items-start">
							<p className="font-poppins text-[22px] font-medium text-muted-foreground">Fecha inicio en simulación</p>
							<p className="text-[40px] font-medium tracking-tight leading-[1]">
								{formatDateTimeLongShort(simulation?.fechaInicioSim || new Date())}
							</p>
						</div>
					</div>
					<PlanRutaEnvios
						className="border col-span-4 row-span-4 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"
						reporteData={results}
						finishSimTime={finishSimTime}
					/>
					<div className="border col-span-2 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10 flex justify-center items-center">
						<div className="flex flex-col items-start">
							<p className="font-poppins text-[22px] font-medium text-muted-foreground">Fecha fin en simulación</p>
							<p className="text-[40px] font-medium tracking-tight leading-[1]">
								{formatDateTimeLongShort(finishSimTime || new Date())}
							</p>
						</div>
					</div>
					<div className="border col-span-2 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10  flex justify-center items-center">
						<div className="flex flex-col items-start">
							<p className="font-poppins text-[22px] font-medium text-muted-foreground">Tiempo total simulado</p>
							<p className="text-[40px] font-medium tracking-tight leading-[1]">{getTimeByMs(totalSimTime)}</p>
						</div>
					</div>
					<div className="border col-span-2 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10  flex justify-center items-center">
						<div className="flex flex-col items-start">
							<p className="font-poppins text-[22px] font-medium text-muted-foreground">Tiempo real transcurrido</p>
							<p className="text-[40px] font-medium tracking-tight leading-[1]">{getTimeByMs(elapsedRealTime)}</p>
						</div>
					</div>
				</div>
			)}
		</MainContainer>
	);
}
export default ResultsPage;
