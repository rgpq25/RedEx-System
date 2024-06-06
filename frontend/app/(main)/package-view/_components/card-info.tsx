"use client";

import { useState } from "react";
import { cn } from "@/lib/utils";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import CurrentStateBox from "./current-state-box";
import { PackageRouteTable } from "./package-route-table";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Button } from "@/components/ui/button";
import { ChevronsLeft, ChevronsRight } from "lucide-react";
import { H1, H2, H3, Muted, Small } from "@/components/ui/typography";
import { Envio, Paquete, Vuelo } from "@/lib/types";
import { Separator } from "@/components/ui/separator";
import { formatDateTimeLongShort } from "@/lib/date";

function CardInfo({ shipment }: { shipment: Envio }) {
    const [visible, setVisible] = useState<boolean>(true);
    const [selectedIdPackage, setSelectedIdPackage] = useState<number>();
    const selectedPackage = shipment?.paquetes?.find((pkg: Paquete) => pkg.id === selectedIdPackage);

    return (
        <>
            <Button
                onClick={() => setVisible(true)}
                className={cn(
                    "absolute top-[105px] left-[45px] gap-1 transition-opacity duration-500 ease-in-out delay-200 z-[100]",
                    visible ? "opacity-0" : "opacity-100"
                )}
            >
                <p>Ver información</p>
                <ChevronsRight />
            </Button>
            <Card
                className={cn(
                    "w-[450px] flex flex-col absolute top-[105px] bottom-[43px] z-[100]",
                    "transition-all duration-300 ease-in-out",
                    visible ? "left-[43px]" : "-left-[470px]"
                )}
            >
                <CardHeader className='relative'>
                    <div className='flex flex-row items-center justify-between'>
                        <H2>{`ID: ${shipment.id}`}</H2>
                        <ChevronsLeft
                            className='cursor-pointer w-10 h-10 p-2 stroke-muted-foreground hover:stroke-black transition-all duration-200 ease-in-out'
                            onClick={() => {
                                console.log("Clckng");
                                setVisible(false);
                            }}
                        >
                            Cerrar
                        </ChevronsLeft>
                    </div>
                </CardHeader>
                <CardContent className='w-full flex space-y-4 flex-col *:w-full overflow-hidden'>
                    {/* <div className='text-center flex flex-col justify-center items-center'>
                        <p className='text-lg font-poppins tracking-tight'>
                            Tiempo de llegada a <span className='font-semibold text-green-600'>destino final:</span>
                        </p>
                        <h2 className='text-3xl font-semibold tracking-wide font-poppins'>27 de marzo - 14:40</h2>
                    </div> */}
                    <div className='flex flex-col w-full items-start'>
                        <H3 className='mb-2'>Envio</H3>
                        <Muted>{`Ubicación origen: ${shipment.ubicacionOrigen.ciudad} (${shipment.ubicacionOrigen.ciudadAbreviada.toUpperCase()})`}</Muted>
                        <Muted>{`Ubicación destino: ${shipment.ubicacionDestino.ciudad} (${shipment.ubicacionDestino.ciudadAbreviada.toUpperCase()})`}</Muted>
                        <Muted>{`Fecha de registro: ${formatDateTimeLongShort(shipment.fechaRecepcion)}`}</Muted>
                        <Muted>{`Fecha de limite: ${formatDateTimeLongShort(shipment.fechaLimiteEntrega)}`}</Muted>
                        <Muted>{`Cantidad de paquetes: ${shipment?.paquetes?.length}`}</Muted>
                    </div>
                    <Separator />
                    <div className='flex flex-col space-y-4 w-full items-start'>
                        <H3>Paquetes</H3>
                        <Select
                            key={selectedIdPackage}
                            onValueChange={(value) => setSelectedIdPackage(parseInt(value))}
                            defaultValue={selectedIdPackage?.toString()}
                            value={selectedIdPackage?.toString()}
                        >
                            <SelectTrigger>
                                <SelectValue placeholder={selectedIdPackage || "Seleccione un paquete"} />
                            </SelectTrigger>
                            <SelectContent className='z-[120]'>
                                <SelectGroup>
                                    {shipment?.paquetes?.map((pkg: any) => (
                                        <SelectItem
                                            key={pkg.id}
                                            value={pkg.id.toString()}
                                        >
                                            {`${pkg.id}`}
                                        </SelectItem>
                                    ))}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <H3>Ruta de paquetes</H3>
                    <ScrollArea className='flex flex-col'>
                        {/* <CurrentStateBox
                            className=''
                            state={shipment.status}
                        /> */}

                        {/* <div className='mt-2 text-center flex-1 flex flex-col justify-center items-center'>
                            <p className='text-lg font-poppins tracking-tight'>
                                Tiempo de llegada a <span className='font-semibold text-green-600'>destino final:</span>
                            </p>
                            <h2 className='text-3xl font-semibold tracking-wide font-poppins'>27 de marzo - 14:40</h2>
                        </div> */}

                        {selectedPackage?.planRutaActual?.vuelos?.map((vuelo: Vuelo) => (
                            <section
                                key={vuelo.id}
                                className='flex flex-row my-4 justify-between *:flex *:flex-col px-2'
                            >
                                <div className='*:text-left'>
                                    <Small>{vuelo.planVuelo.ciudadOrigen.ciudad}</Small>
                                    <Muted>{formatDateTimeLongShort(vuelo.fechaSalida)}</Muted>
                                </div>
                                <div className='*:text-right'>
                                    <Small>{vuelo.planVuelo.ciudadDestino.ciudad}</Small>
                                    <Muted>{formatDateTimeLongShort(vuelo.fechaLlegada)}</Muted>
                                </div>
                            </section>
                        ))}
                    </ScrollArea>
                </CardContent>
            </Card>
        </>
    );
}
export default CardInfo;
