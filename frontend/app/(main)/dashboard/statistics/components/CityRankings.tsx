// components/CityRankings.tsx
import React from 'react';

const CityRankings = () => {
  return (
    <div className="bg-white shadow rounded-lg p-4">
      <h2 className="font-bold text-lg mb-4">Ranking de ciudades con mayores paradas</h2>
      <ul>
        {/* Las ciudades serán dinámicas */}
        <li>London: 33</li>
        <li>Sao Paulo: 33</li>
        {/* Más ciudades */}
      </ul>
    </div>
  );
};

export default CityRankings;