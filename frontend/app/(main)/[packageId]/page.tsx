"use client";
import { useState } from "react";
import Visualizator from "../simulation/_components/visualizator";
import CurrentStateBox from "./_components/current-state-box";
import { PackageStatusName, PackageStatusVariant } from "@/lib/types";
import { PackageRouteTable } from "./_components/package-route-table";

//TODO: packageId is stored under params.packageId

function TrackingPage({ params }: { params: { packageId: string } }) {
    const [packageData, setPackageData] = useState({
        id: "A43HDS5",
        origin: "Buenos Aires",
        destination: "Madrid",
        currentLocation: "En almacen origen",
        status: {
            color: "blue" as PackageStatusVariant,
            text: "Volando" as PackageStatusName,
        },
    });

    return (
        <main className="px-10 py-5 flex flex-row gap-5">
            <section className="w-[400px]">
                <h2>Paquete</h2>
                <h1 className="text-5xl">{packageData.id}</h1>
                <h2>{`${packageData.origin} -> ${packageData.destination}`}</h2>
                <CurrentStateBox className="mt-2" state={packageData.status} />

				<div className="mt-3">
					<p className="text-lg font-poppins">Tiempo de llegada a <span className="font-semibold text-yellow-600">parada</span></p>
					<h2 className="text-3xl">27 de marzo - 14:40</h2>
	
					<p className="text-lg font-poppins">Tiempo de llegada a <span className="font-semibold text-green-600">destino final</span></p>
					<h2 className="text-3xl">27 de marzo - 14:40</h2>
				</div>

				<h2 className="text-4xl text-center mt-4">Ruta de paquete</h2>

				<PackageRouteTable/>

            </section>

            <Visualizator className="" />
        </main>
    );
}
export default TrackingPage;
