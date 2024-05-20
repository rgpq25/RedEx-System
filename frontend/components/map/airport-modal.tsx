"use client";
import React, { useState } from "react";
import { AirportTable } from "./airport-table";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog";
import { Aeropuerto } from "@/lib/types";
import Chip from "../ui/chip";

interface AirportModalProps {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	aeropuerto: Aeropuerto | undefined;
}

function AirportModal({ isOpen, setIsOpen, aeropuerto }: AirportModalProps) {
	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[900px] min-w-[900px]">
				<DialogHeader>
					<DialogTitle className="text-2xl">Información de aeropuerto</DialogTitle>
				</DialogHeader>
				<div className="text-black">
					<div>
						<p>Ubicación: Lima, Perú</p>
					</div>
					<div className="flex flex-row items-center gap-1">
						
							<p>Capacidad actual: 36/100 </p>
							<Chip color="green" >Bueno</Chip>
						
					</div>
				</div>
				<AirportTable />
			</DialogContent>
		</Dialog>
	);
}

export default AirportModal;
