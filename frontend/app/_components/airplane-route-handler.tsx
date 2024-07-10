import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { cn } from "@/lib/utils";
import { Eye, EyeOff, PlaneLanding } from "lucide-react";

function AirplaneRouteHandler({
	className,
	isAllRoutesVisible,
	setIsAllRoutesVisible,
}: {
	className?: string;
	isAllRoutesVisible: boolean;
	setIsAllRoutesVisible: (value: boolean) => void;
}) {
	return (
		<div className={cn(className, "border border-mainRed rounded-xl bg-red-400/40 flex flex-row gap-2 py-2 px-4")}>
			{/* {isAllRoutesVisible === true ? <EyeOff className="w-5 h-5 shrink-0" /> : <Eye className="w-5 h-5 shrink-0" />} */}
			<p className="font-medium text-red-800">Ver rutas</p>
			<Switch
				checked={isAllRoutesVisible}
				onCheckedChange={setIsAllRoutesVisible}
			/>
		</div>
	);
}
export default AirplaneRouteHandler;
