"use client";

import { useState, useRef, useContext } from "react";
import Image from "next/image";
import Navbar from "@/app/_components/navbar";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { ShipmentIdContext } from "@/components/hooks/useShipmentId";
import { api } from "@/lib/api";
import { Envio } from "@/lib/types";

function SecurityCodeLoginPage() {
    const router = useRouter();
    const { setShipmentId } = useContext(ShipmentIdContext);
    const [isLoading, setIsLoading] = useState(false);
    const inputRef = useRef<HTMLInputElement>(null);

    const handleShipmentIdRedirect = async () => {
        const packageId = inputRef.current?.value;
        await api(
            "GET",
            `${process.env.NEXT_PUBLIC_API}/back/envio/sin_simulacion`,
            (data: Envio[]) => {
                if (data.length > 0) {
                    if (data.find((envio) => envio.id.toString() === packageId)) {
                        if (packageId) {
                            setShipmentId(packageId);
                            router.push(`/package-view`);
                        } else {
                            throw new Error("No se encontró el envío");
                        }
                    } else {
                        throw new Error("No se encontró el envío");
                    }
                } else {
                    throw new Error("No se encontró el envío");
                }
            },
            (error) => {
                throw new Error("No se encontró el envío");
            }
        );
    };

    const verifiyCode = async () => {
        setIsLoading(true);
        setTimeout(async () => {
            const code = inputRef.current?.value;
            if (!code) {
                setIsLoading(false);
                toast.error("Ingrese un codigo valido", {
                    position: "bottom-right",
                    duration: 3000,
                });
                return;
            }
            if (code === "admin") {
                localStorage.setItem("role", "admin");
                router.push("/simulation");
            } else if (code === "operario") {
                localStorage.setItem("role", "operario");
                router.push("/dashboard");
            } else {
                localStorage.setItem("role", "user");
                try {
                    await handleShipmentIdRedirect();
                } catch (error) {
                    toast.error("No se encontró el envío", {
                        position: "bottom-right",
                        duration: 3000,
                    });
                } finally {
                    setIsLoading(false);
                    return;
                }
            }
        }, 1000);
    };

    return (
        <main className='overflow-x-hidden'>
            <Navbar isFixed />
            <div className='w-dvw h-dvh flex flex-row'>
                <section className='hidden flex-1 place-items-center place-content-center lg:grid'>
                    <Image
                        src={"/cardbox.png"}
                        alt='Cardbox Image'
                        height={500}
                        width={500}
                        className='w-[70%] object-contain'
                    />
                </section>

                <section className='flex-1 flex flex-col justify-center items-center'>
                    <div>
                        <h1 className='font-semibold text-3xl mb-2'>Ingrese su codigo de seguridad</h1>
                        <Label
                            htmlFor='Codigo'
                            className='font-semibold text-base'
                        >
                            Codigo
                        </Label>
                        <Input
                            className='mt-1'
                            placeholder='1A324019'
                            ref={inputRef}
                        />
                        <Button
                            className=' mt-2 w-[120px]'
                            disabled={isLoading}
                            onClick={verifiyCode}
                            isLoading={isLoading}
                        >
                            Ingresar
                        </Button>
                    </div>
                </section>
            </div>
        </main>
    );
}
export default SecurityCodeLoginPage;
