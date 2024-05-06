"use client";
import React, { useState } from "react";
import * as AlertDialog from "@/components/ui/alert-dialog";
import { AirportTable } from "./airport-table";

interface AirportModalProps {
	isOpen: boolean;
	setIsOpen: (isOpen: boolean) => void;
}

function AirportModal({ isOpen, setIsOpen }: AirportModalProps) {
	return (
		<AlertDialog.AlertDialog open={isOpen} onOpenChange={setIsOpen}>
			<AlertDialog.AlertDialogTrigger asChild>
				<button onClick={() => setIsOpen(true)}>Abrir información de almacén</button>
			</AlertDialog.AlertDialogTrigger>
			<AlertDialog.AlertDialogContent className="fixed p-5 bg-white rounded-lg shadow-xl top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
				<h2 className="text-3xl font-semibold mb-4">Información de almacén</h2>
				<div className="text-lg mb-4">
					<div>
						Ubicación: Lima, Perú <span className="text-red-500">■</span>
						<span className="text-red-500">■</span>
					</div>
					<div>
						Capacidad actual: 36/100 <span className="text-green-500">Bueno</span>
					</div>
				</div>
				<AirportTable />
				<AlertDialog.AlertDialogFooter>
					<button onClick={() => setIsOpen(false)}>Cerrar</button>
				</AlertDialog.AlertDialogFooter>
			</AlertDialog.AlertDialogContent>
		</AlertDialog.AlertDialog>
	);
}

export default AirportModal;
