function ElapsedRealTime({ children }: { children: React.ReactNode }) {
	return (
		<div className="border rounded-3xl border-red-700 text-red-700 bg-red-100/70 py-1 proportional-nums w-[242px] text-start shadow-md px-3 flex flex-row gap-1 items-center">
			<a className="font-medium">Real elapsed time:</a> <p>{children}</p>
		</div>
	);
}
export default ElapsedRealTime;
