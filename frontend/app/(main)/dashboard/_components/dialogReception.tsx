"use client";

import { useRef } from "react";
import { useRouter } from "next/navigation";

import useApi from "@/components/hooks/useApi";

import { CopyCheck } from "lucide-react";
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { Envio } from "@/lib/types";

import { api } from "@/lib/api";

function DialogReception({ twStyle }: { twStyle: string }) {
    const router = useRouter();
    const inputRef = useRef<HTMLInputElement>(null);

    const handlePackageIdRedirect = async () => {
        const packageId = inputRef.current?.value;
        await api(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/envio/sin_simulacion`,
            (data: Envio[]) => {
                if(data.length > 0) {
                    if(data.find((envio) => envio.id.toString() === packageId)) {
                        router.push(`/dashboard/reception-package/${packageId}`);
                    }
                } else {
                    toast.error("No se encontró el envío", {
                        position: "bottom-right",
                        duration: 3000,
                    });
                }
            },
            (error) => {
                toast.error("No se encontró el envío", {
                    position: "bottom-right",
                    duration: 3000,
                });
            }
        );
    };

    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button className='col-span-2'>
                    <CopyCheck className={twStyle} />
                    <p>Registrar recepcion de envío</p>
                </Button>
            </DialogTrigger>
            <DialogContent hasCloseButton={false} disableOutsideEventsToClose>
                <DialogHeader>
                    <DialogTitle>Ingrese el código de monitoreo del envío</DialogTitle>
                </DialogHeader>
                <div className='space-y-1'>
                    <Label htmlFor='tracking-code'>Código de monitoreo</Label>
                    <Input placeholder='Ej. A43HDS5' ref={inputRef} />
                </div>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant='outline'>Cancelar</Button>
                    </DialogClose>
                    <Button onClick={handlePackageIdRedirect}>Ingresar</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}

export { DialogReception };
