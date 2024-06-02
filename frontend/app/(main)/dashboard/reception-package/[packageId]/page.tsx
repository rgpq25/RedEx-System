"use client";

import { cn } from "@/lib/utils";

import { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";

import { Button, buttonVariants } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Large, Muted } from "@/components/ui/typography";
import { Skeleton } from "@/components/ui/skeleton";
import { toast } from "sonner";

import { Envio, Paquete, Aeropuerto, Ubicacion, Vuelo, PlanRuta } from "@/lib/types";
import { api } from "@/lib/api";
import { formatDateLong } from "@/lib/date";
import { format, parseISO } from "date-fns";

export default function ReceptionPackage() {
    const params = useParams<{ packageId: string }>();
    const packageId = params.packageId;
    const [envio, setEnvio] = useState<Envio>();
    const [paquetes, setPaquetes] = useState<Paquete[]>();
    const [selectedIdPaquete, setSelectedIdPaquete] = useState<number>();

    const getEnvio = useCallback(async () => {
        await api(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/envio/${packageId}`,
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
    }, [packageId]);
    const getPaquetesEnvio = useCallback(async () => {
        await api(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/paquete/envio_sin_simulacion/${packageId}`,
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
    }, [packageId]);

    const handleReceptionData = useCallback(async () => {
        try {
            await getEnvio();
            await getPaquetesEnvio();
        } catch (error) {
            console.log(error);
        }
    }, [getEnvio, getPaquetesEnvio]);

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
            for (const paquete of paquetes) {
                getVuelosPaquetes(paquete);
            }
        }
    }, [paquetes]);

    useEffect(() => {
        handleReceptionData();
    }, [handleReceptionData, envio, paquetes]);

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
                        <Card>
                            <CardHeader>
                                <CardTitle>Información personal (emisor)</CardTitle>
                            </CardHeader>
                            <CardContent className='space-y-2 *:space-y-1'>
                                <div>
                                    <Label>Nombres</Label>
                                    <Input
                                        disabled
                                        type='name'
                                    />
                                </div>
                                <div>
                                    <Label>Apellidos</Label>
                                    <Input
                                        disabled
                                        type='name'
                                    />
                                </div>
                                <div>
                                    <Label>Correo</Label>
                                    <Input
                                        disabled
                                        type='name'
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
                                    />
                                </div>
                                <div>
                                    <Label>Apellidos</Label>
                                    <Input
                                        disabled
                                        type='name'
                                    />
                                </div>
                                <div>
                                    <Label>Correo</Label>
                                    <Input
                                        disabled
                                        type='name'
                                    />
                                </div>
                            </CardContent>
                        </Card>
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
                                        type='name'
                                        defaultValue={format(parseISO(envio.fechaRecepcion.toString()).toString(), "dd/MM/yyyy")}
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
                            <CardContent>
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
                                <ScrollArea>
                                    {paquetes
                                        ?.find((paquete) => paquete.id === selectedIdPaquete)
                                        ?.planRutaActual?.vuelos?.map((vuelo: Vuelo) => (
                                            <Card key={vuelo.id}>
                                                <CardContent className='flex flex-wrap gap-4 justify-between *:flex *:flex-col'>
                                                    <div>
                                                        <Large>Origen</Large>
                                                        <Muted>{vuelo.planVuelo.ciudadOrigen.ciudad}</Muted>
                                                    </div>
                                                    <div>
                                                        <Large>Destino</Large>
                                                        <Muted>{vuelo.planVuelo.ciudadDestino.ciudad}</Muted>
                                                    </div>
                                                </CardContent>
                                            </Card>
                                        ))}
                                </ScrollArea>
                            </CardContent>
                        </Card>
                    </section>
                    <section className='flex flex-row justify-center gap-16'>
                        <Button variant='default'>Confirmar</Button>
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
