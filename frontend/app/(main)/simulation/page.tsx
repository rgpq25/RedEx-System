"use client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Info, Settings } from "lucide-react";
import { useEffect, useState } from "react";
import { PackageTable } from "./_components/package-table";
import Visualizator from "./_components/visualizator";
import InfoNotation1 from "./_components/info-notation1";
import Sidebar from "@/app/_components/sidebar";
import Map from "@/components/map/map";

type TabType = "weekly" | "colapse";

import { vuelos, aeropuertos, envios } from "@/lib/sample";
import { Envio } from "@/lib/types";
import useMapZoom from "@/components/hooks/useMapZoom";
import { getFlightPosition } from "@/lib/map-utils";

function SimulationPage() {
	const [tab, setTab] = useState<TabType>("weekly");

	const { currentTime, zoom, centerLongitude, centerLatitude, zoomIn, lockInFlight, unlockFlight } = useMapZoom();

	return (
		<main className="px-10 py-5">
			<h1>Visualizador de simulación</h1>
			<section className="relative h-[800px] overflow-hidden">
				<Map
					currentTime={currentTime}
					zoom={zoom}
					centerLongitude={centerLongitude}
					centerLatitude={centerLatitude}
					zoomIn={zoomIn}
					lockInFlight={lockInFlight}
                    unlockFlight={unlockFlight}
					className="h-full w-full"
				/>
				<Sidebar
					envios={envios}
					vuelos={vuelos}
					aeropuertos={aeropuertos}
					className="absolute top-4 left-4 bottom-4"
					onClickEnvio={(envio: Envio) => {
						console.log("PENDIENTE HACER ZOOM EN VUELO DONDE SE ENCUENTRA PAQUETE");
					}}
					onClickAeropuerto={(aeropuerto) => {
						const longitude = aeropuerto.ubicacion.coordenadas.longitud;
						const latitude = aeropuerto.ubicacion.coordenadas.latitud;
                        unlockFlight();
						zoomIn([longitude, latitude] as [number, number]);
					}}
					onClickVuelo={(vuelo) => {
						lockInFlight(vuelo);
					}}
				/>
			</section>
			<section className="flex flex-col 2xl:flex-row 2xl:gap-10 gap-4  w-full mt-3">
				<div className="flex flex-col 2xl:w-[600px]">
					<div className="flex items-center ">
						<Settings className="stroke-[1.9px]" />
						<h2 className="ml-1 mr-4">Configuracion</h2>

						<Tabs defaultValue="weekly" value={tab} onValueChange={(e) => setTab(e as TabType)}>
							<TabsList>
								<TabsTrigger value="weekly">Pronostico futuro</TabsTrigger>
								<TabsTrigger value="colapse">Hasta colapso</TabsTrigger>
							</TabsList>
						</Tabs>
					</div>

					<InfoNotation1 />

					<div className="flex  items-end gap-1 mt-3">
						<div className="flex-1">
							<Label>Entrada de datos Excel</Label>
							<Input type="text" />
						</div>
						<Button>Subir archivo</Button>
					</div>

					<Button className="mx-auto mt-6" size="lg">
						Empezar simulación
					</Button>
				</div>

				{/* <PackageTable /> */}
			</section>
		</main>
	);
}

export default SimulationPage;
