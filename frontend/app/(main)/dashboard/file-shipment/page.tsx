"use client";

import { useRef, useState, useCallback } from "react";
import { cn } from "@/lib/utils";
import Link from "next/link";

import { Button, buttonVariants } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { toast } from "sonner";
import { H1, Large, Lead, Muted } from "@/components/ui/typography";
import { FileUp } from "lucide-react";

import { RowShipmentType } from "@/lib/types";
import { ShipmentTable } from "../_components/shipment-table";

export default function FileShipment() {
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [file, setFile] = useState<File | undefined>(undefined);
    const [shipments, setShipments] = useState<RowShipmentType[]>([]);
    const [loading, setLoading] = useState(false);

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
            }
        },
        [setFile, readFile]
    );

    const handleSubmit = useCallback(async () => {
        try {
            setLoading(true);
            await new Promise((resolve) => setTimeout(resolve, 5000)); // Simulate API call
            toast.success("Envíos registrados correctamente", {
                position: "bottom-right",
                duration: 3000,
            });
            setFile(undefined);
            setShipments([]);
            if (fileInputRef.current) {
                fileInputRef.current.value = "";
            }
        } catch {
            toast.error("Error al registrar los envíos", {
                position: "bottom-right",
                duration: 3000,
            });
        } finally {
            setLoading(false);
        }
    }, []);

    return (
        <main className='w-3/5 mx-auto my-10 flex flex-col justify-start gap-4 h-full'>
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

            <section className='flex flex-row items-center justify-between gap-4'>
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
            <ShipmentTable data={shipments} />
            {file !== undefined && (
                <Lead className='mx-auto'>
                    Se han detectado <strong>{shipments.length}</strong> envios y{" "}
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
                    disabled={shipments.length === 0 || loading}
                    onClick={() => {
                        handleSubmit();
                    }}
                    isLoading={loading}
                >
                    Registrar
                </Button>
            </div>
        </main>
    );
}
