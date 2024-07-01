"use client";

import { cn } from "@/lib/utils";

import { useEffect, useState, useCallback, useContext } from "react";
import Link from "next/link";

import { Button, buttonVariants } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Large, Muted, Small } from "@/components/ui/typography";
import { Skeleton } from "@/components/ui/skeleton";
import { toast } from "sonner";

import { Envio, Paquete, Aeropuerto, Ubicacion, Vuelo, PlanRuta, Cliente, Usuario } from "@/lib/types";
import { api } from "@/lib/api";
import { formatDateTimeLongShort } from "@/lib/date";
import { format, parseISO } from "date-fns";

import { ReceptionPackageIdContext } from "@/components/hooks/useReceptionPackageId";

export default function ReceptionPackage() {
    const { receptionPackageId } = useContext(ReceptionPackageIdContext);
    const [envio, setEnvio] = useState<Envio>();
    const [paquetes, setPaquetes] = useState<Paquete[]>();
    const [selectedIdPaquete, setSelectedIdPaquete] = useState<number>();
    const [isEnvioToReceive, setIsEnvioToReceive] = useState<boolean>(false);

    const selectedPaquete = paquetes?.find((paquete) => paquete.id === selectedIdPaquete);

    const getEnvio = useCallback(async () => {
        await api(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/envio/${receptionPackageId}`,
            (data: Envio) => {
                setEnvio(data);
            },
            (error) => {
                toast.error("No se encontró el envío", {
                    position: "bottom-right",
                    duration: 3000,
                });
            }
        );
    }, [receptionPackageId]);
    const getPaquetesEnvio = useCallback(async () => {
        await api(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/paquete/envio_sin_simulacion/${receptionPackageId}`,
            (data: Paquete[]) => {
                setPaquetes(data);
            },
            (error) => {
                toast.error("No se encontraron paquetes del respectivo envío", {
                    position: "bottom-right",
                    duration: 3000,
                });
            }
        );
    }, [receptionPackageId]);

    const handleReceptionData = useCallback(async () => {
        try {
            await getEnvio();
            await getPaquetesEnvio();
        } catch (error) {
            console.log(error);
        }
    }, [getEnvio, getPaquetesEnvio]);

    const handleConfirmReception = useCallback(async () => {
        if (selectedPaquete) {
            await api(
                "POST",
                `${process.env.NEXT_PUBLIC_API}/back/envio/actualizarEstadoEntregado`,
                (data) => {
                    toast.success("Paquete recibido con éxito", {
                        position: "bottom-right",
                        duration: 3000,
                    });
                    handleReceptionData();
                },
                (error) => {
                    toast.error("No se pudo confirmar la recepción del paquete", {
                        position: "bottom-right",
                        duration: 3000,
                    });
                },
                { id: envio?.id }
            );
        }
    }, [envio, selectedPaquete, handleReceptionData]);

    const defineIsEnvioReceived = useCallback(() => {
        if (selectedPaquete) {
            let currentTimestamp = new Date();
            // Entre todos los paquetes del envío, se define cual es el tiempo del ultimo vuelo
            let lastVuelo: Vuelo | undefined;
            if (paquetes === undefined) return;
            for (const paquete of paquetes) {
                if (paquete?.planRutaActual && paquete?.planRutaActual?.vuelos) {
                    for (const vuelo of paquete?.planRutaActual?.vuelos) {
                        if (lastVuelo === undefined || vuelo.fechaLlegada > lastVuelo.fechaLlegada) {
                            lastVuelo = vuelo;
                        }
                    }
                }
            }
            if (lastVuelo && lastVuelo.fechaLlegada < currentTimestamp && envio?.estado !== "Entregado") {
                setIsEnvioToReceive(true);
            } else {
                setIsEnvioToReceive(false);
            }
        }
    }, [selectedPaquete, paquetes, envio]);

    useEffect(() => {
        const getVuelosPaquetes = async (paquete: Paquete) => {
            if (paquete.planRutaActual === null) return;
            const idPlanRutaPaquete = paquete.planRutaActual?.id;
            await api(
                "GET",
                `${process.env.NEXT_PUBLIC_API}/back/plan_ruta_vuelo/plan_ruta/${idPlanRutaPaquete}`,
                (data) => {
                    let Vuelos: Vuelo[] = [];
                    for (const item of data) {
                        Vuelos.push(item.vuelo);
                    }
                    if (paquete.planRutaActual !== null) {
                        paquete.planRutaActual.vuelos = Vuelos;
                    }
                },
                (error) => {
                    toast.error("No se encontraron vuelos del respectivo plan de ruta", {
                        position: "bottom-right",
                        duration: 3000,
                    });
                }
            );
        };

        if (paquetes) {
            paquetes.forEach((paquete) => {
                getVuelosPaquetes(paquete);
            });
        }
    }, [paquetes]);

    useEffect(() => {
        handleReceptionData();
    }, [handleReceptionData]);

    useEffect(() => {
        defineIsEnvioReceived();
    }, [defineIsEnvioReceived]);

    console.log(paquetes);

    return (
        <div className='flex flex-col mx-auto gap-8 w-11/12 h-full p-8'>
            {envio === undefined ? (
                <section className='w-10/12 h-full grid grid-cols-1 lg:grid-cols-2 gap-4 m-auto'>
                    <Skeleton />
                    <Skeleton />
                    <Skeleton />
                    <Skeleton />
                </section>
            ) : (
                <>
                    <section className='grid grid-cols-1 lg:grid-cols-2 flex-wrap gap-4 m-auto *:bg-muted *:max-h-fit'>
                        {envio?.emisor && envio?.receptor ? (
                            <>
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Información personal (emisor)</CardTitle>
                                    </CardHeader>
                                    <CardContent className='space-y-2 *:space-y-1'>
                                        <div>
                                            <Label>Nombre</Label>
                                            <Input
                                                disabled
                                                type='name'
                                                defaultValue={envio.emisor.usuario.nombre}
                                            />
                                        </div>
                                        <div>
                                            <Label>Correo</Label>
                                            <Input
                                                disabled
                                                type='name'
                                                defaultValue={envio.emisor.usuario.correo}
                                            />
                                        </div>
                                    </CardContent>
                                </Card>
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Información personal (receptor)</CardTitle>
                                    </CardHeader>
                                    <CardContent className='space-y-2 *:space-y-1'>
                                        <div>
                                            <Label>Nombres</Label>
                                            <Input
                                                disabled
                                                type='name'
                                                defaultValue={envio.receptor.usuario.nombre}
                                            />
                                        </div>
                                        <div>
                                            <Label>Correo</Label>
                                            <Input
                                                disabled
                                                type='name'
                                                defaultValue={envio.receptor.usuario.correo}
                                            />
                                        </div>
                                    </CardContent>
                                </Card>
                            </>
                        ) : (
                            <></>
                        )}
                        <Card>
                            <CardHeader>
                                <CardTitle>Información del envío</CardTitle>
                            </CardHeader>
                            <CardContent className='*:*:space-y-1 grid grid-cols-1 lg:grid-cols-2 gap-2 items-center'>
                                <div>
                                    <Label>Código de envío</Label>
                                    <Input
                                        disabled
                                        type='name'
                                        defaultValue={envio.id}
                                    />
                                </div>
                                <div>
                                    <Label>Cantidad de paquetes</Label>
                                    <Input
                                        disabled
                                        type='number'
                                        defaultValue={envio.cantidadPaquetes}
                                    />
                                </div>
                                <div>
                                    <Label>Estado del envío</Label>
                                    <Input
                                        disabled
                                        type='name'
                                        defaultValue={envio.estado}
                                    />
                                </div>
                                <div>
                                    <Label>Fecha de recepción</Label>
                                    <Input
                                        disabled
                                        type='text'
                                        defaultValue={format(parseISO(envio.fechaRecepcion.toString()), "dd/MM/yyyy HH:mm:ss")}
                                    />
                                </div>
                                <div>
                                    <Label>Ciudad de origen</Label>
                                    <Input
                                        disabled
                                        type='name'
                                        defaultValue={envio.ubicacionOrigen.ciudad}
                                    />
                                </div>
                                <div>
                                    <Label>Ciudad de destino</Label>
                                    <Input
                                        disabled
                                        type='name'
                                        defaultValue={envio.ubicacionDestino.ciudad}
                                    />
                                </div>
                            </CardContent>
                        </Card>
                        <Card>
                            <CardHeader>
                                <CardTitle>Ruta de paquetes</CardTitle>
                            </CardHeader>
                            <CardContent className='w-full'>
                                <Select
                                    key={selectedIdPaquete}
                                    onValueChange={(value) => setSelectedIdPaquete(parseInt(value))}
                                    defaultValue={selectedIdPaquete?.toString()}
                                    value={selectedIdPaquete?.toString()}
                                >
                                    <SelectTrigger>
                                        <SelectValue placeholder={selectedIdPaquete || "Seleccione un paquete"} />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectGroup>
                                            {paquetes?.map((paquete) => (
                                                <SelectItem
                                                    key={paquete.id}
                                                    value={paquete.id.toString()}
                                                >
                                                    {paquete.id}
                                                </SelectItem>
                                            ))}
                                        </SelectGroup>
                                    </SelectContent>
                                </Select>

                                <div className='mt-4 flex flex-row justify-between'>
                                    <Large className='text-left'>Origen</Large>
                                    <Large className='text-right'>Destino</Large>
                                </div>
                                <ScrollArea className='h-28'>
                                    {selectedPaquete?.planRutaActual?.vuelos?.map((vuelo: Vuelo) => (
                                        <section
                                            key={vuelo.id}
                                            className='flex flex-wrap gap-4 my-4 justify-between *:flex *:flex-col px-2'
                                        >
                                            <div>
                                                <Small className='text-left'>{vuelo.planVuelo.ciudadOrigen.ciudad}</Small>
                                                <Muted className='text-left'>{formatDateTimeLongShort(vuelo.fechaSalida)}</Muted>
                                            </div>
                                            <div>
                                                <Small className='text-right'>{vuelo.planVuelo.ciudadDestino.ciudad}</Small>
                                                <Muted className='text-right'>{formatDateTimeLongShort(vuelo.fechaLlegada)}</Muted>
                                            </div>
                                        </section>
                                    ))}
                                </ScrollArea>
                            </CardContent>
                        </Card>
                    </section>
                    <section className='flex flex-row justify-center gap-16'>
                        <Button
                            variant='default'
                            disabled={!isEnvioToReceive}
                            onClick={handleConfirmReception}
                        >
                            Confirmar recepcion
                        </Button>
                        <Link
                            className={cn(buttonVariants({ variant: "outline" }))}
                            href={"/dashboard"}
                        >
                            Cancelar
                        </Link>
                    </section>
                </>
            )}
        </div>
    );
}
