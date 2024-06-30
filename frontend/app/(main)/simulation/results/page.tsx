"use client";
import { useContext, useEffect, useState } from "react";
import PercentageStorage from "./_components/percentage-storage";
import { api } from "@/lib/api";
import { SimulationContext } from "@/components/contexts/simulation-provider";
import { toast } from "sonner";
import PlanRutaEnvios from "./_components/plan-ruta-envios";
import { ReporteData } from "@/lib/types";



function ResultsPage() {
	const { simulation } = useContext(SimulationContext);
	const [results, setResults] = useState<ReporteData[]>([]);

	useEffect(() => {
		async function getResults() {
			if (simulation === null) {
				toast.error("No se ha encontrado una simulacion");
				return;
			}

			console.log("Fetching results for simulation: ", simulation);

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
		}

		getResults();
	}, []);

	return (
		<div className="flex-1 flex flex-col p-7">
			<h1 className="font-poppins font-semibold text-4xl mb-2">Estadisticas de simulacion</h1>
			<div className="flex-1 grid grid-cols-6 grid-rows-4 gap-[20px]">
				<PercentageStorage className="col-span-2 row-span-4 rounded-lg"></PercentageStorage>
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<PlanRutaEnvios
					className="border col-span-3 row-span-4 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"
					reporteData={results}
				/>
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
			</div>
		</div>
	);
}
export default ResultsPage;
