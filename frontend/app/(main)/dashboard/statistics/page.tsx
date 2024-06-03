"use client";
import React from 'react';
import Link from 'next/link';
import CapacityUsageStats from './components/CapacityUsageStats';
import StorageTimeStats from './components/StorageTimeStats';
import DistanceStats from './components/DistanceStats';
import CityRankings from './components/CityRankings';
import ShipmentStatusChart from './components/ShipmentStatusChart';
import DailyPackageStats from './components/DailyPackageStats';
import RouteEfficiencyStats from './components/RouteEfficiencyStats';

const StatisticsPage = () => {

  return (
    <div className="p-5 bg-gray-100 min-h-screen">
      <div className="flex items-center mb-5">
        <Link href="/dashboard" className="text-blue-500 hover:text-blue-700 mr-2">
          <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M11 5l-7 7 7 7m8-14l-7 7 7 7" />
          </svg>
        </Link>
        <h1 className="text-xl font-bold ">Estadísticas de simulación</h1>
      </div>

      <div className="grid grid-cols-5 gap-4">
        <div className="col-span-1">
          <CapacityUsageStats />
        </div>
        <div className="col-span-4 grid grid-cols-4 gap-4">
          <div className="max-h-56 bg-white p-4 shadow rounded-lg flex flex-col justify-center">
            <StorageTimeStats />
          </div>
          <div className="max-h-56 bg-white p-4 shadow rounded-lg flex flex-col justify-center">
            <DistanceStats />
          </div>
          <div className="max-h-56 bg-white p-4 shadow rounded-lg flex flex-col justify-center">
            <DailyPackageStats />
          </div>
          <div className="max-h-56 bg-white p-4 shadow rounded-lg flex flex-col justify-center">
            <RouteEfficiencyStats />
          </div>
        </div>
        <div className="col-span-5 grid grid-cols-2 gap-4">
          <CityRankings />
          <ShipmentStatusChart />
        </div>
      </div>
    </div>
  );
};

export default StatisticsPage;