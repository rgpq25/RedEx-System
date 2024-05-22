"use client";
import { useState } from "react";
import CurrentStateBox from "./_components/current-state-box";
import { Aeropuerto, PackageStatusName, PackageStatusVariant, Vuelo } from "@/lib/types";
import { PackageRouteTable } from "./_components/package-route-table";
import Map from "@/components/map/map";
import CardInfo from "./_components/card-info";
import useMapZoom from "@/components/hooks/useMapZoom";
import MainContainer from "../_components/main-container";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import PlaneLegend from "@/app/_components/plane-legend";
import useApi from "@/components/hooks/useApi";

const breadcrumbItems: BreadcrumbItem[] = [
	{
		label: "Acceso",
		link: "/security-code",
	},
	{
		label: "Envío en tiempo real",
		link: "/[packageId]",
	},
];

function TrackingPage({ params }: { params: { packageId: string } }) {
	const attributes = useMapZoom();
	const { currentTime, zoom, centerLongitude, centerLatitude, zoomIn, lockInFlight, unlockFlight } = attributes;

	const [airports, setAirports] = useState<Aeropuerto[]>([]);
	const [currentAirportModal, setCurrentAirportModal] = useState<Aeropuerto | undefined>(undefined);
	const [currentFlightModal, setCurrentFlightModal] = useState<Vuelo | undefined>(undefined);
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
					<h1 className="text-4xl font-bold font-poppins">Envío en tiempo real</h1>
				</div>
				<PlaneLegend />
			</div>
			<div className="w-full h-full flex flex-row gap-5 relative overflow-hidden mt-[10px]">
				<CardInfo shipment={shipment} />

				<Map
					currentAirportModal={currentAirportModal}
					currentFlightModal={currentFlightModal}
					setCurrentAirportModal={setCurrentAirportModal}
					setCurrentFlightModal={setCurrentFlightModal}
					className="max-h-full"
					attributes={attributes}
					airports={airports}
				/>
			</div>
		</MainContainer>
	);
}
export default TrackingPage;
