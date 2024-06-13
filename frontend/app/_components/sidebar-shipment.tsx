"use client";

import { cn } from "@/lib/utils";

import { useState } from "react";

import { ChevronsRight, ChevronsLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { H2, H3, Muted, Small } from "@/components/ui/typography";

import { Envio, Paquete, Vuelo } from "@/lib/types";
import { formatDateTimeLongShort } from "@/lib/date";
import Chip from "@/components/ui/chip";
import { ChipColors, PackageStatusVariant } from "@/lib/types";

const badgePackageStatusData = [
    { name: "En almacén origen", color: "gray" as ChipColors },
    { name: "Volando", color: "blue" as ChipColors },
    { name: "En espera", color: "yellow" as ChipColors },
    { name: "En almacén destino", color: "purple" as ChipColors },
    { name: "Entregado", color: "green" as ChipColors },
    { name: "Atrasado", color: "red" as ChipColors },
];

const SidebarShipment = ({ shipment }: { shipment: Envio }) => {
    const [visible, setVisible] = useState<boolean>(true);
    const [selectedIdPackage, setSelectedIdPackage] = useState<number>();
    const selectedPackage = shipment?.paquetes?.find((pkg: Paquete) => pkg.id === selectedIdPackage);
    const chipData = badgePackageStatusData.find((data) => data.name === selectedPackage?.estado);

    return (
        <>
            <Button
                onClick={() => setVisible(true)}
                className={cn(
                    "absolute top-[105px] left-[45px] gap-1 transition-opacity duration-500 ease-in-out delay-200 z-[20]",
                    visible ? "opacity-0" : "opacity-100"
                )}
            >
                <p>Ver información</p>
                <ChevronsRight />
            </Button>
            <Card
                className={cn(
                    "flex w-96 flex-col items-center overflow-hidden transition-all duration-300 ease-in-out absolute top-[105px] bottom-[43px] z-[20] border border-gray-300",
                    visible ? "left-[43px]" : "-left-[404px]"
                )}
            >
                <CardHeader className='relative flex flex-row w-full items-center justify-between'>
                    <H2 className='flex-1'>{`ID: ${shipment.id}`}</H2>
                    <ChevronsLeft
                        className='cursor-pointer w-10 h-10 p-2 stroke-muted-foreground hover:stroke-black transition-all duration-200 ease-in-out'
                        onClick={() => {
                            console.log("Clckng");
                            setVisible(false);
                        }}
                    >
                        Cerrar
                    </ChevronsLeft>
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
                        <Muted>{`Origen: ${shipment.ubicacionOrigen.ciudad} (${shipment.ubicacionOrigen.ciudadAbreviada.toUpperCase()})`}</Muted>
                        <Muted>{`Destino: ${shipment.ubicacionDestino.ciudad} (${shipment.ubicacionDestino.ciudadAbreviada.toUpperCase()})`}</Muted>
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
                    <div className="flex flex-col">
                        <H3>Ruta del paquete</H3>
                        {chipData && (
                            <Chip
                                className='mt-2'
                                color={chipData.color as ChipColors}
                            >
                                {chipData.name}
                            </Chip>
                        )}
                    </div>
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

                        {selectedPackage === undefined || selectedPackage?.planRutaActual === undefined ? (
                            <Muted>No hay planeamiento activo.</Muted>
                        ) : (
                            <>
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
                            </>
                        )}
                    </ScrollArea>
                </CardContent>
            </Card>
        </>
    );
};

export default SidebarShipment;
