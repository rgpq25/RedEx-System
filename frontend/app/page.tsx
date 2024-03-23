import { Button } from "@/components/ui/button";
import Image from "next/image";
import Navbar from "./_components/navbar";

export default function Home() {
    return (
        <main className="flex flex-row h-screen justify-center items-center gap-18">
			<Navbar/>
            <section className="flex flex-col text-left">
                <p className="text-8xl font-semibold">
                    Red<span className="text-mainRed">Ex</span>
                </p>
                <p className="text-xl font-light">
                    Mas alla de las fronteras, mas cerca de ti
                </p>
                <div>
                    <Button className="mt-3 text-xl py-6" size="lg">
                        Empieza ya{" "}
                    </Button>
                </div>
            </section>
            <img src="/mainLogo-light.png" className="h-[400px] object-cover" />
        </main>
    );
}
