import { useEffect, useState } from "react";
import { formatDateTimeLongShortWithSeconds } from "@/lib/date";
import { Large } from "@/components/ui/typography";

function DatesLegend({ currentTime }: { currentTime: Date }) {
    const [realDate, setRealDate] = useState<Date>(new Date());

    useEffect(() => {
        const interval = setInterval(() => {
            setRealDate(new Date());
        }, 1000);

        return () => clearInterval(interval);
    }, []);

    return (
        <>
            <div className='flex flex-col items-center border rounded-3xl border-blue-700 text-blue-7 stroke-blue-700 bg-blue-100/70 py-1 text-start shadow-md w-fit px-4'>
                <Large>Fechas</Large>
                <a className='font-medium text-end w-full'>
                    Real: {" "}
                    <span className='font-normal'>{realDate ? formatDateTimeLongShortWithSeconds(realDate) : "-"}</span>
                </a>
                <a className='font-medium text-end w-full'>
                    Simulada:{" "}
                    <span className='font-normal'>{currentTime ? formatDateTimeLongShortWithSeconds(currentTime) : "-"}</span>
                </a>
            </div>
        </>
    );
}

export default DatesLegend;
