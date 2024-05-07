"use client";
import { useState } from "react";
import CurrentStateBox from "./_components/current-state-box";
import { PackageStatusName, PackageStatusVariant } from "@/lib/types";
import { PackageRouteTable } from "./_components/package-route-table";
import Map from "@/components/map/map";
import CardInfo from "./_components/card-info";
import useMapZoom from "@/components/hooks/useMapZoom";
import MainContainer from "../_components/main-container";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import PlaneLegend from "@/app/_components/plane-legend";


const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Acceso",
		link: "/security-code"
	},
	{
		label: "Envío en tiempo real",
		link: "/[packageId]"
	}
]

function TrackingPage({ params }: { params: { packageId: string } }) {
	const [shipment, setShipment] = useState({
		id: "A43HDS5",
		origin: "Buenos Aires",
		destination: "Madrid",
		currentLocation: "En almacen origen",
		status: {
			color: "blue" as PackageStatusVariant,
			text: "Volando" as PackageStatusName,
		},
	});

	const attributes = useMapZoom();
	const { currentTime, zoom, centerLongitude, centerLatitude, zoomIn, lockInFlight, unlockFlight } = attributes;

	return (
		<MainContainer>
			<BreadcrumbCustom items={breadcrumbItems} />
			<div className="flex flex-row justify-between items-center">
				<div className="flex flex-row gap-4 items-center ">
					<h1 className="text-4xl font-bold font-poppins">Envío en tiempo real</h1>
				</div>
				<PlaneLegend />
			</div>
			<div className="w-full h-full flex flex-row gap-5 relative overflow-hidden mt-[10px]">
				<CardInfo shipment={shipment} />

				<Map className="max-h-full" attributes={attributes} />
			</div>
		</MainContainer>
	);
}
export default TrackingPage;
