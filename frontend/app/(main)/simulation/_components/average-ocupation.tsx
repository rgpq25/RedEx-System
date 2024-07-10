import { Large } from "@/components/ui/typography";
import { getPorcentajeOcupacionAeropuertos, getPorcentajeOcupacionVuelos } from "@/lib/map-utils";
import { Aeropuerto, EstadoAlmacen, Vuelo } from "@/lib/types";
import { cn } from "@/lib/utils";
import { Plane, Warehouse } from "lucide-react";
import { useMemo } from "react";

//absolute top-24 right-14 z-[20]

interface AverageOcupationProps {
    airportsHash: Map<string, Aeropuerto> | null;
    estadoAlmacen: EstadoAlmacen | null;
    filtered_vuelos: Vuelo[];
    currentTime: Date | undefined;
    className: string;
}

function AverageOcupation({ airportsHash, estadoAlmacen, filtered_vuelos, currentTime, className }: AverageOcupationProps) {
    const averageAirportOcupation = useMemo(() => {
        return getPorcentajeOcupacionAeropuertos(airportsHash, estadoAlmacen, currentTime);
    }, [airportsHash, estadoAlmacen, currentTime]);

    const averageFlightOcupation = useMemo(() => {
        return getPorcentajeOcupacionVuelos(filtered_vuelos, currentTime);
    }, [filtered_vuelos, currentTime]);

    return (
        <div className={cn("flex flex-col items-end justify-center gap-1 ", className)}>
            <div className='border rounded-xl border-purple-700 text-purple-700 bg-purple-200/70 py-1 proportional-nums w-fit text-start shadow-md px-3 flex flex-row gap-4 items-center justify-end' >
                <Large className='font-medium text-right w-full'>Ocupación <br/> promedio</Large>{" "}
                <div className='flex flex-col'>
                    <div className='flex flex-row items-center gap-1 justify-end w-full'>
                        <p className='proportional-nums text-lg'>{`${averageAirportOcupation}%`}</p>
                        <Warehouse className='stroke-[1.1px] w-5 h-5' />
                    </div>
                    <div className='flex flex-row items-center gap-1 justify-end w-full'>
                        <p className='proportional-nums text-lg'>{`${averageFlightOcupation}%`}</p>
                        <Plane className='stroke-[1.2px] w-5 h-5' />
                    </div>
                </div>
            </div>
        </div>
    );
}
export default AverageOcupation;
