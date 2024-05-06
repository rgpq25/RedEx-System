"use client";
import { useState } from "react";
import CurrentStateBox from "./_components/current-state-box";
import { PackageStatusName, PackageStatusVariant } from "@/lib/types";
import { PackageRouteTable } from "./_components/package-route-table";
import Map from "@/components/map/map";
import CardInfo from "./_components/card-info";

//TODO: packageId is stored under params.packageId

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

    return (
        <main className="px-10 py-5 flex flex-row border flex-1 overflow-hidden">
            <div className="w-full h-full flex flex-row gap-5 relative overflow-hidden">
                <CardInfo shipment={shipment}/>
    
                <Map className="max-h-full" />
            </div>
        </main>
    );
}
export default TrackingPage;
