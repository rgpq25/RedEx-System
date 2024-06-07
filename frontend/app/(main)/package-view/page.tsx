"use client";
import { useState, useContext, useCallback, useEffect, useMemo } from "react";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { toast } from "sonner";

import { api, apiT } from "@/lib/api";
import { Envio, Paquete } from "@/lib/types";
import { Aeropuerto, PackageStatusName, PackageStatusVariant, Vuelo } from "@/lib/types";

import CurrentStateBox from "./_components/current-state-box";
import { PackageRouteTable } from "./_components/package-route-table";
import CardInfo from "./_components/card-info";
import MainContainer from "../_components/main-container";
import MapHeader from "../_components/map-header";
import { Map } from "@/components/map/map";
import PlaneLegend from "@/app/_components/plane-legend";

import useMapZoom from "@/components/hooks/useMapZoom";
import useApi from "@/components/hooks/useApi";
import { ShipmentIdContext } from "@/components/hooks/useShipmentId";
import useMapModals from "@/components/hooks/useMapModals";

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

    const { shipmentId } = useContext(ShipmentIdContext);
    const [airports, setAirports] = useState<Aeropuerto[]>([]);
    const [currentAirportModal, setCurrentAirportModal] = useState<Aeropuerto | undefined>(undefined);
    const [currentFlightModal, setCurrentFlightModal] = useState<Vuelo | undefined>(undefined);
    const [shipment, setShipment] = useState<Envio | undefined>(undefined);

    const { isLoading: isLoadingAirports } = useApi(
        "GET",
        `${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
        (data: Aeropuerto[]) => {
            setAirports(data);
        },
        (error) => {
            console.log(error);
        }
    );

    const getShipment = async () => {
        try {
            const data = await apiT<Envio>("GET", `${process.env.NEXT_PUBLIC_API}/back/envio/${shipmentId}`);
            setShipment(data);
        } catch (error) {
            toast.error("No se encontró el envío", {
                position: "bottom-right",
                duration: 3000,
            });
        }
    };

    const getPackagesShipment = async () => {
        try {
            const data = await apiT<Paquete[]>("GET", `${process.env.NEXT_PUBLIC_API}/back/paquete/envio_sin_simulacion/${shipmentId}`);
            if (data.length === 0 || data === undefined) {
                toast.error("No se encontraron paquetes del respectivo envío", {
                    position: "bottom-right",
                    duration: 3000,
                });
                return;
            }
            setShipment((prevShipment) => {
                if (!prevShipment) return prevShipment;
                return { ...prevShipment, paquetes: data };
            });
        } catch (error) {
            toast.error("No se encontraron paquetes del respectivo envío", {
                position: "bottom-right",
                duration: 3000,
            });
        }
    };

    const updateFlightsPackages = async () => {
        try {
            const packagesPromises = shipment?.paquetes?.map(async (pkg) => {
                const idPackage = pkg?.id;
                const data = await apiT<Vuelo[]>("GET", `${process.env.NEXT_PUBLIC_API}/back/vuelo/paquete/${idPackage}`);

                return data;
            });
            const paqueteDataResults: Vuelo[][] = await Promise.all(packagesPromises || []);
            setShipment((prevShipment: Envio | undefined) => {
                if (!prevShipment) return undefined;
                if (!prevShipment.paquetes) return prevShipment;

                const updatedPaquetes = prevShipment.paquetes.map((paquete, index) => {
                    if (!paquete.planRutaActual) return paquete;
                    const updatedPlanRutaActual = {
                        ...paquete.planRutaActual,
                        vuelos: paqueteDataResults[index],
                    };

                    return {
                        ...paquete,
                        planRutaActual: updatedPlanRutaActual,
                    };
                });

                return {
                    ...prevShipment,
                    paquetes: updatedPaquetes,
                };
            });
        } catch (error) {
            toast.error("Error al actualizar los vuelos de los paquetes", {
                position: "bottom-right",
                duration: 3000,
            });
        }
    };

    useEffect(() => {
        const handleReceptionData = async () => {
            try {
                await getShipment();
                await getPackagesShipment();
            } catch (error) {
                console.log(error);
            }
        };

        handleReceptionData();
    }, []);

    useEffect(() => {
        if (shipment?.paquetes) {
            updateFlightsPackages();
        }
    }, [shipment]);

    const cardInfoRender = useMemo(() => {
        if (!shipment) return null;
        return <CardInfo shipment={shipment} />;
    }, [shipment]);

    return (
        <MainContainer className='relative'>
            <MapHeader>
                <BreadcrumbCustom items={breadcrumbItems} />
                <div className='flex flex-row gap-4 items-center'>
                    <h1 className='text-4xl font-bold font-poppins'>Envío en tiempo real</h1>
                </div>
                {/* <PlaneLegend /> */}
            </MapHeader>
            {/* <div className="w-full h-full flex flex-row gap-5 relative overflow-hidden mt-[10px]"> */}
            {cardInfoRender}
            <Map
                className='absolute top-1 bottom-3 left-3 right-3'
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
