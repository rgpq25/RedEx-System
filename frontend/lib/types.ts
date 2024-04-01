export type PackageStatusName =
    | "En almacen origen"
    | "Volando"
    | "En espera"
    | "En almacen destino"
    | "Entregado";

export type PackageStatusVariant = "gray" | "blue" | "yellow" | "purple" | "green";

export type RowPackageType = {
    id: string;
    origin: string;
    currentLocation: string;
    destination: string;
    statusName: PackageStatusName;
    statusVariant: PackageStatusVariant;
};
