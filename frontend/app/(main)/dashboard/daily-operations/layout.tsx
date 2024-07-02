import { FilteredFlightsProvider } from '@/components/contexts/flights-filter';
import React from 'react';

export default function DiaDiaLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <FilteredFlightsProvider>
            {children}
        </FilteredFlightsProvider>
    );
}