"use client";

import { cn } from "@/lib/utils";
import { useState, useEffect, useMemo } from "react";
import { ChevronsRight, ChevronsLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { H2, H3, Large, Lead, Muted, Small } from "@/components/ui/typography";
import {
    Step,
    StepDescription,
    StepIcon,
    StepIndicator,
    StepNumber,
    StepSeparator,
    StepStatus,
    StepTitle,
    Stepper,
    useSteps,
} from "@chakra-ui/react";
import { Box } from "@chakra-ui/react";
import { Envio, Paquete, Vuelo } from "@/lib/types";
import { formatDateTimeLongShort } from "@/lib/date";
import Chip from "@/components/ui/chip";
import { ChipColors, PackageStatusVariant } from "@/lib/types";
import { getPackageState } from "@/components/map/envio-modal";
import { EstadoPaquete } from "@/lib/types";

const badgePackageStatusData = [
    { name: "En almacén origen", color: "gray" as ChipColors },
    { name: "Volando", color: "blue" as ChipColors },
    { name: "En espera", color: "yellow" as ChipColors },
    { name: "En almacén destino", color: "purple" as ChipColors },
    { name: "Entregado", color: "green" as ChipColors },
    { name: "Atrasado", color: "red" as ChipColors },
];

const StatusChip = ({ status }: { status: EstadoPaquete }) => (
    <Chip
        color={status.color}
        className='ml-4'
    >
        {status.descripcion}
    </Chip>
);

const calculateRemainingTime = (fechaLlegadaUltimoVuelo: Date, currentTime: Date) => {
    const diffTime = fechaLlegadaUltimoVuelo.getTime() - currentTime.getTime();
    if (diffTime < 0) return "0 minutos";
    const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
    const diffMinutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60));
    return diffHours === 0 ? `${diffMinutes} minutos` : `${diffHours} horas y ${diffMinutes} minutos`;
};

const generateAeropuertosData = (selectedPackage: Paquete, shipment: Envio) => {
    return selectedPackage?.planRutaActual?.vuelos?.map((vuelo: Vuelo, index: number) => {
        const aeropuertoData = {
            type: "aeropuerto",
            id: `${vuelo.id}-aeropuerto`,
            ciudad: vuelo.planVuelo.ciudadOrigen.ciudad,
            fechaSalida: vuelo.fechaSalida,
            tiempoEnAlmacen: (() => {
                if (index === 0) {
                    let tiempoRegistro = new Date(shipment?.fechaRecepcion);
                    let tiempoSalida = new Date(vuelo?.fechaSalida);
                    if (isNaN(tiempoRegistro.getTime()) || isNaN(tiempoSalida.getTime())) {
                        return "Fecha no válida";
                    }
                    let diffTime = tiempoSalida.getTime() - tiempoRegistro.getTime();
                    let diffHours = Math.floor(diffTime / (1000 * 60 * 60));
                    let diffMinutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60));
                    return diffHours === 0 ? `${diffMinutes} minutos` : `${diffHours} horas y ${diffMinutes} minutos`;
                } else {
                    if (selectedPackage?.planRutaActual?.vuelos[index - 1] === undefined) return "";
                    let tiempoLlegada = new Date(selectedPackage?.planRutaActual?.vuelos[index - 1]?.fechaLlegada);
                    let tiempoSalida = new Date(vuelo.fechaSalida);
                    if (isNaN(tiempoLlegada.getTime()) || isNaN(tiempoSalida.getTime())) {
                        return "Fecha no válida";
                    }
                    let diffTime = tiempoSalida.getTime() - tiempoLlegada.getTime();
                    let diffHours = Math.floor(diffTime / (1000 * 60 * 60));
                    let diffMinutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60));
                    return diffHours === 0 ? `${diffMinutes} minutos` : `${diffHours} horas y ${diffMinutes} minutos`;
                }
            })(),
        };
        return aeropuertoData;
    });
};

const generateVuelosData = (selectedPackage: Paquete) => {
    return selectedPackage?.planRutaActual?.vuelos?.map((vuelo: Vuelo) => {
        return {
            type: "vuelo",
            id: `${vuelo.id}-vuelo`,
            origen: vuelo.planVuelo.ciudadOrigen.ciudad,
            destino: vuelo.planVuelo.ciudadDestino.ciudad,
            fechaSalida: vuelo.fechaSalida,
            fechaLlegada: vuelo.fechaLlegada,
        };
    });
};

const SidebarShipment = ({ shipment, currentTime }: { shipment: Envio | undefined; currentTime: Date | undefined }) => {
    const [visible, setVisible] = useState<boolean>(true);
    const [selectedIdPackage, setSelectedIdPackage] = useState<number>();
    const selectedPackage = shipment?.paquetes?.find((pkg: Paquete) => pkg.id === selectedIdPackage);

    const intercalateSteps = (aeropuertos: any, vuelos: any, selectedPackage: Paquete) => {
        const steps = [];
        for (let i = 0; i < vuelos?.length; i++) {
            steps.push(aeropuertos[i]);
            steps.push(vuelos[i]);
        }
        if (selectedPackage?.planRutaActual?.vuelos?.length === undefined || selectedPackage?.planRutaActual?.vuelos?.length === 0)
            return steps;
        if (selectedPackage?.planRutaActual?.vuelos?.length > 0) {
            const ultimoVuelo = selectedPackage?.planRutaActual?.vuelos[selectedPackage?.planRutaActual?.vuelos?.length - 1];
            steps.push({
                type: "aeropuerto",
                id: `${ultimoVuelo.id}-aeropuerto-destino`,
                ciudad: ultimoVuelo.planVuelo.ciudadDestino.ciudad,
                fechaSalida: ultimoVuelo.fechaLlegada,
                tiempoEnAlmacen: "Destino final",
            });
        }
        return steps;
    };

    const stepsData = useMemo(() => {
        if (selectedPackage !== undefined && shipment !== undefined) {
            const aeropuertos = generateAeropuertosData(selectedPackage, shipment);
            const vuelos = generateVuelosData(selectedPackage);
            return intercalateSteps(aeropuertos, vuelos, selectedPackage);
        }
        return [];
    }, [selectedPackage, shipment]);

    const { activeStep, setActiveStep } = useSteps({
        index: 0,
        count: stepsData.length,
    });

    useEffect(() => {
        if (stepsData.length > 0 && currentTime) {
            const activeIndex = calculateActiveStep(stepsData, currentTime);
            setActiveStep(activeIndex);
        }
    }, [stepsData, currentTime, setActiveStep]);

    const calculateActiveStep = (steps: any[], currentTime: Date | undefined) => {
        if (!currentTime) return 0;
        for (let i = 0; i < steps.length; i++) {
            if (steps[i].type === "aeropuerto") {
                const stepDate = new Date(steps[i].fechaSalida);
                if (currentTime < stepDate) {
                    return i;
                }
            } else if (steps[i].type === "vuelo") {
                const fechaSalida = new Date(steps[i].fechaSalida);
                const fechaLlegada = new Date(steps[i].fechaLlegada);
                if (currentTime > fechaSalida && currentTime < fechaLlegada) {
                    return i;
                }
            }
        }
        return steps.length;
    };

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
                    "flex w-[30rem] flex-col items-center overflow-hidden transition-all duration-300 ease-in-out absolute top-[105px] bottom-[43px] z-[20] border border-gray-300",
                    visible ? "left-[43px]" : "-left-[40rem]"
                )}
            >
                <CardHeader className='relative flex flex-row w-full items-center justify-between'>
                    {shipment === undefined ? (
                        <Skeleton className='w-1/2 h-8' />
                    ) : (
                        <>
                            <H2 className='flex-1'>{`ID: ${shipment.id}`}</H2>
                            <ChevronsLeft
                                className='cursor-pointer w-10 h-10 p-2 stroke-muted-foreground hover:stroke-black transition-all duration-200 ease-in-out'
                                onClick={() => setVisible(false)}
                            >
                                Cerrar
                            </ChevronsLeft>
                        </>
                    )}
                </CardHeader>
                <CardContent className='w-full flex space-y-4 flex-col *:w-full overflow-hidden'>
                    {shipment === undefined ? (
                        <div className='w-full h-full flex-col space-y-4'>
                            <Skeleton className='w-1/2 h-8' />
                            <Skeleton className='w-full h-2' />
                            <Skeleton className='w-1/2 h-2' />
                            <Skeleton className='w-full h-2' />
                        </div>
                    ) : (
                        <div className='flex flex-col w-full items-start'>
                            <H3 className='mb-2'>Envio</H3>
                            <div className='flex flex-row'>
                                <Muted>{`Origen: ${shipment.ubicacionOrigen.ciudadAbreviada.toUpperCase()}`}</Muted>
                                <Muted className='ml-4'>{`Destino: ${shipment.ubicacionDestino.ciudadAbreviada.toUpperCase()}`}</Muted>
                            </div>
                            <Muted>{`Fecha de registro: ${formatDateTimeLongShort(shipment.fechaRecepcion)}`}</Muted>
                            <Muted>{`Fecha de limite: ${formatDateTimeLongShort(shipment.fechaLimiteEntrega)}`}</Muted>
                        </div>
                    )}
                    <Separator />
                    {shipment?.paquetes === undefined || shipment?.paquetes === null || shipment?.paquetes?.length === 0 ? (
                        <div className='w-full h-full flex-col space-y-4'>
                            <Skeleton className='w-1/2 h-8' />
                            <Skeleton className='w-full h-10' />
                            <Skeleton className='w-1/2 h-10' />
                            <Skeleton className='w-full h-36' />
                        </div>
                    ) : (
                        <>
                            <div className='flex flex-col space-y-4 w-full items-start'>
                                <H3>{`Paquetes (${shipment.paquetes.length})`}</H3>
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
                                            {shipment?.paquetes
                                                ?.sort((a, b) => {
                                                    const estadoComparison = a.estado.localeCompare(b.estado);
                                                    if (estadoComparison !== 0) return estadoComparison;

                                                    const getMaxFechaLlegada = (pkg: any) => {
                                                        return pkg.planRutaActual?.vuelos?.reduce((maxFecha: Date, vuelo: Vuelo) => {
                                                            const fechaLlegada = new Date(vuelo.fechaLlegada);
                                                            const truncFechaLlegada = new Date(
                                                                fechaLlegada.getFullYear(),
                                                                fechaLlegada.getMonth(),
                                                                fechaLlegada.getDate(),
                                                                fechaLlegada.getHours()
                                                            );
                                                            return truncFechaLlegada > maxFecha ? truncFechaLlegada : maxFecha;
                                                        }, new Date(0));
                                                    };

                                                    const aMaxFechaLlegada = getMaxFechaLlegada(a);
                                                    const bMaxFechaLlegada = getMaxFechaLlegada(b);

                                                    return bMaxFechaLlegada - aMaxFechaLlegada;
                                                })
                                                .map((pkg: Paquete) => (
                                                    <SelectItem
                                                        key={pkg.id}
                                                        value={pkg.id.toString()}
                                                    >
                                                        <div className='inline-flex'>
                                                            {pkg.id}
                                                            {pkg.planRutaActual?.vuelos && currentTime && (
                                                                <StatusChip
                                                                    status={getPackageState(pkg.planRutaActual?.vuelos, currentTime)}
                                                                />
                                                            )}
                                                        </div>
                                                    </SelectItem>
                                                ))}
                                        </SelectGroup>
                                    </SelectContent>
                                </Select>
                            </div>
                            <div className='flex flex-col'>
                                <H3>Ruta del paquete</H3>
                            </div>
                            <ScrollArea className='flex flex-col'>
                                {selectedPackage === undefined ||
                                selectedPackage?.planRutaActual === undefined ||
                                selectedPackage?.planRutaActual?.vuelos === undefined ||
                                selectedPackage?.planRutaActual?.vuelos.length === 0 ? (
                                    <Muted className='inline-flex'>Ruta en planificacion...</Muted>
                                ) : (
                                    <div className='flex flex-row gap-4'>
                                        <div className='w-full'>
                                            {selectedPackage?.planRutaActual?.vuelos !== undefined && currentTime !== undefined && (
                                                <>
                                                    {(() => {
                                                        const planRutaActual = selectedPackage?.planRutaActual;

                                                        if (
                                                            !planRutaActual ||
                                                            !planRutaActual.vuelos ||
                                                            planRutaActual.vuelos.length === 0
                                                        ) {
                                                            return null;
                                                        }

                                                        const ultimoVuelo = planRutaActual.vuelos[planRutaActual.vuelos.length - 1];
                                                        const fechaLlegadaUltimoVuelo = new Date(ultimoVuelo.fechaLlegada);
                                                        const tiempoRestante = calculateRemainingTime(fechaLlegadaUltimoVuelo, currentTime);

                                                        return <Small className='mb-4'>{`Tiempo restante: ${tiempoRestante}`}</Small>;
                                                    })()}
                                                </>
                                            )}
                                            <Stepper
                                                size={'sm'}
                                                index={activeStep}
                                                orientation='vertical'
                                                height='max-content'
                                                gap={0}
                                            >
                                                {stepsData.map((step) => (
                                                    <Step
                                                        key={step.id}
                                                        className='w-full pr-4'
                                                    >
                                                        <StepIndicator>
                                                            <StepStatus
                                                                complete={<StepIcon />}
                                                                incomplete={<StepNumber />}
                                                                active={<StepNumber />}
                                                            />
                                                        </StepIndicator>
                                                        <Box className='w-full'>
                                                            {step.type === "aeropuerto" ? (
                                                                <>
                                                                    <StepTitle>{step.ciudad}</StepTitle>
                                                                    <StepDescription>
                                                                        Tiempo en almacén: {step.tiempoEnAlmacen}
                                                                    </StepDescription>
                                                                </>
                                                            ) : (
                                                                <>
                                                                    <div className='flex flex-row justify-between'>
                                                                        <div className='flex flex-col text-left'>
                                                                            <StepTitle>{`${step.origen}`}</StepTitle>
                                                                            <StepDescription>{`${formatDateTimeLongShort(step.fechaSalida)}`}</StepDescription>
                                                                        </div>
                                                                        <div className='flex flex-col text-right'>
                                                                            <StepTitle>{`${step.destino}`}</StepTitle>
                                                                            <StepDescription>{`${formatDateTimeLongShort(step.fechaLlegada)}`}</StepDescription>
                                                                        </div>
                                                                    </div>
                                                                </>
                                                            )}
                                                            <Box className='w-full h-4 bg-transparent' />
                                                        </Box>
                                                        <StepSeparator />
                                                    </Step>
                                                ))}
                                            </Stepper>
                                        </div>
                                    </div>
                                )}
                            </ScrollArea>
                        </>
                    )}
                </CardContent>
            </Card>
        </>
    );
};

export default SidebarShipment;
