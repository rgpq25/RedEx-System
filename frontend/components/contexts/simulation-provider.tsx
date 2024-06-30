"use client";

import { Simulacion } from "@/lib/types";
import { createContext, useState } from "react";

interface SimulationContextType {
	simulation: Simulacion | null;
	setSimulation: (simulation: Simulacion) => void;
}

export const SimulationContext = createContext<SimulationContextType>({
	simulation: null,
	setSimulation: () => {},
});

function SimulationProvider({ children }: { children: React.ReactNode }) {
	const [simulation, setSimulation] = useState<Simulacion | null>(null);

	return <SimulationContext.Provider value={{ simulation, setSimulation }}>{children}</SimulationContext.Provider>;
}
export default SimulationProvider;
