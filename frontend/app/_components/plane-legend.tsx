import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { Info, Plane, X } from "lucide-react";
import { useState } from "react";

function PlaneLegend({ className }: { className?: string }) {
	const [isVisible, setIsVisible] = useState(true);

	return (
		<>
			{isVisible === false ? (
				<Button className={cn("",className)} size="icon" onClick={()=>setIsVisible(true)}>
					<Info className="shrink-0 w-5 h-5"/>
				</Button>
			) : (
				<div className={cn("flex items-center gap-3 relative mr-1", className)}>
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

					<div className="border bg-mainRed rounded-full h-5 w-5 absolute -top-2 -right-2 flex justify-center items-center cursor-pointer" onClick={()=>setIsVisible(false)}>
						<X className="w-3 h-3 shrink-0 stroke-white"/>
					</div>
				</div>
			)}
		</>
	);
}
export default PlaneLegend;
