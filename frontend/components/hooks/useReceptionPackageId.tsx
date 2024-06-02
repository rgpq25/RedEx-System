"use client";

import React, { createContext, useState, useEffect, ReactNode } from 'react';

interface ReceptionPackageIdContextType {
    receptionPackageId: string | undefined;
    setReceptionPackageId: (receptionPackageId: string) => void;
}

const ReceptionPackageIdContext = createContext<ReceptionPackageIdContextType>({
    receptionPackageId: undefined,
    setReceptionPackageId: () => {},
});

const ReceptionPackageIdProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [receptionPackageId, setReceptionPackageId] = useState<string | undefined>(() => {
        if (typeof window !== 'undefined') {
            const savedId = localStorage.getItem('receptionPackageId');
            return savedId || undefined;
        }
        return undefined;
    });

    useEffect(() => {
        if (receptionPackageId !== undefined) {
            localStorage.setItem('receptionPackageId', receptionPackageId.toString());
        }
    }, [receptionPackageId]);

    return (
        <ReceptionPackageIdContext.Provider value={{ receptionPackageId, setReceptionPackageId }}>
            {children}
        </ReceptionPackageIdContext.Provider>
    );
};

export { ReceptionPackageIdProvider, ReceptionPackageIdContext };