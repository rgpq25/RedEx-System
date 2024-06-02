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
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import Chip from "@/components/ui/chip";
import { RowShipmentType } from "@/lib/types";

export const columns: ColumnDef<RowShipmentType>[] = [
    {
        accessorKey: "id",
        header: "Envio",
        cell: ({ row }) => <div className="">{row.getValue("id")}</div>,
    },
    {
        accessorKey: "origin",
        header: ({ column }) => {
            return (
                <div className="flex items-center">
                    <p>Origen</p>
                    <Button
                        variant="ghost"
                        onClick={() =>
                            column.toggleSorting(column.getIsSorted() === "asc")
                        }
                        size={"icon"}
                    >
                        <ArrowUpDown className="h-4 w-4" />
                    </Button>
                </div>
            );
        },
        cell: ({ row }) => <div className="">{row.getValue("origin")}</div>,
    },
    {
        accessorKey: "dateTimeShipment",
        header: ({ column }) => {
            return (
                <div className="flex items-center">
                    <p>Fecha de envio</p>
                    <Button
                        variant="ghost"
                        onClick={() =>
                            column.toggleSorting(column.getIsSorted() === "asc")
                        }
                        size={"icon"}
                    >
                        <ArrowUpDown className="h-4 w-4" />
                    </Button>
                </div>
            );
        },
        cell: ({ row }) => (
            <div className="">{(row.getValue("dateTimeShipment") as Date).toLocaleString()}</div>
        ),
    },
    {
        accessorKey: "destination",
        header: ({ column }) => {
            return (
                <div className="flex items-center">
                    <p>Destino</p>
                    <Button
                        variant="ghost"
                        onClick={() =>
                            column.toggleSorting(column.getIsSorted() === "asc")
                        }
                        size={"icon"}
                    >
                        <ArrowUpDown className="h-4 w-4" />
                    </Button>
                </div>
            );
        },
        cell: ({ row }) => (
            <div className="">{row.getValue("destination")}</div>
        ),
    },
    {
        accessorKey: "amountPackages",
        header: ({ column }) => {
            return (
                <div className="flex items-center justify-end">
                    <p>Cantidad de paquetes</p>
                    <Button
                        variant="ghost"
                        onClick={() =>
                            column.toggleSorting(column.getIsSorted() === "asc")
                        }
                        size={"icon"}
                    >
                        <ArrowUpDown className="h-4 w-4" />
                    </Button>
                </div>
            );
        },
        cell: ({ row }) => (
            <div className="text-right">{row.getValue("amountPackages")}</div>
        ),
    },
];

export function ShipmentTable({data}: {data: RowShipmentType[]}) {
    const [sorting, setSorting] = React.useState<SortingState>([]);
    const [columnFilters, setColumnFilters] =
        React.useState<ColumnFiltersState>([]);
    const [columnVisibility, setColumnVisibility] =
        React.useState<VisibilityState>({});
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
            <div className="flex items-center gap-4">
                <Input
                    placeholder="Filtrar envíos..."
                    value={
                        (table
                            .getColumn("id")
                            ?.getFilterValue() as string) ?? ""
                    }
                    onChange={(event) =>
                        table
                            .getColumn("id")
                            ?.setFilterValue(event.target.value)
                    }
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
                                        onCheckedChange={(value) =>
                                            column.toggleVisibility(!!value)
                                        }
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
                                        <TableHead key={header.id}>
                                            {header.isPlaceholder
                                                ? null
                                                : flexRender(
                                                      header.column.columnDef
                                                          .header,
                                                      header.getContext()
                                                  )}
                                        </TableHead>
                                    );
                                })}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {table.getRowModel().rows?.length ? (
                            table.getRowModel().rows.map((row) => (
                                <TableRow
                                    key={row.id}
                                    data-state={
                                        row.getIsSelected() && "selected"
                                    }
                                >
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id}>
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center"
                                >
                                    Sin información presente
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
            <div className="flex items-center justify-end space-x-2 py-4">
                {/* <div className="flex-1 text-sm text-muted-foreground">
                    {table.getFilteredSelectedRowModel().rows.length} de{" "}
                    {table.getFilteredRowModel().rows.length} fila(s) seleccionadas.
                </div> */}
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
