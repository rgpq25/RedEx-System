"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import Map from "@/components/map/map";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { aeropuertos, envios, vuelos } from "@/lib/sample";
import { Aeropuerto, Envio, Vuelo } from "@/lib/types";
import { Clock } from "lucide-react";
import CurrentTime from "@/app/_components/current-time";
import PlaneLegend from "@/app/_components/plane-legend";
import MainContainer from "../../_components/main-container";

const breadcrumbItems: BreadcrumbItem[] = [
    {
		label: "Acceso",
		link: "/security-code"
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
					aeropuertos={aeropuertos}
					envios={envios}
					vuelos={vuelos}
					className="absolute top-4 left-4 bottom-4 z-20"
					onClickEnvio={(envio: Envio) => {
						console.log("PENDIENTE HACER ZOOM EN VUELO DONDE SE ENCUENTRA PAQUETE");
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
				<Map attributes={attributes} className="h-full w-full" />
			</section>
		</MainContainer>
	);
}
export default DailyOperationsPage;
