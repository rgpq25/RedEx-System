"use client";
import { useEffect, useState } from "react";
import Navbar from "../_components/navbar";
import { useRouter } from "next/navigation";
import { RoleType } from "@/lib/types";
import { Toaster } from "sonner";
import { FilteredFlightsProvider } from "@/components/contexts/flights-filter";

export default function SimulationLayout({ children }: { children: React.ReactNode }) {
    const router = useRouter();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [role, setRole] = useState<RoleType>(null);

    useEffect(() => {
        setIsLoading(true);
        setRole(null);
        const role = localStorage.getItem("role");

        if (!role) {
            router.push("/security-code");
        }

        if (role === "admin") {
            //router.push("/simulation");
            setRole("admin");
            setIsLoading(false);
        }
        if (role === "operario") {
            setRole("operario");
            //router.push("/dashboard");
            setIsLoading(false);
        }
        if (role === "user") {
            setRole("user");
            setIsLoading(false);
        }
    }, []);

    return (
        <>
            <FilteredFlightsProvider>
                <div className='overflow-x-hidden h-dvh w-dvw flex flex-col'>
                    <Navbar role={role} />
                    {isLoading ? <p>Loading...</p> : children}
                </div>
                <Toaster richColors />
            </FilteredFlightsProvider>
        </>
    );
}
