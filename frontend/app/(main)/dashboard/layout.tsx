import React from 'react';
import { ReceptionPackageIdProvider } from '@/components/hooks/useReceptionPackageId';

export default function DashboardLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <ReceptionPackageIdProvider>
            {children}
        </ReceptionPackageIdProvider>
    );
}
