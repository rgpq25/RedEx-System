// components/RouteEfficiencyStats.tsx
import React from 'react';

const RouteEfficiencyStats = () => {
  // Supongamos que la eficiencia de la ruta también se carga de una API o es estática por ahora
  const routeEfficiency = 77; // Porcentaje de eficiencia como ejemplo

  return (
    <div className="bg-white p-4 shadow rounded-lg flex flex-col items-center">
      <h2 className="text-lg font-semibold mb-2">Eficiencia promedio de rutas</h2>
      <div className="text-3xl font-bold">{routeEfficiency}%</div>
      <div className="text-sm text-gray-500">Eficiencia</div>
    </div>
  );
};

export default RouteEfficiencyStats;