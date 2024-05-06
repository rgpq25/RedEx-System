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
import { Large, Muted, Small } from "@/components/ui/typography";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { format } from "date-fns";
import { CalendarIcon, Check, Eraser, ListFilter } from "lucide-react";
import { Calendar } from "@/components/ui/calendar";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

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
        <Card className={cn("flex w-96 flex-col items-center overflow-hidden", className)}>
            <CardHeader>
                <Tabs value={selectedOperation} onValueChange={(e) => setSelectedOperation(e as Operacion)} className='w-full '>
                    <TabsList>
                        <TabsTrigger value='Envios'>Envios</TabsTrigger>
                        <TabsTrigger value='Aeropuertos'>Aeropuertos</TabsTrigger>
                        <TabsTrigger value='Vuelos'>Vuelos</TabsTrigger>
                    </TabsList>
                </Tabs>
            </CardHeader>
            <CardContent className='w-full flex flex-col items-center gap-6 *:w-full overflow-hidden'>
                <Separator />
                {selectedOperation === Operacion.Envios && <Envios envios={envios} />}
                {selectedOperation === Operacion.Aeropuertos && <Aeropuertos aeropuertos={aeropuertos} />}
                {selectedOperation === Operacion.Vuelos && <Vuelos vuelos={vuelos} />}
            </CardContent>
        </Card>
    );
}

function Envios({ envios }: { envios: Envio[] | undefined }) {
    const [filteredEnvios, setFilteredEnvios] = useState<Envio[] | undefined>(envios);
    const [receptionDateStart, setReceptionDateStart] = useState<Date | undefined>(undefined);
    const [receptionDateEnd, setReceptionDateEnd] = useState<Date | undefined>(undefined);
    const [search, setSearch] = useState<string>("");

    return (
        <>
            {envios === undefined ? (
                SidebarSkeleton()
            ) : envios.length === 0 ? (
                <p>No hay envíos disponibles</p>
            ) : (
                <>
                    <section className='flex flex-row justify-between gap-4 w-full'>
                        <Input placeholder='Buscar envío...' value={search} onChange={(e) => setSearch(e.target.value)} />
                        <Popover>
                            <PopoverTrigger asChild>
                                <Button variant='secondary'>
                                    <ListFilter className='mr-2 h-4 w-4' /> Filtros
                                </Button>
                            </PopoverTrigger>
                            <PopoverContent align='start' className='w-fit flex flex-col gap-4'>
                                <Small>Fecha de registro:</Small>
                                <section className='flex flex-col gap-2'>
                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant={"outline"}
                                                className={cn(
                                                    "w-[280px] justify-start text-left font-normal",
                                                    !receptionDateStart && "text-muted-foreground"
                                                )}
                                            >
                                                <CalendarIcon className='mr-2 h-4 w-4' />
                                                {receptionDateStart ? format(receptionDateStart, "PPP") : <span>Fecha de inicio</span>}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className='w-auto p-0'>
                                            <Calendar
                                                mode='single'
                                                selected={receptionDateStart}
                                                onSelect={setReceptionDateStart}
                                                initialFocus
                                            />
                                        </PopoverContent>
                                    </Popover>
                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant={"outline"}
                                                className={cn(
                                                    "w-[280px] justify-start text-left font-normal",
                                                    !receptionDateEnd && "text-muted-foreground"
                                                )}
                                            >
                                                <CalendarIcon className='mr-2 h-4 w-4' />
                                                {receptionDateEnd ? format(receptionDateEnd, "PPP") : <span>Fecha de fin</span>}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className='w-auto p-0'>
                                            <Calendar
                                                mode='single'
                                                selected={receptionDateEnd}
                                                onSelect={setReceptionDateEnd}
                                                initialFocus
                                            />
                                        </PopoverContent>
                                    </Popover>
                                </section>
                                <Separator />
                                <Small>Rango de capacidad:</Small>
                                <Input type='number' placeholder='Cantidad minima' defaultValue={0} />
                                <Input type='number' placeholder='Cantidad maxima' defaultValue={1000} />
                                <section className='flex flex-row justify-end gap-4'>
                                    <Button
                                        size='sm'
                                        variant='secondary'
                                        onSelect={(e) => {
                                            e.preventDefault();
                                        }}
                                    >
                                        <Check className='mr-2 h-4 w-4' />
                                        Aplicar filtros
                                    </Button>
                                    <Button
                                        size='sm'
                                        variant='secondary'
                                        onSelect={(e) => {
                                            e.preventDefault();
                                        }}
                                    >
                                        <Eraser className='mr-2 h-4 w-4' />
                                        Limpiar filtros
                                    </Button>
                                </section>
                            </PopoverContent>
                        </Popover>
                    </section>
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
                                                <Muted>Fecha de recepción: {envio.fecha_recepcion.toDateString()}</Muted>
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
                    <section className='flex flex-row justify-between gap-4 w-full'>
                        <Input placeholder='Buscar aeropuerto...' value={search} onChange={(e) => setSearch(e.target.value)} />
                        <Popover>
                            <PopoverTrigger asChild>
                                <Button variant='secondary'>
                                    <ListFilter className='mr-2 h-4 w-4' /> Filtros
                                </Button>
                            </PopoverTrigger>
                            <PopoverContent align='start' className='w-fit flex flex-col gap-4'>
                                <Small>Rango de capacidad:</Small>
                                <Input type='number' placeholder='Cantidad minima' defaultValue={0} />
                                <Input type='number' placeholder='Cantidad maxima' defaultValue={1000} />
                                <Separator />
                                <Small>Zona horaria:</Small>
                                <Select>
                                    <SelectTrigger className='w-full'>
                                        <SelectValue placeholder='Zona horaria' />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value='-10'>GMT-10</SelectItem>
                                        <SelectItem value='-9'>GMT-9</SelectItem>
                                        <SelectItem value='-8'>GMT-8</SelectItem>
                                        <SelectItem value='-7'>GMT-7</SelectItem>
                                        <SelectItem value='-6'>GMT-6</SelectItem>
                                        <SelectItem value='-5'>GMT-5</SelectItem>
                                        <SelectItem value='-4'>GMT-4</SelectItem>
                                        <SelectItem value='-3'>GMT-3</SelectItem>
                                        <SelectItem value='-2'>GMT-2</SelectItem>
                                        <SelectItem value='-1'>GMT-1</SelectItem>
                                        <SelectItem value='0'>GMT</SelectItem>
                                        <SelectItem value='1'>GMT+1</SelectItem>
                                        <SelectItem value='2'>GMT+2</SelectItem>
                                        <SelectItem value='3'>GMT+3</SelectItem>
                                        <SelectItem value='4'>GMT+4</SelectItem>
                                        <SelectItem value='5'>GMT+5</SelectItem>
                                        <SelectItem value='6'>GMT+6</SelectItem>
                                        <SelectItem value='7'>GMT+7</SelectItem>
                                        <SelectItem value='8'>GMT+8</SelectItem>
                                        <SelectItem value='9'>GMT+9</SelectItem>
                                        <SelectItem value='10'>GMT+10</SelectItem>
                                    </SelectContent>
                                </Select>
                                <Separator />
                                <section className='flex flex-row justify-end gap-4'>
                                    <Button
                                        size='sm'
                                        variant='secondary'
                                        onSelect={(e) => {
                                            e.preventDefault();
                                        }}
                                    >
                                        <Check className='mr-2 h-4 w-4' />
                                        Aplicar filtros
                                    </Button>
                                    <Button
                                        size='sm'
                                        variant='secondary'
                                        onSelect={(e) => {
                                            e.preventDefault();
                                        }}
                                    >
                                        <Eraser className='mr-2 h-4 w-4' />
                                        Limpiar filtros
                                    </Button>
                                </section>
                            </PopoverContent>
                        </Popover>
                    </section>
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
                                                <Muted>Zona horaria: {aeropuerto.ubicacion.zona_horaria}</Muted>
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
    const [filteredVuelos, setFilteredVuelos] = useState<Vuelo[] | undefined>(vuelos);
    const [flyDateStart, setFlyDateStart] = useState<Date | undefined>(undefined);
    const [flyDateEnd, setFlyDateEnd] = useState<Date | undefined>(undefined);
    const [search, setSearch] = useState<string>("");

    return (
        <>
            {vuelos === undefined ? (
                SidebarSkeleton()
            ) : vuelos.length === 0 ? (
                <p>No hay vuelos disponibles</p>
            ) : (
                <>
                    <section className='flex flex-row justify-between gap-4 w-full'>
                        <Input placeholder='Buscar vuelo...' value={search} onChange={(e) => setSearch(e.target.value)} />
                        <Popover>
                            <PopoverTrigger asChild>
                                <Button variant='secondary'>
                                    <ListFilter className='mr-2 h-4 w-4' /> Filtros
                                </Button>
                            </PopoverTrigger>
                            <PopoverContent align='start' className='w-fit flex flex-col gap-4'>
                                <Small>Fecha de salida de vuelo:</Small>
                                <section className='flex flex-col gap-2'>
                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant={"outline"}
                                                className={cn(
                                                    "w-[280px] justify-start text-left font-normal",
                                                    !flyDateStart && "text-muted-foreground"
                                                )}
                                            >
                                                <CalendarIcon className='mr-2 h-4 w-4' />
                                                {flyDateStart ? format(flyDateStart, "PPP") : <span>Fecha de inicio</span>}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className='w-auto p-0'>
                                            <Calendar mode='single' selected={flyDateStart} onSelect={setFlyDateStart} initialFocus />
                                        </PopoverContent>
                                    </Popover>
                                    <Popover>
                                        <PopoverTrigger asChild>
                                            <Button
                                                variant={"outline"}
                                                className={cn(
                                                    "w-[280px] justify-start text-left font-normal",
                                                    !flyDateEnd && "text-muted-foreground"
                                                )}
                                            >
                                                <CalendarIcon className='mr-2 h-4 w-4' />
                                                {flyDateEnd ? format(flyDateEnd, "PPP") : <span>Fecha de fin</span>}
                                            </Button>
                                        </PopoverTrigger>
                                        <PopoverContent className='w-auto p-0'>
                                            <Calendar mode='single' selected={flyDateEnd} onSelect={setFlyDateEnd} initialFocus />
                                        </PopoverContent>
                                    </Popover>
                                </section>
                                <Separator />
                                <Small>Rango de capacidad:</Small>
                                <Input type='number' placeholder='Cantidad minima' defaultValue={0} />
                                <Input type='number' placeholder='Cantidad maxima' defaultValue={1000} />
                                <Separator />
                                <section className='flex flex-row justify-end gap-4'>
                                    <Button
                                        size='sm'
                                        variant='secondary'
                                        onSelect={(e) => {
                                            e.preventDefault();
                                        }}
                                    >
                                        <Check className='mr-2 h-4 w-4' />
                                        Aplicar filtros
                                    </Button>
                                    <Button
                                        size='sm'
                                        variant='secondary'
                                        onSelect={(e) => {
                                            e.preventDefault();
                                        }}
                                    >
                                        <Eraser className='mr-2 h-4 w-4' />
                                        Limpiar filtros
                                    </Button>
                                </section>
                            </PopoverContent>
                        </Popover>
                    </section>
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
                                                <Muted>Fecha origen: {vuelo.fecha_origen.toDateString()}</Muted>
                                                <Muted>Fecha destino: {vuelo.fecha_destino.toDateString()}</Muted>
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
