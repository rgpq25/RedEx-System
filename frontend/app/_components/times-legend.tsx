import { Large } from "@/components/ui/typography";

function TimesLegend({ elapsedRealTime, elapsedSimuTime }: { elapsedRealTime: string; elapsedSimuTime: string }) {
    return (
        <>
            <div className="flex flex-col items-center border rounded-3xl border-yellow-700 text-yellow-700 bg-yellow-100/70 py-1 text-start shadow-md w-fit px-4">
                <Large>Tiempo transcurrido</Large>
                <a className='font-medium text-end w-full'>
                    Real: <span className='font-normal'>{elapsedRealTime}</span>
                </a>
                <a className='font-medium text-end w-full'>
                    Simulado: <span className='font-normal'>{elapsedSimuTime}</span>
                </a>
            </div>
        </>
    );
}

export default TimesLegend;
