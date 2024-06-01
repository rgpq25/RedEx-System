import { api } from "@/lib/api";
import { Simulacion } from "@/lib/types";

export const startWeeklySimulation = async (startDate: Date): Promise<Simulacion> => {
	let idSimulacion: number = 0;
	let simulacion: Simulacion | undefined = undefined;
	const finishDate = new Date(startDate);
	finishDate.setDate(finishDate.getDate() + 7);

	//@ts-expect-error
	const simuData: Simulacion = {
		fechaInicioSim: startDate,
		fechaFinSim: finishDate,
		fechaInicioSistema: new Date(),
		multiplicadorTiempo: 100,
	};

	await api(
		"POST",
		"http://localhost:8080/back/simulacion/inicializarSimulacion",
		(data: Simulacion) => {
			console.log(data);
			idSimulacion = data.id;
			simulacion = data;
		},
		(error) => {
			console.log(error);
		},
		simuData
	);

	console.log("Se usara el idSimulacion ", idSimulacion);
	if (simulacion === undefined) throw new Error("Error al crear simulacion");
	return simulacion;
};
