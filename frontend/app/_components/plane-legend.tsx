import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { Info, Plane } from "lucide-react";
import { useState } from "react";

function PlaneLegend({ className }: { className?: string }) {
	const [isVisible, setIsVisible] = useState(true);

	return (
		<>
			{isVisible === true ? (
				<Button className={cn("",className)} size="icon" onClick={()=>setIsVisible(false)}>
					<Info className="shrink-0 w-5 h-5"/>
				</Button>
			) : (
				<div className={cn("flex items-center gap-3 border", className)}>
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
			)}
		</>
	);
}
export default PlaneLegend;
