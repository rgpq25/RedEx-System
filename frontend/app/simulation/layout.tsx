import Navbar from "../_components/navbar";

export default function SimulationLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <div className="overflow-x-hidden h-dvh w-dvw flex flex-col">
            <Navbar/>
            {children}
        </div>
    );
}
