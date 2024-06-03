"use client";
import { useState } from "react";
import CurrentStateBox from "./_components/current-state-box";
import { Aeropuerto, PackageStatusName, PackageStatusVariant, Vuelo } from "@/lib/types";
import { PackageRouteTable } from "./_components/package-route-table";
import CardInfo from "./_components/card-info";
import useMapZoom from "@/components/hooks/useMapZoom";
import MainContainer from "../_components/main-container";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import PlaneLegend from "@/app/_components/plane-legend";
import useApi from "@/components/hooks/useApi";
import { Map } from "@/components/map/map";
import useMapModals from "@/components/hooks/useMapModals";
import MapHeader from "../_components/map-header";

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

function TrackingPage() {
	const mapModalAttributes = useMapModals();
	const attributes = useMapZoom();
	//const { } = attributes;

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
		`${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
		(data: Aeropuerto[]) => {
			console.log(data);
			setAirports(data);
		},
		(error) => {
			console.log(error);
		}
	);

	return (
		<MainContainer className="relative">
			<MapHeader>
				<BreadcrumbCustom items={breadcrumbItems} />
				<div className="flex flex-row gap-4 items-center ">
					<h1 className="text-4xl font-bold font-poppins">Envío en tiempo real</h1>
				</div>
				{/* <PlaneLegend /> */}
			</MapHeader>
			{/* <div className="w-full h-full flex flex-row gap-5 relative overflow-hidden mt-[10px]"> */}
				<CardInfo shipment={shipment} />

				<Map
					className="absolute top-1 bottom-3 left-3 right-3"
					attributes={attributes}
					estadoAlmacen={null}
					flights={[]}
					isSimulation={false}
					mapModalAttributes={mapModalAttributes}
					simulation={undefined}
				/>
			{/* </div> */}
		</MainContainer>
	);
}
export default TrackingPage;
