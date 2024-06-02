"use client";
import React from 'react';
import { useRouter } from 'next/router';
import CapacityUsageStats from './components/CapacityUsageStats';
import StorageTimeStats from './components/StorageTimeStats';
import DistanceStats from './components/DistanceStats';
import CityRankings from './components/CityRankings';
import ShipmentStatusChart from './components/ShipmentStatusChart';
import DailyPackageStats from './components/DailyPackageStats';
import RouteEfficiencyStats from './components/RouteEfficiencyStats';


const router = useRouter();
const StatisticsPage = () => {

  return (
    <div className="p-5 bg-gray-100 min-h-screen">
      <button className="mb-5 text-blue-500 hover:text-blue-700" onClick={() => router.back()}>
        ← Regresar
      </button>
      <h1 className="text-xl font-bold mb-5">Estadísticas de simulación</h1>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-1">
          <CapacityUsageStats />
        </div>
        <div className="lg:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-4">
          <StorageTimeStats />
          <DistanceStats />
          <DailyPackageStats />
          <RouteEfficiencyStats />
        </div>
        <div className="lg:col-span-3 grid grid-cols-1 md:grid-cols-2 gap-4">
          <CityRankings />
          <ShipmentStatusChart />
        </div>
      </div>
    </div>
  );
};

export default StatisticsPage;