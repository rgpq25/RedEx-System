"use client";
import React, { useState } from 'react';
import * as AlertDialog from '@radix-ui/react-alert-dialog';
import { AlmacenTable } from './_components/almacen-table'; // Ruta correcta al componente de la tabla

function AlmacenModalPage() {
    const [isOpen, setIsOpen] = useState(true); // Controla si el diálogo está abierto

    return (
        <AlertDialog.Root open={isOpen} onOpenChange={setIsOpen}>
            <AlertDialog.Trigger asChild>
                <button onClick={() => setIsOpen(true)}>Abrir información de almacén</button>
            </AlertDialog.Trigger>
            <AlertDialog.Portal>
                <AlertDialog.Overlay className="fixed inset-0 bg-black bg-opacity-50" />
                <AlertDialog.Content className="fixed p-5 bg-white rounded-lg shadow-xl top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
                    <h2 className="text-3xl font-semibold mb-4">Información de almacén</h2>
                    <div className="text-lg mb-4">
                        <div>Ubicación: Lima, Perú <span className="text-red-500">■</span><span className="text-red-500">■</span></div>
                        <div>Capacidad actual: 36/100  <span className="text-green-500">Bueno</span></div>
                    </div>
                    <AlmacenTable />
                    <AlertDialog.Action asChild>
                        <button onClick={() => setIsOpen(false)}>Cerrar</button>
                    </AlertDialog.Action>
                </AlertDialog.Content>
            </AlertDialog.Portal>
        </AlertDialog.Root>
    );
}


export default AlmacenModalPage;
