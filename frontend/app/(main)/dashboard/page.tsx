"use client";

import { cn } from "@/lib/utils";
import { useState } from "react";
import Link from "next/link";

import { CopyCheck, FileUp, PackagePlus, Plane, BarChart} from "lucide-react";
import { Button, buttonVariants } from "@/components/ui/button";

import {
	Dialog,
	DialogClose,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
	DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DialogReception } from "./_components/dialogReception";
import MainContainer from "../_components/main-container";

const twStyle = "w-5 h-5";

function ManagePackagesPage() {
	return (
		<>
			<MainContainer>
				<section className="m-auto flex flex-row justify-center gap-[150px]">
					<img src="/dashboard-main.svg" className="w-[45%]" />
					<div className="flex flex-col items-start justify-center">
						<h1 className="text-4xl font-semibold font-poppins">Bienvenido, operario</h1>
						<div className="grid grid-cols-2 gap-[5px] w-[410px] mt-[6px]">
							<Link className={cn(buttonVariants(), "col-span-1")} href={"/dashboard/register-shipment"}>
								<PackagePlus className={twStyle} />
								<p>Registrar envio</p>
							</Link>
							<Link className={cn(buttonVariants(), "col-span-1")} href={"/dashboard/file-shipment"}>
								<FileUp className={twStyle} />
								<p>Carga de envíos</p>
							</Link>
							<DialogReception twStyle={twStyle} />
							<Link className={cn(buttonVariants(), "col-span-2")} href={"/dashboard/daily-operations"}>
								<Plane className={twStyle} />
								<p>Ver operaciones día a día</p>
							</Link>
							{/* <Link className={cn(buttonVariants(), "col-span-2")} href={"/dashboard/statistics"}>
								<BarChart className={twStyle} />
								<p>Estadísticas de envíos</p>
							</Link> */}
						</div>
					</div>
				</section>
			</MainContainer>
		</>
	);
}

export default ManagePackagesPage;
