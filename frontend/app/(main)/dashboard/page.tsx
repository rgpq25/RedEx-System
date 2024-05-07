import { Button, buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { CopyCheck, FileUp, PackagePlus, Plane } from "lucide-react";
import Link from "next/link";

function ManagePackagesPage() {
	const twStyle = "w-5 h-5";

	return (
		<main className="flex-1 min-h-[800px] flex">
			<section className="m-auto flex flex-row justify-center gap-[150px]">
				<img src="/dashboard-main.svg" className="w-[45%]" />
				<div className="flex flex-col items-start justify-center">
					<h1 className="text-4xl font-semibold font-poppins">Bienvenido, operario</h1>
					<div className="grid grid-cols-2 gap-[5px] w-[410px] mt-[6px]">
						<Button disabled>
							<PackagePlus className={twStyle} />
							<p>Registrar envio</p>
						</Button>
						<Button disabled>
							<FileUp className={twStyle} />
							<p>Carga de envíos</p>
						</Button>
						<Button disabled className="col-span-2">
							<CopyCheck className={twStyle} />
							<p>Registrar recepcion de envío</p>
						</Button>
						<Link className={cn(buttonVariants(), "col-span-2")} href={"/dashboard/daily-operations"}>
							<Plane className={twStyle} />
							<p>Ver operaciones día a día</p>
						</Link>
					</div>
				</div>
			</section>
		</main>
	);
}
export default ManagePackagesPage;
