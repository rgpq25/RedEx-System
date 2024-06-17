"use client";

import { useState, useContext, useCallback, useEffect, useMemo } from "react";
import BreadcrumbCustom, { BreadcrumbItem } from "@/components/ui/breadcrumb-custom";
import { toast } from "sonner";

import { api, apiT } from "@/lib/api";
import { Envio, EstadoAlmacen, Paquete, RespuestaAlgoritmo } from "@/lib/types";
import { Aeropuerto, PackageStatusName, PackageStatusVariant, Vuelo } from "@/lib/types";

import CurrentTime from "@/app/_components/current-time";
import MainContainer from "../_components/main-container";
import MapHeader from "../_components/map-header";
import { Map } from "@/components/map/map";
import PlaneLegend from "@/app/_components/plane-legend";

import useMapZoom from "@/components/hooks/useMapZoom";
import { ShipmentIdContext } from "@/components/hooks/useShipmentId";
import useMapModals from "@/components/hooks/useMapModals";
import { Client } from "@stomp/stompjs";
import { structureDataFromRespuestaAlgoritmo } from "@/lib/map-utils";
import SidebarShipment from "@/app/_components/sidebar-shipment";

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
    // Attributes for intearctions
    const mapModalAttributes = useMapModals();
    const attributes = useMapZoom();
    const { currentTime, setCurrentTimeNoSimulation, zoomToAirport, lockToFlight } = attributes;
    const { openFlightModal, openAirportModal, openEnvioModal } = mapModalAttributes;

    // Shipment information
    const { shipmentId } = useContext(ShipmentIdContext);
    const [shipment, setShipment] = useState<Envio | undefined>(undefined);
    const [firstFetchPlanes, setFirstFetchPlanes] = useState<boolean>(true);
    const [shouldUpdateFlights, setShouldUpdateFlights] = useState<boolean>(false);

    // Shipment data fetching
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
        console.log("Updating flights of packages");
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
                await updateFlightsPackages();
            } catch (error) {
                console.log(error);
            }
        };

        handleReceptionData();
    }, []);
    useEffect(() => {
        const handleUpdateFlights = async () => {
            if (shipment && shouldUpdateFlights) {
                await getPackagesShipment();
                await updateFlightsPackages();
                setShouldUpdateFlights((prev) => false);
            }
        }
        handleUpdateFlights();
    }, [shipment, shouldUpdateFlights]);
    const cardInfoRender = useMemo(() => {
        return <SidebarShipment shipment={shipment} currentTime={currentTime}/>;
    }, [shipment, currentTime]);

    // Web Socket data fetching
    const [airports, setAirports] = useState<Aeropuerto[]>([]);
    const [flights, setFlights] = useState<Vuelo[]>([]);
    const [envios, setEnvios] = useState<Envio[]>([]);
    const [estadoAlmacen, setEstadoAlmacen] = useState<EstadoAlmacen | null>(null);
    const [client, setClient] = useState<Client | null>(null);
    const [isLoadingFirstTime, setIsLoadingFirstTime] = useState(false);

    useEffect(() => {
        async function getData() {
            await api(
                "GET",
                `${process.env.NEXT_PUBLIC_API}/back/aeropuerto/`,
                (data: Aeropuerto[]) => {
                    setAirports(data);
                },
                (error) => {
                    console.log(error);
                }
            );

            //Connect to the socket
            const socket = new WebSocket(`${process.env.NEXT_PUBLIC_SOCKET}/websocket`);
            const client = new Client({
                webSocketFactory: () => socket,
            });

            client.onConnect = () => {
                console.log("Connected to WebSocket");
                client.subscribe("/algoritmo/diaDiaRespuesta", (msg) => {
                    console.log("MENSAJE DE /algoritmo/diaDiaRespuesta: ", JSON.parse(msg.body));
                    const data: RespuestaAlgoritmo = JSON.parse(msg.body);

                    if (data.iniciandoPrimeraPlanificacionDiaDia === false) {
                        setIsLoadingFirstTime(false);
                    }

                    const { db_vuelos, db_envios, db_estadoAlmacen } = structureDataFromRespuestaAlgoritmo(data);

                    setFlights(db_vuelos);
                    setEnvios(db_envios);
                    setEstadoAlmacen(db_estadoAlmacen);

                    setShouldUpdateFlights((prev) => true);
                });
                client.subscribe("/algoritmo/diaDiaEstado", (msg) => {
                    console.log("MENSAJE DE /algoritmo/diaDiaEstado: ", msg.body);
                });
            };

            client.activate();
            setClient(client);

            console.log("Getting data");
            await api(
                "GET",
                `${process.env.NEXT_PUBLIC_API}/back/operacionesDiaDia/diaDiaRespuesta`,
                async (data: RespuestaAlgoritmo) => {
                    console.log("DATA DE operacionesDiaDia/diaDiaRespuesta: ", data);
                    setCurrentTimeNoSimulation();

                    if (data.iniciandoPrimeraPlanificacionDiaDia === true) {
                        setIsLoadingFirstTime(true);
                        return;
                    }

                    setShouldUpdateFlights((prev) => true);

                    const { db_vuelos, db_envios, db_estadoAlmacen } = structureDataFromRespuestaAlgoritmo(data);

                    setFlights(db_vuelos);
                    setEnvios(db_envios);
                    setEstadoAlmacen(db_estadoAlmacen);
                },
                (error) => {
                    console.log(error);
                }
            );
        }

        getData();
    }, []);
    useEffect(() => {
        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, []);
    const filteredFlights = useMemo(() => {
        if (!shipment) return flights;
        return flights.filter((flight) => {
            return shipment.paquetes?.some((paquete) => {
                return paquete.planRutaActual?.vuelos?.some((vuelo) => {
                    return vuelo.id === flight.id;
                });
            });
        });
    }, [flights, shipment]);

    return (
        <MainContainer className='relative'>
            <MapHeader>
                <BreadcrumbCustom items={breadcrumbItems} />
                <div className='flex flex-row gap-4 items-center'>
                    <h1 className='text-4xl font-bold font-poppins'>Envío en tiempo real</h1>
                    <CurrentTime currentTime={currentTime} />
                </div>
            </MapHeader>
            <PlaneLegend className='absolute top-10 right-14 z-[50]' />
            {cardInfoRender}
            <Map
                isSimulation={false}
                mapModalAttributes={mapModalAttributes}
                attributes={attributes}
                className='absolute top-1 bottom-3 left-3 right-3'
                //airports={airports}
                flights={filteredFlights}
                estadoAlmacen={estadoAlmacen}
                simulation={undefined}
            />
        </MainContainer>
    );
}

export default TrackingPage;
