"use client";

import { cn } from "@/lib/utils";
import { useState } from "react";
import Link from "next/link";

import { CopyCheck, FileUp, PackagePlus, Plane } from "lucide-react";
import { Button, buttonVariants } from "@/components/ui/button";

import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const twStyle = "w-5 h-5";

function ManagePackagesPage() {
    const [isModalOpen, setIsModalOpen] = useState(false);

    return (
        <>
            <main className='flex-1 min-h-[800px] flex'>
                <section className='m-auto flex flex-row justify-center gap-[150px]'>
                    <img src='/dashboard-main.svg' className='w-[45%]' />
                    <div className='flex flex-col items-start justify-center'>
                        <h1 className='text-4xl font-semibold font-poppins'>Bienvenido, operario</h1>
                        <div className='grid grid-cols-2 gap-[5px] w-[410px] mt-[6px]'>
                            <Button disabled>
                                <PackagePlus className={twStyle} />
                                <p>Registrar envio</p>
                            </Button>
                            <Button disabled>
                                <FileUp className={twStyle} />
                                <p>Carga de envíos</p>
                            </Button>
                            <DialogReception />
                            <Link className={cn(buttonVariants(), "col-span-2")} href={"/dashboard/daily-operations"}>
                                <Plane className={twStyle} />
                                <p>Ver operaciones día a día</p>
                            </Link>
                        </div>
                    </div>
                </section>
            </main>
        </>
    );
}

function DialogReception() {
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
                    <Input placeholder='Ej. A43HDS5' />
                </div>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant='outline'>Cancelar</Button>
                    </DialogClose>
                    <Link className={cn(buttonVariants())} href={"/dashboard/reception-package"}>
                        Ingresar
                    </Link>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}

export default ManagePackagesPage;
