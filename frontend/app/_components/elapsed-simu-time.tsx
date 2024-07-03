function ElapsedSimuTime({ children }: { children: React.ReactNode }) {
	return (
		<div className="border rounded-3xl border-yellow-700 text-yellow-700 bg-yellow-100/70 py-1 text-start shadow-md w-[337px] pl-3">
			<a className="font-medium text-start w-full">
				Tiempo simulado transcurrido: <span className="font-normal">{children}</span>
			</a>
		</div>
	);
}
export default ElapsedSimuTime;
