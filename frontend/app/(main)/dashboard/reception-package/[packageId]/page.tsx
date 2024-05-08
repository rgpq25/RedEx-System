"use client";

import { cn } from "@/lib/utils";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";

import { Button, buttonVariants } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger } from "@/components/ui/select";
import { Large, Muted } from "@/components/ui/typography";
import { Skeleton } from "@/components/ui/skeleton";

import { Envio, Paquete, Aeropuerto, Ubicacion, Vuelo } from "@/lib/types";
import { api } from "@/lib/api";

export default function ReceptionPackage() {
    const params = useParams<{ packageId: string }>();
    const [envio, setEnvio] = useState<Envio | undefined>(undefined);

    useEffect(() => {
        const getEnvio = async () => {
            await api(
                "GET",
                `${process.env.NEXT_PUBLIC_API}/back/envio/${params.packageId}`,
                (data: Envio) => {
                    setEnvio(data);
                },
                (error) => {
                    console.log(error);
                }
            );
        };

        getEnvio();
    }, [params.packageId]);

    return (
        <div className='flex flex-col mx-auto gap-8 w-11/12 h-full p-8'>
            {envio === undefined ? (
                <section className="w-10/12 h-full grid grid-cols-1 lg:grid-cols-2 gap-4 m-auto">
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
                                    <Input disabled type='name' />
                                </div>
                                <div>
                                    <Label>Apellidos</Label>
                                    <Input disabled type='name' />
                                </div>
                                <div>
                                    <Label>Correo</Label>
                                    <Input disabled type='name' />
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
                                    <Input disabled type='name' />
                                </div>
                                <div>
                                    <Label>Apellidos</Label>
                                    <Input disabled type='name' />
                                </div>
                                <div>
                                    <Label>Correo</Label>
                                    <Input disabled type='name' />
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
                                    <Input disabled type='name' />
                                </div>
                                <div>
                                    <Label>Cantidad de paquetes</Label>
                                    <Input disabled type='number' />
                                </div>
                                <div>
                                    <Label>Estado del envío</Label>
                                    <Input disabled type='name' />
                                </div>
                                <div>
                                    <Label>Fecha de recepción</Label>
                                    <Input disabled type='date' />
                                </div>
                                <div>
                                    <Label>Ciudad de origen</Label>
                                    <Input disabled type='name' />
                                </div>
                                <div>
                                    <Label>Ciudad de destino</Label>
                                    <Input disabled type='name' />
                                </div>
                            </CardContent>
                        </Card>
                        <Card>
                            <CardHeader>
                                <CardTitle>Ruta de paquetes</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <Select onValueChange={() => {}}>
                                    <SelectTrigger>Seleccionar paquete</SelectTrigger>
                                    <SelectContent>
                                        <SelectGroup>
                                            {envio?.paquetes?.map((paquete) => (
                                                <SelectItem key={paquete.id} value={paquete.id.toString()}>
                                                    {paquete.id}
                                                </SelectItem>
                                            ))}
                                        </SelectGroup>
                                    </SelectContent>
                                </Select>
                                <ScrollArea>
                                    {envio.paquetes?.map((paquete: Paquete) =>
                                        paquete.plan_ruta.vuelos.map((vuelo: Vuelo) => (
                                            <Card key={vuelo.id}>
                                                <CardContent className='flex flex-wrap gap-4 justify-between *:flex *:flex-col'>
                                                    <div>
                                                        <Large>Origen</Large>
                                                        <Muted>{vuelo.plan_vuelo.ubicacion_origen}</Muted>
                                                    </div>
                                                    <div>
                                                        <Large>Destino</Large>
                                                        <Muted>{vuelo.plan_vuelo.ubicacion_destino}</Muted>
                                                    </div>
                                                </CardContent>
                                            </Card>
                                        ))
                                    )}
                                </ScrollArea>
                            </CardContent>
                        </Card>
                    </section>
                    <section className='flex flex-row justify-center gap-16'>
                        <Button variant='default'>Confirmar</Button>
                        <Link className={cn(buttonVariants({ variant: "outline" }))} href={"/dashboard"}>
                            Cancelar
                        </Link>
                    </section>
                </>
            )}
        </div>
    );
}
