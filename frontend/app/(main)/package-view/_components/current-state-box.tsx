import Chip from "@/components/ui/chip";
import { PackageStatusName, PackageStatusVariant } from "@/lib/types";
import { cn } from "@/lib/utils";

function CurrentStateBox({
	className,
    state,
}: {
	className?: string;
    state: { color: PackageStatusVariant; text: PackageStatusName };
}) {
    return (
        <div className={cn("flex flex-col border rounded-lg", className)}>
            <div className="flex flex-row divide-x-2">
                <p className="font-poppins font-medium w-1/2 text-center p-2">Estado</p>
                <div className="w-1/2 flex justify-center items-center">
                    <Chip color={state.color}>{state.text}</Chip>
                </div>
            </div>
            <p className="font-poppins border-t p-5 text-center font-medium text-lg">
                Viajando de Lima a Barcelona
            </p>
        </div>
    );
}
export default CurrentStateBox;
