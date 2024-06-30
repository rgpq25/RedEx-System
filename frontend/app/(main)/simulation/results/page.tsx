"use client";
import { useContext, useEffect } from "react";
import PercentageStorage from "./_components/percentage-storage";
import { api } from "@/lib/api";
import { SimulationContext } from "@/components/contexts/simulation-provider";
import { toast } from "sonner";

function ResultsPage() {
	const { simulation } = useContext(SimulationContext);

	useEffect(() => {
		async function getResults() {
			if (simulation === null) {
				toast.error("No se ha encontrado una simulacion");
				return;
			}

			console.log("Fetching results for simulation: ", simulation);
            let results;

			await api(
				"POST",
				`${process.env.NEXT_PUBLIC_API}/back/simulacion/obtenerReporteSimulacion`,
				(data: any) => {
					console.log("Data: ", data);
					results = data;
				},
				(error) => {
					console.log("Error: ", error);
				},
				{
					idSimulacion: simulation.id,
					fechaCorte: simulation.fechaFinSim,
				}
			);
		}

		getResults();
	}, []);

	return (
		<div className="flex-1 flex flex-col p-7">
			<h1 className="font-poppins font-semibold text-4xl mb-2">Estadisticas de simulacion</h1>
			<div className="flex-1 grid grid-cols-6 grid-rows-5 gap-[20px]">
				{/* <PercentageStorage className="col-span-2 row-span-5 rounded-lg"></PercentageStorage> */}
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<div className="border col-span-1 row-span-1 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<div className="border col-span-2 row-span-4 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
				<div className="border col-span-2 row-span-4 rounded-lg bg-resultsPieceBackground border-resultsPieceBorder/10"></div>
			</div>
		</div>
	);
}
export default ResultsPage;
