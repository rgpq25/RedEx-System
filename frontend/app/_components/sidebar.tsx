"use client";

import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Skeleton } from "@/components/ui/skeleton";
import { useState } from "react";

import { cn } from "@/lib/utils";

import { Operacion, Aeropuerto, Envio, Vuelo } from "@/lib/types";
import { H3, Large, Muted } from "@/components/ui/typography";

interface SidebarProps {
    envios?: Envio[];
    aeropuertos?: Aeropuerto[];
    vuelos?: Vuelo[];
    className?: string;
    props?: any;
}

export default function Sidebar({ envios, aeropuertos, vuelos, className, ...props }: SidebarProps) {
    const [selectedOperation, setSelectedOperation] = useState<Operacion>(Operacion.Envios);

    return (
        <Card className={cn("flex w-96 flex-col max-h-full items-center", className)}>
            <CardHeader>
                <Tabs value={selectedOperation} onValueChange={(e) => setSelectedOperation(e as Operacion)} className='w-full '>
                    <TabsList>
                        <TabsTrigger value='Envios'>Envios</TabsTrigger>
                        <TabsTrigger value='Aeropuertos'>Aeropuertos</TabsTrigger>
                        <TabsTrigger value='Vuelos'>Vuelos</TabsTrigger>
                    </TabsList>
                </Tabs>
            </CardHeader>
            <CardContent className='w-full min-h-full flex flex-col items-center gap-6 *:w-full'>
                <Separator />
                {selectedOperation === Operacion.Envios && <Envios envios={envios} />}
                {selectedOperation === Operacion.Aeropuertos && <Aeropuertos aeropuertos={aeropuertos} />}
                {selectedOperation === Operacion.Vuelos && <Vuelos vuelos={vuelos} />}
            </CardContent>
        </Card>
    );
}

function Envios({ envios }: { envios: Envio[] | undefined }) {
    const [search, setSearch] = useState<string>("");

    return (
        <>
            {envios === undefined ? (
                SidebarSkeleton()
            ) : envios.length === 0 ? (
                <p>No hay envíos disponibles</p>
            ) : (
                <>
                    <Input placeholder='Buscar envío...' value={search} onChange={(e) => setSearch(e.target.value)} />
                    <ScrollArea className='h-screen'>
                        <section className='flex flex-col gap-4'>
                            {envios.filter((envio) => envio.id.toString().includes(search)).length === 0 ? (
                                <p>No se encontraron resultados</p>
                            ) : (
                                envios
                                    .filter((envio) => envio.id.toString().includes(search))
                                    .map((envio) => (
                                        <Card key={envio.id} className='p-3 *:p-0 hover:bg-gray-100 cursor-pointer'>
                                            <CardHeader>
                                                <Large>{envio.id}</Large>
                                            </CardHeader>
                                            <CardContent>
                                                <Muted>Fecha de recepción: {envio.fecha_recepcion}</Muted>
                                                <Muted>Cantidad: {envio.cantidad_paquetes}</Muted>
                                                <span className='flex flex-wrap *:flex-grow'>
                                                    <Muted>Origen: {envio.ubicacion_origen.ciudad_abreviada}</Muted>
                                                    <Muted>Destino: {envio.ubicacion_destino.ciudad_abreviada}</Muted>
                                                </span>
                                            </CardContent>
                                        </Card>
                                    ))
                            )}
                        </section>
                    </ScrollArea>
                </>
            )}
        </>
    );
}

function Aeropuertos({ aeropuertos }: { aeropuertos: Aeropuerto[] | undefined }) {
    const [search, setSearch] = useState<string>("");

    return (
        <>
            {aeropuertos === undefined ? (
                SidebarSkeleton()
            ) : aeropuertos.length === 0 ? (
                <p>No hay aeropuertos disponibles</p>
            ) : (
                <>
                    <Input
                        placeholder='Buscar aeropuerto...'
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                    <ScrollArea className='h-screen'>
                        <section className='flex flex-col gap-4'>
                            {aeropuertos.filter((aeropuerto) =>
                                aeropuerto.ubicacion.ciudad_abreviada.toLowerCase().includes(search.toLowerCase())
                            ).length === 0 ? (
                                <p>No se encontraron resultados</p>
                            ) : (
                                aeropuertos
                                    .filter((aeropuerto) =>
                                        aeropuerto.ubicacion.ciudad_abreviada.toLowerCase().includes(search.toLowerCase())
                                    )
                                    .map((aeropuerto) => (
                                        <Card key={aeropuerto.id} className='p-3 *:p-0 hover:bg-gray-100 cursor-pointer'>
                                            <CardHeader>
                                                <Large>
                                                    {aeropuerto.ubicacion.ciudad_abreviada} ({aeropuerto.ubicacion.ciudad})
                                                </Large>
                                            </CardHeader>
                                            <CardContent>
                                                <Muted>Capacidad: {aeropuerto.capacidad_maxima}</Muted>
                                            </CardContent>
                                        </Card>
                                    ))
                            )}
                        </section>
                    </ScrollArea>
                </>
            )}
        </>
    );
}

function Vuelos({ vuelos }: { vuelos: Vuelo[] | undefined }) {
    const [search, setSearch] = useState<string>("");

    return (
        <>
            {vuelos === undefined ? (
                SidebarSkeleton()
            ) : vuelos.length === 0 ? (
                <p>No hay vuelos disponibles</p>
            ) : (
                <>
                    <Input placeholder='Buscar vuelo...' value={search} onChange={(e) => setSearch(e.target.value)} />
                    <ScrollArea className='h-screen'>
                        <section className='flex flex-col gap-4'>
                            {vuelos.filter((vuelo) => vuelo.id.toString().includes(search)).length === 0 ? (
                                <p>No se encontraron resultados</p>
                            ) : (
                                vuelos
                                    .filter((vuelo) => vuelo.id.toString().includes(search))
                                    .map((vuelo) => (
                                        <Card key={vuelo.id} className='p-3 *:p-0 hover:bg-gray-100 cursor-pointer'>
                                            <CardHeader>
                                                <Large>{vuelo.id}</Large>
                                            </CardHeader>
                                            <CardContent>
                                                <span className='flex flex-wrap *:flex-grow'>
                                                    <Muted>Origen: {vuelo.plan_vuelo.ubicacion_origen.ciudad_abreviada}</Muted>
                                                    <Muted>Destino: {vuelo.plan_vuelo.ubicacion_destino.ciudad_abreviada}</Muted>
                                                </span>
                                                <Muted>Fecha origen: {vuelo.fecha_origen}</Muted>
                                                <Muted>Fecha destino: {vuelo.fecha_destino}</Muted>
                                                <Muted>Capacidad: {vuelo.capacidad_utilizada}</Muted>
                                            </CardContent>
                                        </Card>
                                    ))
                            )}
                        </section>
                    </ScrollArea>
                </>
            )}
        </>
    );
}

function SidebarSkeleton() {
    return (
        <div className='flex flex-col space-y-3 w-full'>
            <Skeleton className='h-12 w-full rounded-xl' />
            <div className='space-y-2'>
                <Skeleton className='h-4 w-1/3' />
                <Skeleton className='h-4 w-1/4' />
            </div>
        </div>
    );
}
