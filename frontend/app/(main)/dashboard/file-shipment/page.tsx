"use client";

import { useRef, useState, useCallback, useEffect } from "react";
import { cn } from "@/lib/utils";
import Link from "next/link";

import { Button, buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { toast } from "sonner";
import { H1, Large, Lead, Muted, Small } from "@/components/ui/typography";
import { FileUp } from "lucide-react";

import { RowShipmentType } from "@/lib/types";
import { ShipmentTable } from "../_components/shipment-table";

import { api, apiT } from "@/lib/api";
import { currentTimeString, getTimeString } from "@/lib/date";
import { Label } from "@/components/ui/label";
import { DatePicker } from "@/components/ui/date-picker";

// API to get the date from backend server.
const getCurrentDate = async () => {
    try {
        const response = await apiT<string>(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/time/now`
        );
        return response;
    } catch (error) {
        console.error("Error al obtener la fecha del servidor", error);
    }
};

export default function FileShipment() {
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [file, setFile] = useState<File | undefined>(undefined);
    const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined);
    const [selectedTime, setSelectedTime] = useState<string>('');
    const [shipments, setShipments] = useState<RowShipmentType[]>([]);
    const [loading, setLoading] = useState(false);
    const [useCustomDate, setUseCustomDate] = useState(false);
    const isDisabled = shipments.length === 0 || loading;

    useEffect(() => {
        const intervalId = setInterval(async () => {
            let stringDate = await getCurrentDate();
            let date = new Date(stringDate || '');

            setSelectedDate(new Date(date));
            setSelectedTime(getTimeString(stringDate || ''));
        }, 1000);

        return () => clearInterval(intervalId); // Cleanup interval on component unmount
    }, []);

    const openFilePicker = useCallback(() => {
        if (fileInputRef.current) {
            fileInputRef.current.click();
        }
    }, [fileInputRef]);
    const readFile = useCallback((file: File) => {
        try {
            const reader = new FileReader();
            reader.onload = (event) => {
                const text = event.target?.result as string;
                const lines = text
                    .split("\n")
                    .map((line) => line.trim())
                    .filter((line) => line.length > 0);

                const parsedShipments = lines.map((line) => {
                    const [origin, id, dateShipment, timeShipment, info] = line.split("-");
                    const [destination, amountPackages] = info?.split(":");

                    const formattedId = origin + "-" + id;

                    const year = parseInt(dateShipment.substring(0, 4));
                    const month = parseInt(dateShipment.substring(4, 6)) - 1; // Months are 0-based in JS Date
                    const day = parseInt(dateShipment.substring(6, 8));
                    const [hours, minutes] = timeShipment.split(":").map(Number);

                    const dateTimeShipment = new Date(year, month, day, hours, minutes);

                    return {
                        id: formattedId,
                        origin,
                        dateTimeShipment,
                        destination,
                        amountPackages: parseInt(amountPackages),
                    };
                });
                setShipments(parsedShipments);
            };

            reader.readAsText(file);
        } catch (error) {
            toast.error("Error al leer el archivo", {
                position: "bottom-right",
                duration: 3000,
            });
        }
    }, []);
    const handleFileChange = useCallback(
        (event: React.ChangeEvent<HTMLInputElement>) => {
            const file = event.target.files?.[0];
            if (file) {
                setFile(file);
                readFile(file);
            } else {
                setFile(undefined);
                setShipments([]);
            }
        },
        [setFile, readFile]
    );
    const handleSubmit = useCallback(async () => {
        try {
            setLoading(true);
            const formatDate = (date: Date): string => {
                const year = date.getFullYear();
                const month = (date.getMonth() + 1).toString().padStart(2, "0");
                const day = date.getDate().toString().padStart(2, "0");
                return `${year}${month}${day}`;
            };

            const formatTime = (time: string): string => {
                const [hours, minutes] = time.split(":").map(Number);
                return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
            };

            const formattedShipments = shipments.map((shipment) => {
                let dateString;
                let timeString;

                dateString = formatDate(shipment.dateTimeShipment);
                timeString = formatTime(shipment.dateTimeShipment.toTimeString().split(" ")[0]);

                return `${shipment.origin}-${shipment.id.split("-")[1]}-${dateString}-${timeString}-${shipment.destination}:${shipment.amountPackages.toString().padStart(2, "0")}`;
            });

            let urlApi;
            if (useCustomDate) {
                urlApi = `${process.env.NEXT_PUBLIC_API}/back/envio/codigoAll/horaSistema`;
            } else {
                urlApi = `${process.env.NEXT_PUBLIC_API}/back/envio/codigoAll`;
            }

            await api(
                "POST",
                urlApi,
                (response) => {
                    toast.success("Envíos registrados correctamente", {
                        position: "bottom-right",
                        duration: 3000,
                    });
                    setFile(undefined);
                    setShipments([]);
                    if (fileInputRef.current) {
                        fileInputRef.current.value = "";
                    }
                },
                (error) => {
                    toast.error(`Error al registrar los envíos: ${error}`, {
                        position: "bottom-right",
                        duration: 3000,
                    });
                },
                formattedShipments
            );
        } catch {
            toast.error("Error al registrar los envíos", {
                position: "bottom-right",
                duration: 3000,
            });
        } finally {
            setLoading(false);
        }
    }, [shipments, useCustomDate, selectedDate, selectedTime]);

    return (
        <main className='w-3/5 mx-auto my-10 flex flex-col justify-start h-full'>
            <section className='space-y-4 mb-10'>
                <H1>Archivo de envíos</H1>
                <Large>
                    <strong>Formato de líneas:</strong> CORIEnvio-FORI-HORI-CDES:QQ
                </Large>
                <span className='grid grid-cols-2 gap-2 w-4/6'>
                    <Muted>CORI = Ciudad origen </Muted>
                    <Muted>CDES = Ciudad destino</Muted>
                    <Muted>Envio = Número de envío</Muted>
                    <Muted>FORI = Fecha de envío</Muted>
                    <Muted>HORI = Hora de envío</Muted>
                    <Muted>QQ = Cantidad de paquetes</Muted>
                    <Muted className='col-span-2'>
                        <strong>Ejemplo:</strong> SKBO-000000001-20240103-01:02-SPIM:15
                    </Muted>
                </span>
            </section>
            <section className='flex flex-col items-start justify-start gap-4 mb-10'>
                <div className='flex flex-row items-center gap-2'>
                    <Checkbox
                        id='check'
                        checked={useCustomDate}
                        onCheckedChange={() => setUseCustomDate(!useCustomDate)}
                    /> 
                    <Label htmlFor='check'>Usar fecha actual del sistema para todos los envíos</Label>
                    
                    {/* <Small>Se usa la fecha actual del sistema para todos los envios</Small> */}
                </div>
                <div className={cn("flex flex-row items-center gap-2", useCustomDate ? "visible" : "hidden")}>
                    <DatePicker
                        className='flex-1'
                        date={selectedDate}
                        setDate={setSelectedDate}
                        placeholder='Selecciona una fecha'
                        disabled={true}
                    />
                    <Input
                        type='time'
                        className='w-[95px]'
                        defaultValue={selectedTime}
                        value={selectedTime}
                        onChange={(e) => setSelectedTime(e.target.value)}
                        disabled={true}
                    />
                </div>
            </section>
            <section className='flex flex-row items-center justify-between gap-4 mb-4'>
                <Input
                    placeholder='No ha seleccionado ningun archivo'
                    readOnly
                    value={file !== undefined ? file!.name : "No ha seleccionado ningun archivo"}
                    className='w-full'
                />
                <Input
                    id='username'
                    placeholder='No ha seleccionado ningun archivo'
                    readOnly
                    ref={fileInputRef}
                    type='file'
                    onChange={handleFileChange}
                    className='hidden'
                />
                <Button
                    size={"icon"}
                    className='shrink-0'
                    onClick={openFilePicker}
                >
                    <FileUp className='w-5 h-5 shrink-0' />
                </Button>
            </section>
            <section className='flex flex-col items-center justify-between gap-4'>
                <ShipmentTable
                    data={shipments}
                    useCustomDate={useCustomDate}
                />
                {file !== undefined && (
                    <Lead className='mx-auto'>
                        Se han detectado <strong>{shipments.length}</strong> envíos y{" "}
                        <strong>{shipments.reduce((acc, shipment) => acc + shipment.amountPackages, 0)}</strong> paquetes
                    </Lead>
                )}
                <div className='mx-auto flex flex-row gap-4 pb-10'>
                    <Link
                        href='/dashboard'
                        className={cn(buttonVariants({ variant: "outline" }))}
                    >
                        Cancelar
                    </Link>
                    <Button
                        variant='default'
                        disabled={isDisabled}
                        onClick={() => {
                            handleSubmit();
                        }}
                        isLoading={loading}
                    >
                        Registrar
                    </Button>
                </div>
            </section>
        </main>
    );
}
