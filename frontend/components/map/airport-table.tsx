"use client";

import * as React from "react";
import {
	ColumnDef,
	ColumnFiltersState,
	SortingState,
	VisibilityState,
	flexRender,
	getCoreRowModel,
	getFilteredRowModel,
	getPaginationRowModel,
	getSortedRowModel,
	useReactTable,
} from "@tanstack/react-table";
import { ArrowUpDown, ChevronDown, MoreHorizontal } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
	DropdownMenu,
	DropdownMenuCheckboxItem,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuLabel,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Envio, Paquete, RowAlmacenType } from "@/lib/types";
import Chip from "@/components/ui/chip";

const data: Paquete[] = [
	// {
	// 	id: "11M289",
	// 	packets: 1,
	// 	origin: "Beijing, China",
	// 	destination: "Lima - Peru",
	// 	statusAlmacen: "Entregado",
	// 	statusVariant: "green",
	// },
	// {
	// 	id: "12N391",
	// 	packets: 2,
	// 	origin: "Roma, Italia",
	// 	destination: "Lima, Peru",
	// 	statusAlmacen: "En almacen destino",
	// 	statusVariant: "purple",
	// },
	// {
	// 	id: "13O402",
	// 	packets: 3,
	// 	origin: "Cairo, Egipto",
	// 	destination: "Puno - Peru",
	// 	statusAlmacen: "En espera",
	// 	statusVariant: "yellow",
	// },
	// {
	// 	id: "14P513",
	// 	packets: 1,
	// 	origin: "Mumbai, India",
	// 	destination: "Tokio, Japon",
	// 	statusAlmacen: "En espera",
	// 	statusVariant: "yellow",
	// },
	// {
	// 	id: "16R735",
	// 	packets: 1,
	// 	origin: "Lima, Peru",
	// 	destination: "Frankfurt, Alemania",
	// 	statusAlmacen: "En almacen origen",
	// 	statusVariant: "gray",
	// },
	{
		id: 1,
		aeropuertoActual: {
			id: 23,
			ubicacion: {
				id: "EKCH",
				continente: "Europa",
				pais: "Dinamarca",
				ciudad: "Copenhague",
				ciudadAbreviada: "cope",
				zonaHoraria: "GMT+2",
				latitud: 55.6761,
				longitud: 12.5683,
			},
			capacidadMaxima: 480,
		},
		enAeropuerto: true,
		entregado: false,
		fechaDeEntrega: null,
		envio: {
			id: 1,
			ubicacionOrigen: {
				id: "EKCH",
				continente: "Europa",
				pais: "Dinamarca",
				ciudad: "Copenhague",
				ciudadAbreviada: "cope",
				zonaHoraria: "GMT+2",
				latitud: 55.6761,
				longitud: 12.5683,
			},
			ubicacionDestino: {
				id: "UMMS",
				continente: "Europa",
				pais: "Bielorrusia",
				ciudad: "Minsk",
				ciudadAbreviada: "mins",
				zonaHoraria: "GMT+3",
				latitud: 53.9006,
				longitud: 27.559,
			},
			fechaRecepcion: new Date("2024-01-03T23:49:39.731+00:00"),
			fechaLimiteEntrega: new Date("2024-01-04T22:49:39.731+00:00"),
			estado: "En proceso",
			cantidadPaquetes: 1,
			codigoSeguridad: "123456",
		},
		simulacionActual: {
			fechaInicioSim: new Date("2024-05-23T14:50:42.073+00:00"),
			fechaFinSim: new Date("2024-05-23T14:50:42.073+00:00"),
			fechaInicioSistema: new Date("2024-05-23T14:50:42.073+00:00"),
			multiplicadorTiempo: 1.0,
			estado: 0,
			id: 1,
		},
		planRutaActual: null,
		fechaRecepcion: new Date("2024-01-03T23:49:39.731+00:00"),
	},
];

export const columns: ColumnDef<Paquete>[] = [
	{
		accessorKey: "numero",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[40px]">
					<p className="text-center w-full">Nro.</p>
				</div>
			);
		},
		cell: ({ row }) => <div className="text-muted-foreground text-center w-[40px]">{row.index + 1}</div>,
	},
	{
		accessorKey: "envio",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[150px] gap-1">
					<p>Envío asociado</p>
					<Button
						variant="ghost"
						onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
						size={"icon"}
					>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => <div className="w-[150px] text-start">{(row.getValue("envio") as Envio).id}</div>,
	},
	{
		accessorKey: "Origen",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[150px]">
					<p>Origen</p>
					<Button
						variant="ghost"
						onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
						size={"icon"}
					>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => <div className="w-[150px]">{(row.getValue("envio") as Envio).ubicacionOrigen.ciudad + ", " + (row.getValue("envio") as Envio).ubicacionOrigen.pais}</div>,
	},
	{
		accessorKey: "Destino",
		header: ({ column }) => {
			return (
				<div className="flex items-center w-[150px]">
					<p>Destino</p>
					<Button
						variant="ghost"
						onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
						size={"icon"}
					>
						<ArrowUpDown className="h-4 w-4" />
					</Button>
				</div>
			);
		},
		cell: ({ row }) => <div className="w-[150px]">{(row.getValue("envio") as Envio).ubicacionDestino.ciudad + ", " + (row.getValue("envio") as Envio).ubicacionDestino.pais}</div>,
	},
	{
		accessorKey: "statusAlmacen",
		header: ({ column }) => {
			return (
				<div className="flex items-center">
					<p>Estado</p>
				</div>
			);
		},
		cell: ({ row }) => (
			<div className="">
                <p>Pendiente</p>
				{/* <Chip color={row.original.statusVariant}>{row.getValue("statusAlmacen")}</Chip> */}
			</div>
		),
	},
];

export function AirportTable({ paquetes }: { paquetes: Paquete[] }) {
	const [sorting, setSorting] = React.useState<SortingState>([]);
	const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>([]);
	const [columnVisibility, setColumnVisibility] = React.useState<VisibilityState>({});
	const [rowSelection, setRowSelection] = React.useState({});

	const table = useReactTable({
		data,
		columns,
		onSortingChange: setSorting,
		onColumnFiltersChange: setColumnFilters,
		getCoreRowModel: getCoreRowModel(),
		getPaginationRowModel: getPaginationRowModel(),
		getSortedRowModel: getSortedRowModel(),
		getFilteredRowModel: getFilteredRowModel(),
		onColumnVisibilityChange: setColumnVisibility,
		onRowSelectionChange: setRowSelection,
		state: {
			sorting,
			columnFilters,
			columnVisibility,
			rowSelection,
		},
	});

	return (
		<div className="w-full">
			<div className="flex items-center">
				<Input
					placeholder="Filtrar envíos..."
					// value={(table.getColumn("email")?.getFilterValue() as string) ?? ""}
					// onChange={(event) => table.getColumn("email")?.setFilterValue(event.target.value)}
					className="max-w-sm"
				/>
				<DropdownMenu>
					<DropdownMenuTrigger asChild>
						<Button variant="outline" className="ml-auto">
							Columnas <ChevronDown className="ml-2 h-4 w-4" />
						</Button>
					</DropdownMenuTrigger>
					<DropdownMenuContent align="end">
						{table
							.getAllColumns()
							.filter((column) => column.getCanHide())
							.map((column) => {
								return (
									<DropdownMenuCheckboxItem
										key={column.id}
										className="capitalize"
										checked={column.getIsVisible()}
										onCheckedChange={(value) => column.toggleVisibility(!!value)}
									>
										{column.id}
									</DropdownMenuCheckboxItem>
								);
							})}
					</DropdownMenuContent>
				</DropdownMenu>
			</div>
			<div className="rounded-md border mt-3">
				<Table>
					<TableHeader>
						{table.getHeaderGroups().map((headerGroup) => (
							<TableRow key={headerGroup.id}>
								{headerGroup.headers.map((header) => {
									return (
										<TableHead key={header.id} className="bg-mainRed/20 text-black">
											{header.isPlaceholder
												? null
												: flexRender(header.column.columnDef.header, header.getContext())}
										</TableHead>
									);
								})}
							</TableRow>
						))}
					</TableHeader>
					<TableBody>
						{table.getRowModel().rows?.length ? (
							table.getRowModel().rows.map((row) => (
								<TableRow key={row.id} data-state={row.getIsSelected() && "selected"}>
									{row.getVisibleCells().map((cell) => (
										<TableCell key={cell.id}>
											{flexRender(cell.column.columnDef.cell, cell.getContext())}
										</TableCell>
									))}
								</TableRow>
							))
						) : (
							<TableRow>
								<TableCell colSpan={columns.length} className="h-24 text-center">
									No results.
								</TableCell>
							</TableRow>
						)}
					</TableBody>
				</Table>
			</div>
			<div className="flex items-center justify-end space-x-2 pt-4">
				<div className="space-x-2">
					<Button
						variant="outline"
						size="sm"
						onClick={() => table.previousPage()}
						disabled={!table.getCanPreviousPage()}
					>
						Previo
					</Button>
					<Button
						variant="outline"
						size="sm"
						onClick={() => table.nextPage()}
						disabled={!table.getCanNextPage()}
					>
						Siguiente
					</Button>
				</div>
			</div>
		</div>
	);
}
