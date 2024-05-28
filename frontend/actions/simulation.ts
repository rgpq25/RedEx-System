import { api } from "@/lib/api";
import { Simulacion } from "@/lib/types";

export const startWeeklySimulation = async (enviosFile: File, startDate: Date): Promise<Simulacion> => {
	let idSimulacion: number = 0;
	let simulacion: Simulacion | undefined = undefined;
	const finishDate = new Date(startDate);
	finishDate.setDate(finishDate.getDate() + 7);

	await api(
		"POST",
		"http://localhost:8080/back/simulacion/",
		(data: Simulacion) => {
			console.log(data);
			idSimulacion = data.id;
			simulacion = data;
		},
		(error) => {
			console.log(error);
		},
		{
			fechaInicioSim: startDate,
			fechaFinSim: finishDate,
			fechaInicioSistema: new Date(),
			multiplicadorTiempo: 100,
			estado: 0,
		}
	);

	console.log("Se usara el idSimulacion ", idSimulacion);

	const readFileContent = (file: File): Promise<string> => {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();
			reader.onload = (event) => {
				if (event.target?.result) {
					resolve(event.target.result as string);
				} else {
					reject(new Error("Failed to read file"));
				}
			};
			reader.onerror = (event) => {
				reject(new Error(`File read error: ${event.target?.error?.code}`));
			};
			reader.readAsText(file);
		});
	};

	try {
		const fileContent = await readFileContent(enviosFile);
		const lines = fileContent.split("\n").filter((line) => line.trim());

		let conte = 0;
		for (const line of lines) {
			await api(
				"POST",
				"http://localhost:8080/back/envio/codigo",
				(data) => {
					console.log(data);
				},
				(error) => {
					console.log(error);
				},
				{
					codigo: line,
					simulacion: {
						id: idSimulacion,
					},
				}
			);

			conte++;
			if (conte === 500) break;
		}
	} catch (error) {
		console.error("Error processing file:", error);
	}

	if (simulacion === undefined) throw new Error("Error al crear simulacion");
	return simulacion;
};
