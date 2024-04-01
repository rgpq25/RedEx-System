import { Info } from "lucide-react";

function InfoNotation1() {
    return (
        <div className="flex items-start mt-1 ml-[1.8px]">
            <Info className="shrink-0 stroke-[1px] w-5 h-5 mr-1 mt-[1.2px]" />
            <p className="font-light text-sm">
                La simulacion tomara los datos de entrada para simular un
                pronostico de el trafico en el sistema
            </p>
        </div>
    );
}
export default InfoNotation1;
