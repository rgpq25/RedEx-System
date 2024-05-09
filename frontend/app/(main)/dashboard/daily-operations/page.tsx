"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import Map from "@/components/map/map";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { envios, vuelos } from "@/lib/sample";
import { Aeropuerto, Envio, Vuelo } from "@/lib/types";
import { Clock } from "lucide-react";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../../_components/main-container";
import { useState } from "react";
import useApi from "@/components/hooks/useApi";
import { toast } from "sonner";

const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Acceso",
		link: "/security-code",
	},
	{
		label: "Dashboard",
		link: "/dashboard",
	},
	{
		label: "Operaciones día a día",
		link: "/dashboard/daily-operations",
	},
];

function DailyOperationsPage() {
	const attributes = useMapZoom();

	const { centerLatitude, centerLongitude, currentTime, lockInFlight, unlockFlight, zoom, zoomIn } = attributes;

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
		}
	);

	return (
		<MainContainer>
			<BreadcrumbCustom items={breadcrumbItems} />
			<div className="flex flex-row justify-between items-center">
				<div className="flex flex-row gap-4 items-center ">
					<h1 className="text-4xl font-bold font-poppins">Operaciones día a día</h1>
					<CurrentTime currentTime={currentTime} />
				</div>
				<PlaneLegend />
			</div>
			<section className="relative mt-[10px] flex-1 flex overflow-hidden">
				<Sidebar
					aeropuertos={airports}
					envios={envios}
					vuelos={vuelos}
					onClickEnvio={(envio: Envio) => {
						console.log("PENDIENTE HACER ZOOM EN VUELO DONDE SE ENCUENTRA PAQUETE");
						toast.error("Pendiente de implementar")
					}}
					onClickAeropuerto={(aeropuerto: Aeropuerto) => {
						const longitude = aeropuerto.ubicacion.longitud;
						const latitude = aeropuerto.ubicacion.latitud;
						unlockFlight();
						zoomIn([longitude, latitude] as [number, number]);
					}}
					onClickVuelo={(vuelo: Vuelo) => {
						lockInFlight(vuelo);
					}}
				/>
				<Map attributes={attributes} className="h-full w-full" airports={airports} />
			</section>
		</MainContainer>
	);
}
export default DailyOperationsPage;
