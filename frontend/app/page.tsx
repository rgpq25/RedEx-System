import { Button, buttonVariants } from "@/components/ui/button";
import Image from "next/image";
import Navbar from "./_components/navbar";
import MainLogo from "@/public/main-logo";
import { cn } from "@/lib/utils";
import Link from "next/link";

export default function Home() {
    return (
        <main className="overflow-x-hidden">
            <Navbar hasNavigation hasLoginButton isFixed/>
            <div className="flex flex-row w-dvw h-dvh justify-center items-center gap-18">
                <section className="flex flex-col text-left">
                    <p className="text-8xl font-bold font-poppins">
                        Red<span className="text-mainRed">Ex</span>
                    </p>
                    <p className="text-xl font-light">
                        Mas alla de las fronteras, mas cerca de ti
                    </p>
                    <div>
                        <Link
                            className={cn(
                                buttonVariants({
                                    variant: "default",
                                    size: "lg",
                                }),
                                "mt-3 text-xl py-6"
                            )}
                            href={"/modal-vuelo"}
                        >
                            Empieza ya{" "}
                        </Link>
                    </div>
                </section>
                <MainLogo className="h-[310px] w-[310px]" />
            </div>
            <div className="flex flex-row w-screen h-screen justify-center items-center gap-18">
                <p>nosotros</p>
            </div>
            <div className="flex flex-row w-screen h-screen justify-center items-center gap-18">
                <p>servicios</p>
            </div>
            <div className="flex flex-row w-screen h-screen justify-center items-center gap-18">
                <p>preguntas</p>
            </div>
        </main>
    );
}
