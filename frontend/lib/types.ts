export type PackageStatusName =
    | "En almacen origen"
    | "Volando"
    | "En espera"
    | "En almacen destino"
    | "Entregado";


export type AlmacenStatusName =
    | "En almacen origen"
    | "Volando"
    | "En espera"
    | "En almacen destino"
    | "Entregado";

export type PackageStatusVariant = "gray" | "blue" | "yellow" | "purple" | "green";

export type AlmacenStatusVariant = "gray" | "blue" | "yellow" | "purple" | "green";

export type RowPackageType = {
    id: string;
    origin: string;
    currentLocation: string;
    destination: string;
    statusName: PackageStatusName;
    statusVariant: PackageStatusVariant;
};

export type RowAlmacenType = {
    id: string;
    packets: number;
    origin: string;
    destination: string;
    statusAlmacen: AlmacenStatusName;
    statusVariant: AlmacenStatusVariant;
};

export type RowVuelosType = {
    id: string;
    packets: number;
    origin: string;
    destination: string;
};


export type RowPackageRouteType = {
    id: string;
    origin: string;
    timeOrigin: string | null;
    destination: string;
    timeDestination: string | null;
    isCurrent: boolean;
}


export type RoleType = "admin" | "operator" | "user" | null;