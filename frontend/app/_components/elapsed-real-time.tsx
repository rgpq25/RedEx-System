function ElapsedRealTime({ children }: { children: React.ReactNode }) {
	return (
		<div className="border rounded-3xl border-red-700 text-red-700 bg-red-100/70 py-1 proportional-nums w-fit text-start shadow-md px-3 flex flex-row gap-1 items-center justify-end">
			<a className="font-medium">Tiempo real transcurrido:</a> <p>{children}</p>
		</div>
	);
}
export default ElapsedRealTime;
