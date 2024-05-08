"use client";

import { cn } from "@/lib/utils";
import { useState } from "react";
import Link from "next/link";

import { CopyCheck, FileUp, PackagePlus, Plane } from "lucide-react";
import { Button, buttonVariants } from "@/components/ui/button";

import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogReception } from "./_components/dialogReception";

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
                            <DialogReception twStyle={twStyle} />
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

export default ManagePackagesPage;
