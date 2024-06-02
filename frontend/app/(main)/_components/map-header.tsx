function MapHeader({ children }: { children: React.ReactNode }) {
	return (
		<div className="absolute top-8 left-12 z-[50]">
			{children}
		</div>
	);
}
export default MapHeader;
