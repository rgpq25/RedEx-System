import { Clock } from "lucide-react";

function CurrentTime({ currentTime }: { currentTime: Date }) {
	return (
		<div className="flex flex-row gap-1 border rounded-3xl border-blue-700 w-[200px] text-blue-700 stroke-blue-700 p-1">
			<Clock />
			<p>{currentTime.toLocaleDateString() + " " + currentTime.toLocaleTimeString()}</p>
		</div>
	);
}
export default CurrentTime;
