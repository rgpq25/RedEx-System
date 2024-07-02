"use client";

import { cn } from "@/lib/utils";

import { ChevronsLeft, ChevronsRight, Eraser, Info, ListFilter, MapPinned } from "lucide-react";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
} from "@/components/ui/pagination";
import { SliderDouble } from "@/components/ui/slider-double";
import { Checkbox } from "@/components/ui/checkbox";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import Chip from "@/components/ui/chip";
import { Label } from "@/components/ui/label";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Large, Muted, Small } from "@/components/ui/typography";
import { useCallback, useEffect, useMemo, useState } from "react";

import { Operacion, Aeropuerto, Envio, Vuelo, Ubicacion, EstadoAlmacen, HistoricoValores } from "@/lib/types";
import { continentes } from "@/lib/sample";
import { formatDateShort, formatDateTimeShort } from "@/lib/date";
import { api } from "@/lib/api";
import { getCurrentAirportOcupation } from "@/lib/map-utils";
import { useFilteredFlightsContext } from "@/components/contexts/flights-filter";

const DEFAULT_RANGO_CAPACIDAD_ENVIOS: [number, number] = [0, 200];
const DEFAULT_RANGO_CAPACIDAD_AEROPUERTOS: [number, number] = [1000, 2000];
const DEFAULT_RANGO_CAPACIDAD_VUELOS: [number, number] = [0, 600];

interface SidebarProps {
    envios?: Envio[];
    aeropuertos?: Aeropuerto[];
    vuelos: Vuelo[];
    estadoAlmacen: EstadoAlmacen | null;
    onClicksEnvio: {
        onClickLocation: (envio: Envio) => void;
        onClickInfo: (envio: Envio) => void;
    };
    onClicksAeropuerto: {
        onClickLocation: (aeropuerto: Aeropuerto) => void;
        onClickInfo: (aeropuerto: Aeropuerto) => void;
    };
    onClickVuelo: (vuelo: Vuelo) => void;
    tiempoActual: Date | undefined;
    props?: any;
}

export default function Sidebar({
    envios,
    aeropuertos,
    vuelos,
    estadoAlmacen,
    onClicksEnvio,
    onClicksAeropuerto,
    onClickVuelo,
    tiempoActual,
    ...props
}: SidebarProps) {
    const [selectedOperation, setSelectedOperation] = useState<Operacion>(Operacion.Envios);
    const [visible, setVisible] = useState<boolean>(true);
    const [ubicaciones, setUbicaciones] = useState<Ubicacion[]>([]);

    const getUbicaciones = useCallback(async () => {
        const ubicaciones = await api(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/ubicacion/`,
            (data: Ubicacion[]) => {
                setUbicaciones(data);
            },
            (error) => {
                console.error(error);
            }
        );
        return ubicaciones;
    }, []);

    useEffect(() => {
        getUbicaciones();
    });

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
                <CardHeader className='w-full'>
                    <div className='w-full flex flex-row justify-between items-center'>
                        <Tabs
                            value={selectedOperation}
                            onValueChange={(e) => setSelectedOperation(e as Operacion)}
                            className=''
                        >
                            <TabsList>
                                <TabsTrigger value='Envios'>Envios</TabsTrigger>
                                <TabsTrigger value='Aeropuertos'>Aeropuertos</TabsTrigger>
                                <TabsTrigger value='Vuelos'>Vuelos</TabsTrigger>
                            </TabsList>
                        </Tabs>
                        <ChevronsLeft
                            className='cursor-pointer h-10 w-10 p-2 stroke-muted-foreground hover:stroke-black transition-all duration-200 ease-in-out'
                            onClick={() => setVisible(false)}
                        >
                            Cerrar
                        </ChevronsLeft>
                    </div>
                </CardHeader>
                <CardContent className='w-full flex flex-col items-center space-y-6 *:w-full overflow-hidden'>
                    <Separator />
                    {selectedOperation === Operacion.Envios && (
                        <Envios
                            envios={envios}
                            onClicks={onClicksEnvio}
                            tiempoActual={tiempoActual}
                            ubicaciones={ubicaciones}
                        />
                    )}
                    {selectedOperation === Operacion.Aeropuertos && (
                        <Aeropuertos
                            aeropuertos={aeropuertos}
                            onClicks={onClicksAeropuerto}
                            ubicaciones={ubicaciones}
                            estadoAlmacen={estadoAlmacen}
                            currentTime={tiempoActual}
                        />
                    )}
                    {selectedOperation === Operacion.Vuelos && (
                        <Vuelos
                            vuelos={vuelos}
                            onClick={onClickVuelo}
                            tiempoActual={tiempoActual}
                            ubicaciones={ubicaciones}
                        />
                    )}
                </CardContent>
            </Card>
        </>
    );
}

function Envios({
    envios,
    onClicks,
    tiempoActual,
    ubicaciones,
}: {
    envios: Envio[] | undefined;
    onClicks: {
        onClickLocation: (envio: Envio) => void;
        onClickInfo: (envio: Envio) => void;
    };
    tiempoActual?: Date | undefined;
    ubicaciones: Ubicacion[];
}) {
    const [receptionDateStart, setReceptionDateStart] = useState<Date | undefined>(undefined);
    const [receptionDateEnd, setReceptionDateEnd] = useState<Date | undefined>(undefined);
    const [page, setPage] = useState<number>(1);
    const rowsPerPage = 10;
    const [search, setSearch] = useState<string>("");
    const hasSearchFilter = Boolean(search);
    const [continentesOrigenFilter, setContinentesOrigenFilter] = useState<string[]>(continentes);
    const [continentesDestinoFilter, setContinentesDestinoFilter] = useState<string[]>(continentes);
    const [paisOrigenFilter, setPaisOrigenFilter] = useState<string | undefined>(undefined);
    const [paisDestinoFilter, setPaisDestinoFilter] = useState<string | undefined>(undefined);
    const [rangoCapacidadFilter, setRangoCapacidadFilter] = useState<[number, number]>(DEFAULT_RANGO_CAPACIDAD_ENVIOS);

    const minCapacidad = Math.min(...rangoCapacidadFilter);
    const maxCapacidad = Math.max(...rangoCapacidadFilter);

    const filteredItems = useMemo(() => {
        let filteredEnvios = envios;
        if (tiempoActual) {
            filteredEnvios = filteredEnvios?.filter(
                (envio) =>
                    envio.fechaRecepcion.getTime() <= tiempoActual.getTime() && envio.fechaLimiteEntrega.getTime() >= tiempoActual.getTime()
            );
        }
        if (continentesOrigenFilter) {
            filteredEnvios = filteredEnvios?.filter((envio) => continentesOrigenFilter?.includes(envio.ubicacionOrigen.continente));
        }
        if (continentesDestinoFilter) {
            filteredEnvios = filteredEnvios?.filter((envio) => continentesDestinoFilter?.includes(envio.ubicacionDestino.continente));
        }
        if (paisOrigenFilter) {
            filteredEnvios = filteredEnvios?.filter((envio) => envio.ubicacionOrigen.pais === paisOrigenFilter);
        }
        if (paisDestinoFilter) {
            filteredEnvios = filteredEnvios?.filter((envio) => envio.ubicacionDestino.pais === paisDestinoFilter);
        }
        filteredEnvios;
        if (rangoCapacidadFilter) {
            filteredEnvios = filteredEnvios?.filter(
                (envio) => envio.cantidadPaquetes >= minCapacidad && envio.cantidadPaquetes <= maxCapacidad
            );
        }
        if (receptionDateStart && receptionDateEnd) {
            filteredEnvios = filteredEnvios?.filter(
                (envio) =>
                    envio.fechaRecepcion.getTime() >= receptionDateStart.getTime() &&
                    envio.fechaRecepcion.getTime() <= receptionDateEnd.getTime()
            );
        }
        if (hasSearchFilter) {
            filteredEnvios = filteredEnvios?.filter((envio) => envio.id.toString().includes(search));
        }
        return filteredEnvios;
    }, [
        envios,
        receptionDateStart,
        receptionDateEnd,
        search,
        hasSearchFilter,
        tiempoActual,
        rangoCapacidadFilter,
        minCapacidad,
        maxCapacidad,
        continentesOrigenFilter,
        continentesDestinoFilter,
        paisOrigenFilter,
        paisDestinoFilter,
    ]);

    const pages = Math.ceil((filteredItems?.length || 0) / 10);

    const items = useMemo(() => {
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        return filteredItems?.slice(start, end);
    }, [filteredItems, page, rowsPerPage]);

    const onNextPage = useCallback(() => {
        if (page < pages) {
            setPage(page + 1);
        }
    }, [page, pages]);
    const onPreviousPage = useCallback(() => {
        if (page > 1) {
            setPage(page - 1);
        }
    }, [page]);
    const onSearchChange = useCallback((value: string) => {
        if (value) {
            setSearch(value);
            setPage(1);
        } else {
            setSearch("");
        }
    }, []);
    const onClearFilters = useCallback(() => {
        setReceptionDateStart(undefined);
        setReceptionDateEnd(undefined);
        setRangoCapacidadFilter(DEFAULT_RANGO_CAPACIDAD_ENVIOS);
        setContinentesOrigenFilter(continentes);
        setContinentesDestinoFilter(continentes);
        setPaisOrigenFilter(undefined);
        setPaisDestinoFilter(undefined);
    }, []);

    const renderCard = useCallback(
        (envio: Envio) => (
            <Card
                key={envio.id}
                className='p-3 *:p-0 hover:bg-gray-100 cursor-pointer'
                onClick={() => onClicks.onClickInfo(envio)}
            >
                <CardHeader>
                    <Large>{envio.id}</Large>
                </CardHeader>
                <CardContent>
                    <Muted>Fecha de recepción: {formatDateTimeShort(envio.fechaRecepcion)}</Muted>
                    <Muted>Cantidad: {envio.cantidadPaquetes}</Muted>
                    <span className='flex flex-wrap *:flex-grow'>
                        <Muted>Origen: {envio.ubicacionOrigen.ciudad}</Muted>
                        <Muted>Destino: {envio.ubicacionDestino.ciudad}</Muted>
                    </span>
                </CardContent>
            </Card>
        ),
        [onClicks]
    );

    const renderFilters = useCallback(() => {
        return (
            <>
                <section className='flex flex-row justify-between gap-4 w-full'>
                    <Input
                        placeholder='Buscar envío...'
                        value={search}
                        onChange={(e) => onSearchChange(e.target.value)}
                    />
                    <Popover>
                        <PopoverTrigger asChild>
                            <Button variant='secondary'>
                                <ListFilter className='mr-2 h-4 w-4' /> Filtros
                            </Button>
                        </PopoverTrigger>
                        <PopoverContent
                            align='start'
                            className='w-fit flex flex-col gap-4'
                        >
                            <Small>Rango de cantidad de paquetes:</Small>
                            <div className='flex justify-between'>
                                <Small>{minCapacidad}</Small>
                                <Small>{maxCapacidad}</Small>
                            </div>
                            <SliderDouble
                                onValueChange={(range) => {
                                    const [newMin, newMax] = range;
                                    setRangoCapacidadFilter([newMin, newMax]);
                                }}
                                value={rangoCapacidadFilter}
                                min={DEFAULT_RANGO_CAPACIDAD_ENVIOS[0]}
                                max={DEFAULT_RANGO_CAPACIDAD_ENVIOS[1]}
                                defaultValue={DEFAULT_RANGO_CAPACIDAD_ENVIOS}
                                step={1}
                            />
                            <Separator />
                            {/* <Small>Rango de fecha de recepción:</Small>
                            <section className='flex flex-row gap-2'>
                                <Popover>
                                    <PopoverTrigger asChild>
                                        <Button
                                            variant={"outline"}
                                            className={cn(
                                                "w-fit justify-start text-left font-normal",
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
                                                "w-fit justify-start text-left font-normal",
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
                            <Separator /> */}
                            <section className='flex h-fit items-center space-x-4'>
                                <div className='flex flex-col gap-4'>
                                    <Small>Continente de origen:</Small>
                                    {continentes.map((continente) => (
                                        <div
                                            className='ml-4 flex items-center space-x-2'
                                            key={continente}
                                        >
                                            <Checkbox
                                                id={continente}
                                                checked={continentesOrigenFilter.includes(continente)}
                                                onCheckedChange={(checked) => {
                                                    setContinentesOrigenFilter((prev) =>
                                                        checked ? [...prev, continente] : prev.filter((c) => c !== continente)
                                                    );
                                                }}
                                            />
                                            <Label htmlFor={continente}>{continente}</Label>
                                        </div>
                                    ))}
                                </div>
                                <Separator orientation='vertical' />
                                <div className='flex flex-col gap-4'>
                                    <Small>Continente de destino:</Small>
                                    {continentes.map((continente) => (
                                        <div
                                            className='ml-4 flex items-center space-x-2'
                                            key={continente}
                                        >
                                            <Checkbox
                                                id={continente}
                                                checked={continentesDestinoFilter.includes(continente)}
                                                onCheckedChange={(checked) => {
                                                    setContinentesDestinoFilter((prev) =>
                                                        checked ? [...prev, continente] : prev.filter((c) => c !== continente)
                                                    );
                                                }}
                                            />
                                            <Label htmlFor={continente}>{continente}</Label>
                                        </div>
                                    ))}
                                </div>
                            </section>
                            <Separator />
                            <section className='flex h-fit items-center space-x-4 justify-between'>
                                <div className='flex flex-col gap-2'>
                                    <Small>Pais de origen:</Small>
                                    <Select
                                        key={paisOrigenFilter}
                                        onValueChange={(e) => setPaisOrigenFilter(e as string)}
                                        value={paisOrigenFilter}
                                        defaultValue={paisOrigenFilter}
                                    >
                                        <SelectTrigger className='w-full'>
                                            <SelectValue placeholder={paisOrigenFilter || "Pais de origen"} />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {ubicaciones
                                                ?.sort((a, b) => a.pais.localeCompare(b.pais))
                                                .map((ubicacion) => (
                                                    <SelectItem
                                                        key={ubicacion.pais}
                                                        value={ubicacion.pais}
                                                    >
                                                        {ubicacion.pais}
                                                    </SelectItem>
                                                ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                                <Separator orientation='vertical' />
                                <div className='flex flex-col gap-2'>
                                    <Small>Pais de destino:</Small>
                                    <Select
                                        key={paisDestinoFilter}
                                        onValueChange={(e) => setPaisDestinoFilter(e as string)}
                                        value={paisDestinoFilter}
                                        defaultValue={paisDestinoFilter}
                                    >
                                        <SelectTrigger className='w-full'>
                                            <SelectValue placeholder={paisDestinoFilter || "Pais de destino"} />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {ubicaciones
                                                ?.sort((a, b) => a.pais.localeCompare(b.pais))
                                                .map((ubicacion) => (
                                                    <SelectItem
                                                        key={ubicacion.pais}
                                                        value={ubicacion.pais}
                                                    >
                                                        {ubicacion.pais}
                                                    </SelectItem>
                                                ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                            </section>
                            <Separator />
                            <section className='flex flex-row justify-end gap-4'>
                                <Button
                                    size='sm'
                                    variant='secondary'
                                    onClick={(e) => {
                                        e.preventDefault();
                                        onClearFilters();
                                    }}
                                >
                                    <Eraser className='mr-2 h-4 w-4' />
                                    Limpiar filtros
                                </Button>
                            </section>
                        </PopoverContent>
                    </Popover>
                </section>
            </>
        );
    }, [
        search,
        onSearchChange,
        onClearFilters,
        rangoCapacidadFilter,
        minCapacidad,
        maxCapacidad,
        continentesOrigenFilter,
        continentesDestinoFilter,
        paisOrigenFilter,
        paisDestinoFilter,
        ubicaciones,
    ]);

    return (
        <>
            {items === undefined ? (
                SidebarSkeleton()
            ) : (
                <>
                    <Muted>Cantidad de envios: {filteredItems?.length}</Muted>
                    {renderFilters()}
                    {items.length === 0 ? (
                        <div className='h-screen flex'>
                            <p className='m-auto text-muted-foreground text-sm'>No hay envíos disponibles</p>
                        </div>
                    ) : (
                        <>
                            <ScrollArea className='h-screen'>
                                <section className='flex flex-col gap-4'>{items?.map(renderCard)}</section>
                            </ScrollArea>
                            <Pagination>
                                <PaginationContent>
                                    <PaginationItem>
                                        <PaginationPrevious
                                            onClick={(e) => {
                                                e.preventDefault();
                                                onPreviousPage();
                                            }}
                                        />
                                    </PaginationItem>
                                    <PaginationItem>
                                        <PaginationLink>{page}</PaginationLink>
                                    </PaginationItem>
                                    <PaginationItem>
                                        <PaginationNext
                                            onClick={(e) => {
                                                e.preventDefault();
                                                onNextPage();
                                            }}
                                        />
                                    </PaginationItem>
                                </PaginationContent>
                            </Pagination>
                        </>
                    )}
                </>
            )}
        </>
    );
}

function Aeropuertos({
    aeropuertos,
    onClicks,
    ubicaciones,
    estadoAlmacen,
    currentTime,
}: {
    aeropuertos: Aeropuerto[] | undefined;
    onClicks: {
        onClickLocation: (aeropuerto: Aeropuerto) => void;
        onClickInfo: (aeropuerto: Aeropuerto) => void;
    };
    ubicaciones: Ubicacion[];
    estadoAlmacen: EstadoAlmacen | null;
    currentTime: Date | undefined;
}) {
    const [search, setSearch] = useState<string>("");
    const hasSearchFilter = Boolean(search);
    const [continentesFilter, setContinentesFilter] = useState<string[]>(continentes);
    const [rangoCapacidadFilter, setRangoCapacidadFilter] = useState<[number, number]>(DEFAULT_RANGO_CAPACIDAD_AEROPUERTOS);
    const [gmtFilter, setGmtFilter] = useState<string | undefined>(undefined);

    const minCapacidad = Math.min(...rangoCapacidadFilter);
    const maxCapacidad = Math.max(...rangoCapacidadFilter);

    const items = useMemo(() => {
        let filteredAeropuertos = aeropuertos;
        filteredAeropuertos = filteredAeropuertos?.sort((a, b) => a.ubicacion.ciudad.localeCompare(b.ubicacion.ciudad));
        if (continentesFilter) {
            filteredAeropuertos = filteredAeropuertos?.filter((aeropuerto) => continentesFilter?.includes(aeropuerto.ubicacion.continente));
        }
        if (rangoCapacidadFilter) {
            filteredAeropuertos = filteredAeropuertos?.filter(
                (aeropuerto) => aeropuerto.capacidadMaxima >= minCapacidad && aeropuerto.capacidadMaxima <= maxCapacidad
            );
        }
        if (gmtFilter) {
            filteredAeropuertos = filteredAeropuertos?.filter((aeropuerto) => aeropuerto.ubicacion.zonaHoraria === gmtFilter);
        }
        if (hasSearchFilter) {
            filteredAeropuertos = filteredAeropuertos?.filter(
                (aeropuerto) =>
                    aeropuerto.ubicacion.ciudadAbreviada.toLowerCase().includes(search.toLowerCase()) ||
                    aeropuerto.ubicacion.ciudad.toLowerCase().includes(search.toLowerCase())
            );
        }
        return filteredAeropuertos;
    }, [aeropuertos, search, hasSearchFilter, continentesFilter, rangoCapacidadFilter, minCapacidad, maxCapacidad, gmtFilter]);

    const onSearchChange = useCallback((value: string) => {
        if (value) {
            setSearch(value);
        } else {
            setSearch("");
        }
    }, []);
    const onClearFilters = useCallback(() => {
        setContinentesFilter(continentes);
        setRangoCapacidadFilter(DEFAULT_RANGO_CAPACIDAD_AEROPUERTOS);
        setGmtFilter(undefined);
    }, []);

    const renderCard = useCallback(
        (aeropuerto: Aeropuerto, usoHistorico: HistoricoValores) => {
            const currentCapacity = getCurrentAirportOcupation(usoHistorico, currentTime);
            const porcentajeOcupacion = currentCapacity / aeropuerto.capacidadMaxima;

            return (
                <Card
                    key={aeropuerto.id}
                    className='p-3 *:p-0 relative'
                    onClick={(e) => {
                        //e.preventDefault();
                        // onClicks.onClickLocation(aeropuerto);
                        // onClicks.onClickInfo(aeropuerto);
                    }}
                >
                    <CardHeader>
                        <Large>
                            {aeropuerto.ubicacion.ciudad} ({aeropuerto.ubicacion.ciudadAbreviada.toUpperCase()})
                        </Large>
                    </CardHeader>
                    <CardContent>
                        <div className='flex flex-row items-center gap-1'>
                            <Muted>
                                Capacidad: {currentCapacity} / {aeropuerto.capacidadMaxima}
                            </Muted>
                            {porcentajeOcupacion <= 0.3 ? (
                                <Chip color='green'>Bajo</Chip>
                            ) : porcentajeOcupacion <= 0.6 ? (
                                <Chip color='yellow'>Medio</Chip>
                            ) : (
                                <Chip color='red'>Alto</Chip>
                            )}
                        </div>
                        <Muted>Zona horaria: {aeropuerto.ubicacion.zonaHoraria}</Muted>
                    </CardContent>
                    <div className='absolute right-0 top-0 bottom-0 flex flex-col justify-center items-center gap-1 w-[59px]'>
                        <Button
                            variant={"outline"}
                            className='w-8 h-8 p-0'
                            onClick={(e) => {
                                e.preventDefault();
                                onClicks.onClickLocation(aeropuerto);
                            }}
                        >
                            <MapPinned className='w-4 h-4 ' />
                        </Button>
                        <Button
                            variant={"outline"}
                            className='w-8 h-8 p-0'
                            onClick={(e) => {
                                e.preventDefault();
                                onClicks.onClickInfo(aeropuerto);
                            }}
                        >
                            <Info className='w-4 h-4 ' />
                        </Button>
                    </div>
                </Card>
            );
        },
        [onClicks, currentTime]
    );

    const renderFilters = useCallback(() => {
        return (
            <>
                <section className='flex flex-row justify-between gap-4 w-full'>
                    <Input
                        placeholder='Buscar aeropuerto...'
                        value={search}
                        onChange={(e) => onSearchChange(e.target.value)}
                    />
                    <Popover>
                        <PopoverTrigger asChild>
                            <Button variant='secondary'>
                                <ListFilter className='mr-2 h-4 w-4' /> Filtros
                            </Button>
                        </PopoverTrigger>
                        <PopoverContent
                            align='start'
                            className='w-fit flex flex-col gap-4'
                        >
                            <Small>Rango de capacidad maxima:</Small>
                            <div className='flex justify-between'>
                                <Small>{minCapacidad}</Small>
                                <Small>{maxCapacidad}</Small>
                            </div>
                            <SliderDouble
                                onValueChange={(range) => {
                                    const [newMin, newMax] = range;
                                    setRangoCapacidadFilter([newMin, newMax]);
                                }}
                                value={rangoCapacidadFilter}
                                min={DEFAULT_RANGO_CAPACIDAD_AEROPUERTOS[0]}
                                max={DEFAULT_RANGO_CAPACIDAD_AEROPUERTOS[1]}
                                defaultValue={DEFAULT_RANGO_CAPACIDAD_AEROPUERTOS}
                                step={5}
                            />
                            <Separator />
                            <Small>Continente:</Small>
                            {continentes.map((continente) => (
                                <div
                                    className='ml-4 flex items-center space-x-2'
                                    key={continente}
                                >
                                    <Checkbox
                                        id={continente}
                                        checked={continentesFilter.includes(continente)}
                                        onCheckedChange={(checked) => {
                                            setContinentesFilter((prev) =>
                                                checked ? [...prev, continente] : prev.filter((c) => c !== continente)
                                            );
                                        }}
                                    />
                                    <Label htmlFor={continente}>{continente}</Label>
                                </div>
                            ))}
                            <Separator />
                            <Small>Zona horaria:</Small>
                            <Select
                                key={gmtFilter}
                                onValueChange={(e) => setGmtFilter(e as string)}
                                value={gmtFilter}
                                defaultValue={gmtFilter}
                            >
                                <SelectTrigger className='w-full'>
                                    <SelectValue placeholder={gmtFilter || "GMT"} />
                                </SelectTrigger>
                                <SelectContent>
                                    {ubicaciones
                                        ?.reduce((acc, ubicacion) => {
                                            if (!acc.includes(ubicacion.zonaHoraria)) {
                                                acc.push(ubicacion.zonaHoraria);
                                            }
                                            return acc;
                                        }, [] as string[])
                                        .sort((a, b) => a.localeCompare(b))
                                        .map((zonaHoraria) => (
                                            <SelectItem
                                                key={zonaHoraria}
                                                value={zonaHoraria}
                                            >
                                                {zonaHoraria}
                                            </SelectItem>
                                        ))}
                                </SelectContent>
                            </Select>
                            <Separator />
                            <section className='flex flex-row justify-end gap-4'>
                                <Button
                                    size='sm'
                                    variant='secondary'
                                    onClick={(e) => {
                                        e.preventDefault();
                                        onClearFilters();
                                    }}
                                >
                                    <Eraser className='mr-2 h-4 w-4' />
                                    Limpiar filtros
                                </Button>
                            </section>
                        </PopoverContent>
                    </Popover>
                </section>
            </>
        );
    }, [
        search,
        onSearchChange,
        continentesFilter,
        rangoCapacidadFilter,
        minCapacidad,
        maxCapacidad,
        onClearFilters,
        ubicaciones,
        gmtFilter,
    ]);

    return (
        <>
            {items === undefined ? (
                SidebarSkeleton()
            ) : (
                <>
                    <Muted>Cantidad de aeropuertos: {items.length}</Muted>
                    {renderFilters()}
                    {items.length === 0 ? (
                        <div className='h-screen flex'>
                            <p className='m-auto text-muted-foreground text-sm'>No hay aeropuertos disponibles</p>
                        </div>
                    ) : (
                        <ScrollArea className='flex-1 pr-3'>
                            <section className='flex flex-col gap-4'>
                                {items.map((airport) => renderCard(airport, estadoAlmacen?.uso_historico[airport.ubicacion.id] || {}))}
                            </section>
                        </ScrollArea>
                    )}
                </>
            )}
        </>
    );
}

function Vuelos({
    vuelos,
    onClick,
    tiempoActual,
    ubicaciones,
}: {
    vuelos: Vuelo[];
    onClick: (vuelo: Vuelo) => void;
    tiempoActual?: Date | undefined;
    ubicaciones: Ubicacion[];
}) {
    const { search,
        setSearch,
        hasSearchFilter,
        continentesFilter,
        setContinentesFilter,
        paisOrigenFilter,
        setPaisOrigenFilter,
        paisDestinoFilter,
        setPaisDestinoFilter,
        rangoCapacidadFilter,
        setRangoCapacidadFilter,
        minCapacidad,
        maxCapacidad,
        getFilteredFlights } = useFilteredFlightsContext();
    const [page, setPage] = useState<number>(1);
    const rowsPerPage = 10;

    const filteredItems = vuelos;

    const pages = Math.ceil((filteredItems?.length || 0) / rowsPerPage);

    const items = useMemo(() => {
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        return filteredItems?.slice(start, end);
    }, [filteredItems, page, rowsPerPage]);

    const onNextPage = useCallback(() => {
        if (page < pages) {
            setPage(page + 1);
        }
    }, [page, pages]);
    const onPreviousPage = useCallback(() => {
        if (page > 1) {
            setPage(page - 1);
        }
    }, [page]);
    const onSearchChange = useCallback((value: string) => {
        if (value) {
            setSearch(value);
            setPage(1);
        } else {
            setSearch("");
        }
    }, []);
    const onClearFilters = useCallback(() => {
        setContinentesFilter(continentes);
        setRangoCapacidadFilter(DEFAULT_RANGO_CAPACIDAD_VUELOS);
        setPaisOrigenFilter(undefined);
        setPaisDestinoFilter(undefined);
    }, []);

    const renderCard = useCallback(
        (vuelo: Vuelo) => (
            <Card
                key={vuelo.id}
                className='p-3 *:p-0 hover:bg-gray-100 cursor-pointer'
                onClick={() => onClick(vuelo)}
            >
                <CardHeader>
                    <Large>{vuelo.id}</Large>
                </CardHeader>
                <CardContent>
                    <span className='flex flex-wrap *:flex-grow'>
                        <Muted>Origen: {vuelo.planVuelo.ciudadOrigen.ciudad}</Muted>
                        <Muted>Destino: {vuelo.planVuelo.ciudadDestino.ciudad}</Muted>
                    </span>
                    <Muted>Fecha origen: {formatDateShort(vuelo.fechaSalida)}</Muted>
                    <Muted>Fecha destino: {formatDateShort(vuelo.fechaLlegada)}</Muted>
                    <Muted>Capacidad: {vuelo.capacidadUtilizada}</Muted>
                </CardContent>
            </Card>
        ),
        [onClick]
    );

    const renderFilters = useCallback(() => {
        return (
            <>
                <section className='flex flex-row justify-between gap-4 w-full'>
                    <Input
                        placeholder='Buscar vuelo...'
                        value={search}
                        onChange={(e) => onSearchChange(e.target.value)}
                    />
                    <Popover>
                        <PopoverTrigger asChild>
                            <Button variant='secondary'>
                                <ListFilter className='mr-2 h-4 w-4' /> Filtros
                            </Button>
                        </PopoverTrigger>
                        <PopoverContent
                            align='start'
                            className='w-fit flex flex-col gap-4'
                        >
                            <Small>Rango de capacidad usada:</Small>
                            <div className='flex justify-between'>
                                <Small>{minCapacidad}</Small>
                                <Small>{maxCapacidad}</Small>
                            </div>
                            <SliderDouble
                                onValueChange={(range) => {
                                    const [newMin, newMax] = range;
                                    setRangoCapacidadFilter([newMin, newMax]);
                                }}
                                value={rangoCapacidadFilter}
                                min={DEFAULT_RANGO_CAPACIDAD_VUELOS[0]}
                                max={DEFAULT_RANGO_CAPACIDAD_VUELOS[1]}
                                defaultValue={DEFAULT_RANGO_CAPACIDAD_VUELOS}
                                step={1}
                            />
                            <Separator />
                            <Small>Continente de origen:</Small>
                            {continentes.map((continente) => (
                                <div
                                    className='ml-4 flex items-center space-x-2'
                                    key={continente}
                                >
                                    <Checkbox
                                        id={continente}
                                        checked={continentesFilter.includes(continente)}
                                        onCheckedChange={(checked) => {
                                            setContinentesFilter((prev) =>
                                                checked ? [...prev, continente] : prev.filter((c) => c !== continente)
                                            );
                                        }}
                                    />
                                    <Label htmlFor={continente}>{continente}</Label>
                                </div>
                            ))}
                            <Separator />
                            <section className='flex h-fit items-center space-x-4 justify-between'>
                                <div className='flex flex-col gap-2'>
                                    <Small>Pais de origen:</Small>
                                    <Select
                                        key={paisOrigenFilter}
                                        onValueChange={(e) => setPaisOrigenFilter(e as string)}
                                        value={paisOrigenFilter}
                                        defaultValue={paisOrigenFilter}
                                    >
                                        <SelectTrigger className='w-full'>
                                            <SelectValue placeholder={paisOrigenFilter || "Pais de origen"} />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {ubicaciones
                                                ?.sort((a, b) => a.pais.localeCompare(b.pais))
                                                .map((ubicacion) => (
                                                    <SelectItem
                                                        key={ubicacion.pais}
                                                        value={ubicacion.pais}
                                                    >
                                                        {ubicacion.pais}
                                                    </SelectItem>
                                                ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                                <Separator orientation='vertical' />
                                <div className='flex flex-col gap-2'>
                                    <Small>Pais de destino:</Small>
                                    <Select
                                        key={paisDestinoFilter}
                                        onValueChange={(e) => setPaisDestinoFilter(e as string)}
                                        value={paisDestinoFilter}
                                        defaultValue={paisDestinoFilter}
                                    >
                                        <SelectTrigger className='w-full'>
                                            <SelectValue placeholder={paisDestinoFilter || "Pais de destino"} />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {ubicaciones
                                                ?.sort((a, b) => a.pais.localeCompare(b.pais))
                                                .map((ubicacion) => (
                                                    <SelectItem
                                                        key={ubicacion.pais}
                                                        value={ubicacion.pais}
                                                    >
                                                        {ubicacion.pais}
                                                    </SelectItem>
                                                ))}
                                        </SelectContent>
                                    </Select>
                                </div>
                            </section>

                            <Separator />
                            <section className='flex flex-row justify-end gap-4'>
                                <Button
                                    size='sm'
                                    variant='secondary'
                                    onClick={(e) => {
                                        e.preventDefault();
                                        onClearFilters();
                                    }}
                                >
                                    <Eraser className='mr-2 h-4 w-4' />
                                    Limpiar filtros
                                </Button>
                            </section>
                        </PopoverContent>
                    </Popover>
                </section>
            </>
        );
    }, [
        search,
        onSearchChange,
        continentesFilter,
        onClearFilters,
        rangoCapacidadFilter,
        minCapacidad,
        maxCapacidad,
        ubicaciones,
        paisOrigenFilter,
        paisDestinoFilter,
    ]);

    return (
        <>
            {items === undefined ? (
                SidebarSkeleton()
            ) : (
                <>
                    <Muted>Cantidad de vuelos: {filteredItems?.length}</Muted>
                    {renderFilters()}
                    {items.length === 0 ? (
                        <div className='h-screen flex'>
                            <p className='m-auto text-muted-foreground text-sm'>No hay vuelos disponibles</p>
                        </div>
                    ) : (
                        <>
                            <ScrollArea className='h-screen'>
                                <section className='flex flex-col gap-4'>{items.map(renderCard)}</section>
                            </ScrollArea>
                            <Pagination>
                                <PaginationContent>
                                    <PaginationItem>
                                        <PaginationPrevious
                                            onClick={(e) => {
                                                e.preventDefault();
                                                onPreviousPage();
                                            }}
                                        />
                                    </PaginationItem>
                                    <PaginationItem>
                                        <PaginationLink>{page}</PaginationLink>
                                    </PaginationItem>
                                    <PaginationItem>
                                        <PaginationNext
                                            onClick={(e) => {
                                                e.preventDefault();
                                                onNextPage();
                                            }}
                                        />
                                    </PaginationItem>
                                </PaginationContent>
                            </Pagination>
                        </>
                    )}
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
