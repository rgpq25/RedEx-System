"use client";

import { continentes } from "@/lib/sample";
import { Vuelo } from "@/lib/types";
import React, { createContext, useContext, useMemo, useState } from "react";

const DEFAULT_RANGO_CAPACIDAD_VUELOS: [number, number] = [0, 600];

interface FilteredFlightsContextType {
	search: string;
	setSearch: React.Dispatch<React.SetStateAction<string>>;
	hasSearchFilter: boolean;
	continentesFilter: string[];
	setContinentesFilter: React.Dispatch<React.SetStateAction<string[]>>;
	paisOrigenFilter: string | undefined;
	setPaisOrigenFilter: React.Dispatch<React.SetStateAction<string | undefined>>;
	paisDestinoFilter: string | undefined;
	setPaisDestinoFilter: React.Dispatch<React.SetStateAction<string | undefined>>;
	rangoCapacidadFilter: [number, number];
	setRangoCapacidadFilter: React.Dispatch<React.SetStateAction<[number, number]>>;
	minCapacidad: number;
	maxCapacidad: number;
    getFilteredFlights: (vuelos: Vuelo[]) => Vuelo[];
}

const FilteredFlightsContext = createContext<FilteredFlightsContextType | undefined>(undefined);

export const useFilteredFlightsContext = () => {
	const context = useContext(FilteredFlightsContext);
	if (context === undefined) {
		throw new Error("useVuelosContext must be used within a VuelosProvider");
	}
	return context;
};

export const FilteredFlightsProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
	// const [filteredFlights, setFilteredFlights] = useState<Vuelo[]>([]);
	const [search, setSearch] = useState<string>("");
	const hasSearchFilter = Boolean(search);
	const [continentesFilter, setContinentesFilter] = useState<string[]>(continentes);
	const [paisOrigenFilter, setPaisOrigenFilter] = useState<string | undefined>(undefined);
	const [paisDestinoFilter, setPaisDestinoFilter] = useState<string | undefined>(undefined);
	const [rangoCapacidadFilter, setRangoCapacidadFilter] = useState<[number, number]>(DEFAULT_RANGO_CAPACIDAD_VUELOS);

	const minCapacidad = Math.min(...rangoCapacidadFilter);
	const maxCapacidad = Math.max(...rangoCapacidadFilter);

	const getFilteredFlights = (vuelos: Vuelo[]) => {
		let filteredVuelos = [...vuelos];
		if (continentesFilter) {
			filteredVuelos = filteredVuelos?.filter((vuelo) => {
				return continentesFilter?.includes(vuelo.planVuelo.ciudadOrigen.continente);
			});
		}
		if (paisOrigenFilter) {
			filteredVuelos = filteredVuelos?.filter((vuelo) => vuelo.planVuelo.ciudadOrigen.pais === paisOrigenFilter);
		}
		if (paisDestinoFilter) {
			filteredVuelos = filteredVuelos?.filter((vuelo) => vuelo.planVuelo.ciudadDestino.pais === paisDestinoFilter);
		}
		if (rangoCapacidadFilter) {
			filteredVuelos = filteredVuelos?.filter((vuelo) => vuelo.capacidadUtilizada >= minCapacidad && vuelo.capacidadUtilizada <= maxCapacidad);
		}
		if (hasSearchFilter) {
			filteredVuelos = filteredVuelos?.filter((vuelo) => vuelo.id.toString().includes(search));
		}
		return filteredVuelos;
	};

	return (
		<FilteredFlightsContext.Provider
			value={{
				search,
				setSearch,
				hasSearchFilter,
				continentesFilter,
				setContinentesFilter,
				paisOrigenFilter,
				setPaisOrigenFilter,
				paisDestinoFilter,
				setPaisDestinoFilter,
				rangoCapacidadFilter,
				setRangoCapacidadFilter,
				minCapacidad,
				maxCapacidad,
                getFilteredFlights
			}}
		>
			{children}
		</FilteredFlightsContext.Provider>
	);
};
