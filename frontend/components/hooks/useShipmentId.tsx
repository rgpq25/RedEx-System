"use client";

import React, { createContext, useState, useEffect, ReactNode } from 'react';

interface ShipmentIdContextType {
    shipmentId: string | undefined;
    setShipmentId: (shipmentId: string) => void;
}

const ShipmentIdContext = createContext<ShipmentIdContextType>({
    shipmentId: undefined,
    setShipmentId: () => {},
});

const ShipmentIdProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [shipmentId, setShipmentId] = useState<string | undefined>(() => {
        if (typeof window !== 'undefined') {
            const savedId = localStorage.getItem('shipmentId');
            return savedId || undefined;
        }
        return undefined;
    });

    useEffect(() => {
        if (shipmentId !== undefined) {
            localStorage.setItem('shipmentId', shipmentId.toString());
        }
    }, [shipmentId]);

    return (
        <ShipmentIdContext.Provider value={{ shipmentId, setShipmentId }}>
            {children}
        </ShipmentIdContext.Provider>
    );
};

export { ShipmentIdProvider, ShipmentIdContext };