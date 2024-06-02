// components/DailyPackageStats.tsx
import React from 'react';

const DailyPackageStats = () => {
  // Aquí podrías cargar los datos reales desde una API o usar un estado estático
  const packagesDelivered = 132; // Este valor es un ejemplo

  return (
    <div className="bg-white p-4 shadow rounded-lg flex flex-col items-center">
      <h2 className="text-lg font-semibold mb-2">Paquetes entregados por día</h2>
      <div className="text-3xl font-bold">{packagesDelivered}</div>
      <div className="text-sm text-gray-500">Paquetes</div>
    </div>
  );
};

export default DailyPackageStats;