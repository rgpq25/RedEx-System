import { Simulacion } from "@/lib/types";

function SimulationIndicator({ simulation }: { simulation: Simulacion | undefined }) {
	return (
		<>
			{simulation !== undefined ? (
				simulation.multiplicadorTiempo === 270 ? (
					<div className="border rounded-3xl border-green-700 text-green-700 bg-green-100/70 py-1 text-start shadow-md w-fit px-3">
						<a className="font-medium text-start w-full">Simulación semanal</a>
					</div>
				) : (
					<div className="border rounded-3xl border-fuchsia-700 text-fuchsia-700 bg-fuchsia-100/70 py-1 text-start shadow-md w-fit px-3">
						<a className="font-medium text-start w-full">Simulación hasta colapso</a>
					</div>
				)
			) : null}
		</>
	);
}
export default SimulationIndicator;
