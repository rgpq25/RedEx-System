import { Clock } from "lucide-react";

function CurrentTime({ currentTime }: { currentTime: Date | undefined }) {
	if (currentTime === undefined) {
		return null;
	}

	return (
		<div className="flex flex-row gap-1 border rounded-3xl border-blue-700 min-w-[200px] text-center text-blue-700 stroke-blue-700 p-1 shadow-md bg-blue-100/70 pr-4">
			<Clock />
			<p className="text-center flex-1">{currentTime.toLocaleDateString() + " " + currentTime.toLocaleTimeString()}</p>
		</div>
	);
}
export default CurrentTime;
