import { cn } from "@/lib/utils";
import { Plane } from "lucide-react";

function PlaneLegend({className}: {className?: string}) {
	return (
		<div className={cn("flex items-center gap-3", className)}>
			<div className="flex flex-col justify-center items-center">
				<Plane className="fill-green-500" />
				<p className="text-xs">0%-30%</p>
			</div>
			<div className="flex flex-col justify-center items-center">
				<Plane className="fill-yellow-500" />
				<p className="text-xs">31%-60%</p>
			</div>
			<div className="flex flex-col justify-center items-center">
				<Plane className="fill-red-500" />
				<p className="text-xs">61%-100%</p>
			</div>
		</div>
	);
}
export default PlaneLegend;
