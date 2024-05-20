"use client";
import React, { useState } from "react";
import * as AlertDialog from "@radix-ui/react-alert-dialog";
import { FlightTable } from "./flight-table";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { Vuelo } from "@/lib/types";
import Chip from "../ui/chip";

interface FlightModalProps {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
	vuelo: Vuelo | undefined;
}

function FlightModal({ isOpen, setIsOpen, vuelo }: FlightModalProps) {
	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogContent className="w-[900px] min-w-[900px]">
				<DialogHeader>
					<DialogTitle className="text-2xl">Información de vuelo</DialogTitle>
				</DialogHeader>
				<div>
					<p>Origen - Destino: Lima, Perú - Roma, Italia</p>
					<div className="flex flex-row items-center gap-1">
						<p>Capacidad actual: 230/250</p>
						<Chip color="red">Ocupado</Chip>
					</div>
				</div>
				<FlightTable />
			</DialogContent>
		</Dialog>
	);
}

export default FlightModal;
