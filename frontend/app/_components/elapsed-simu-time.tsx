function ElapsedSimuTime({ children }: { children: React.ReactNode }) {
	return (
		<div className="border rounded-3xl border-yellow-700 text-yellow-700 bg-yellow-100/70 py-1 proportional-nums w-[287px] text-start shadow-md px-3 flex flex-row gap-1 items-center">
			<a className="font-medium">Simulation elapsed time:</a> <p>{children}</p>
		</div>
	);
}
export default ElapsedSimuTime;
