"use client";
import Sidebar from "@/app/_components/sidebar";
import useMapZoom from "@/components/hooks/useMapZoom";
import Map from "@/components/map/map";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { aeropuertos, envios, vuelos } from "@/lib/sample";
import { Aeropuerto, Envio, Vuelo } from "@/lib/types";
import { Clock } from "lucide-react";
import PlaneLegend from "./_components/plane-legend";

const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Dashboard",
		link: "/dashboard",
	},
	{
		label: "Operaciones dia a dia",
		link: "/dashboard/daily-operations",
	},
];

function DailyOperationsPage() {
	const attributes = useMapZoom();

	const { centerLatitude, centerLongitude, currentTime, lockInFlight, unlockFlight, zoom, zoomIn } = attributes;

	return (
		<main className="flex-1 min-h-[800px] pt-5 px-10 pb-10 flex flex-col">
			<BreadcrumbCustom items={breadcrumbItems} />
			<div className="flex flex-row justify-between items-center">
				<div className="flex flex-row gap-4 items-center ">
				    <h1 className="text-4xl font-bold font-poppins">Operaciones dia a dia</h1>
    				<div className="flex flex-row gap-1 border rounded-3xl border-blue-700 w-[200px] text-blue-700 stroke-blue-700 p-1">
    					<Clock />
    					<p>
    						{attributes.currentTime.toLocaleDateString() +
    							" " +
    							attributes.currentTime.toLocaleTimeString()}
    					</p>
    				</div>
				</div>
                <PlaneLegend/>
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
						const longitude = aeropuerto.ubicacion.coordenadas.longitud;
						const latitude = aeropuerto.ubicacion.coordenadas.latitud;
						unlockFlight();
						zoomIn([longitude, latitude] as [number, number]);
					}}
					onClickVuelo={(vuelo: Vuelo) => {
						lockInFlight(vuelo);
					}}
				/>
				<Map attributes={attributes} className="h-full w-full" />
			</section>
		</main>
	);
}
export default DailyOperationsPage;
