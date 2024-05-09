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

import { vuelos, envios } from "@/lib/sample";
import { Aeropuerto, Envio } from "@/lib/types";
import useMapZoom from "@/components/hooks/useMapZoom";
import { getFlightPosition } from "@/lib/map-utils";
import { ModalIntro } from "./_components/modal-intro";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../_components/main-container";
import useApi from "@/components/hooks/useApi";
import { toast } from "sonner";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";

const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Acceso",
		link: "/security-code",
	},
	{
		label: "Simulación",
		link: "/simulation",
	},
];

function SimulationPage() {
	const [tab, setTab] = useState<TabType>("weekly");
	const [isModalOpen, setIsModalOpen] = useState(true);

	const attributes = useMapZoom();
	const { currentTime, zoom, centerLongitude, centerLatitude, zoomIn, lockInFlight, unlockFlight } = attributes;

	const [airports, setAirports] = useState<Aeropuerto[]>([]);

	const { isLoading } = useApi(
		"GET",
		"http://localhost:8080/back/aeropuerto/",
		(data: Aeropuerto[]) => {
			console.log(data);
			setAirports(data);
		},
		(error) => {
			console.log(error);
			toast.error("Error al cargar aeropuertos");
		}
	);

	return (
		<>
			<ModalIntro isOpen={isModalOpen} setIsModalOpen={setIsModalOpen} />
			<MainContainer>
				<BreadcrumbCustom items={breadcrumbItems} />
				<div className="flex flex-row justify-between items-center">
					<div className="flex flex-row gap-4 items-center ">
						<h1 className="text-4xl font-bold font-poppins">Visualizador de simulación</h1>
						<CurrentTime currentTime={currentTime} />
					</div>
					<PlaneLegend />
				</div>

				<section className="relative flex-1 mt-[10px] overflow-hidden">
					<Map attributes={attributes} className="h-full w-full" airports={airports} />
					<Sidebar
						envios={envios}
						vuelos={vuelos}
						aeropuertos={airports}
						onClickEnvio={(envio: Envio) => {
							console.log("PENDIENTE HACER ZOOM EN VUELO DONDE SE ENCUENTRA PAQUETE");
						}}
						onClickAeropuerto={(aeropuerto) => {
							const longitude = aeropuerto.ubicacion.longitud;
							const latitude = aeropuerto.ubicacion.latitud;
							unlockFlight();
							zoomIn([longitude, latitude] as [number, number]);
						}}
						onClickVuelo={(vuelo) => {
							lockInFlight(vuelo);
						}}
					/>
				</section>
			</MainContainer>
		</>
	);
}

export default SimulationPage;
