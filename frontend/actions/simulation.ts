import { api } from "@/lib/api";
import { Simulacion, SimulationType } from "@/lib/types";

export const startWeeklySimulation = async (startDate: Date, simulationType: SimulationType): Promise<Simulacion> => {
	let idSimulacion: number = 0;
	let simulacion: Simulacion | undefined = undefined;
	const finishDate = new Date(startDate);
	finishDate.setDate(finishDate.getDate() + 7);

	//@ts-expect-error
	const simuData: Simulacion = {
		fechaInicioSim: startDate,
		fechaFinSim: finishDate,
		fechaInicioSistema: new Date(),
		multiplicadorTiempo: simulationType === "weekly" ? 270 : 100,
	};

	await api(
		"POST",
		//`${process.env.NEXT_PUBLIC_API}/back/simulacion/inicializarSimulacionCargaVariable/media`,
		`${process.env.NEXT_PUBLIC_API}/back/simulacion/inicializarSimulacion`,
		(data: Simulacion) => {
			idSimulacion = data.id;
			simulacion = data;
		},
		(error) => {
			console.log(error);
		},
		simuData
	);
	if (simulacion === undefined) throw new Error("Error al crear simulacion");
	return simulacion;
};
