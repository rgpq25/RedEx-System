"use client";

import { useState } from "react";
import Image from "next/image";
import Navbar from "@/app/_components/navbar";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

function SecurityCodeLoginPage() {
    const router = useRouter();
    const [code, setCode] = useState("");

    return (
        <main className="overflow-x-hidden">
            <Navbar isFixed />
            <div className="w-dvw h-dvh flex flex-row">
                <section className="hidden flex-1 place-items-center place-content-center lg:grid">
                    <Image
                        src={"/cardbox.png"}
                        alt="Cardbox Image"
                        height={500}
                        width={500}
                        className="w-[70%] object-contain"
                    />
                </section>

                <section className="flex-1 flex flex-col justify-center items-center">
                    <div>
                        <h1 className="font-semibold text-3xl mb-2">
                            Ingrese su codigo de seguridad
                        </h1>
                        <Label
                            htmlFor="Codigo"
                            className="font-semibold text-base"
                        >
                            Codigo
                        </Label>
                        {/* <div className="flex flex-row w-full items-center gap-2"> */}
                        <Input
                            className="mt-1"
                            placeholder="1A324019"
                            value={code}
                            onChange={(e) => setCode(e.target.value)}
                        />
                        <Button
                            className=" mt-2 w-[120px]"
                            disabled={code.length === 0}
                            onClick={() => {
                                if (code === "admin")
                                    router.push("/simulation");
                                else if (code === "operator")
                                    router.push("/operator");
                                else router.push("/tracking");
                            }}
                        >
                            Ingresar
                        </Button>
                        {/* </div> */}
                    </div>
                </section>
            </div>
        </main>
    );
}
export default SecurityCodeLoginPage;
