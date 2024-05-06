import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";
import CurrentStateBox from "./current-state-box";
import { PackageRouteTable } from "./package-route-table";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { useState } from "react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { ChevronsLeft, ChevronsRight, X } from "lucide-react";

function CardInfo({ shipment }: { shipment: any }) {
	const [visible, setVisible] = useState<boolean>(true);

	return (
		<>
			<Button
				onClick={() => setVisible(true)}
				className={cn(
					"absolute top-8 left-8 gap-1 transition-opacity duration-500 ease-in-out delay-200 z-[100]",
					visible ? "opacity-0" : "opacity-100"
				)}
			>
				<p>Ver información</p>
                <ChevronsRight/>
			</Button>
			<Card
				className={cn(
					"w-[450px] flex flex-col absolute top-5 bottom-5 left-5 shadow-xl z-[100]",
					"transition-all duration-300 ease-in-out",
					visible ? "left-5" : "-left-[470px]"
				)}
			>
				<CardHeader className="font-poppins relative">
					<CardTitle>Envío</CardTitle>
					<CardTitle className="text-5xl leading-[40px]">
						{shipment.id}
					</CardTitle>
					<CardTitle className="text-2xl leading-[20px]">{`${shipment.origin} -> ${shipment.destination}`}</CardTitle>
					<div className="flex gap-3 w-full items-center">
						<p className="">Viendo</p>
						<Select>
							<SelectTrigger className="w-full h-[38px] z-[120]">
								<SelectValue placeholder="Paquete" className="z-[120]"/>
							</SelectTrigger>
							<SelectContent className="z-[120]">
								<SelectItem value="light">Paquete 1</SelectItem>
								<SelectItem value="dark">Paquete 2</SelectItem>
								<SelectItem value="system">
									Paquete 3
								</SelectItem>
								<SelectItem value="all">
									Todos
								</SelectItem>
							</SelectContent>
						</Select>
					</div>
					<ChevronsLeft
                        className="absolute top-2 right-3 cursor-pointer w-10 h-10 p-2"
						onClick={() => {
							console.log("Clckng");
							setVisible(false);
						}}
					>
						Cerrar
					</ChevronsLeft>
				</CardHeader>
				<CardContent className="flex-1 flex flex-col">
					<CurrentStateBox className="" state={shipment.status} />

					<div className="mt-3 text-center flex-1 flex flex-col justify-center items-center">
						<p className="text-lg font-poppins tracking-tight">
							Tiempo de llegada a{" "}
							<span className="font-semibold text-green-600">
								destino final:
							</span>
						</p>
						<h2 className="text-3xl tracking-wide font-poppins">
							27 de marzo - 14:40
						</h2>
					</div>

					<h2 className="text-4xl text-center mt-4">
						Ruta de paquete
					</h2>

					<PackageRouteTable />
				</CardContent>
			</Card>
		</>
	);
}
export default CardInfo;
