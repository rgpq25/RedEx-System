"use client";

import { Vuelo } from "@/lib/types";
import React, { createContext, useContext, useState } from "react";

interface FilteredFlightsContextType {
    filteredFlights: Vuelo[] | undefined;
    setFilteredFlights: React.Dispatch<React.SetStateAction<Vuelo[] | undefined>>;
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
    const [filteredFlights, setFilteredFlights] = useState<Vuelo[] | undefined>(undefined);

    return <FilteredFlightsContext.Provider value={{ filteredFlights, setFilteredFlights }}>{children}</FilteredFlightsContext.Provider>;
};
