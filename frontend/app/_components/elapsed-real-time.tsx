function ElapsedRealTime({ children }: { children: React.ReactNode }) {
	return (
		<div className="border rounded-3xl border-red-700 text-red-700 bg-red-100/70 py-1  text-start shadow-md w-[295px] pl-3">
			<a className="font-medium text-start w-full">
				Tiempo real transcurrido: <span className="font-normal">{children}</span>
			</a>
		</div>
	);
}
export default ElapsedRealTime;
