import React from 'react';

// Definiendo una interfaz para los datos de capacidad
interface CapacityData {
  city: string;
  percentage: number;
  airport: string;
}

const capacityData: CapacityData[] = [
  { city: "Madrid - España", percentage: 33, airport: "Aeropuerto de Barajas" },
  { city: "Vancouver, Canadá", percentage: 62, airport: "Aeropuerto Internacional de Vancouver" },
  { city: "Amsterdam, Países Bajos", percentage: 82, airport: "Aeropuerto de Schiphol" },
  { city: "Bangkok, Tailandia", percentage: 20, airport: "Aeropuerto de Suvarnabhumi" },
  { city: "Buenos Aires, Argentina", percentage: 33, airport: "Aeropuerto de Ezeiza" },
  { city: "Nairobi, Kenia", percentage: 33, airport: "Aeropuerto Internacional Jomo Kenyatta" },
  { city: "Seúl, Corea del Sur", percentage: 33, airport: "Aeropuerto Internacional de Incheon" },
  { city: "Río de Janeiro, Brasil", percentage: 33, airport: "Aeropuerto Internacional Galeão" },
  { city: "Sydney, Australia", percentage: 33, airport: "Aeropuerto de Kingsford Smith" },
  { city: "México City, México", percentage: 33, airport: "Aeropuerto Internacional Benito Juárez" },
  { city: "Berlín, Alemania", percentage: 33, airport: "Aeropuerto de Berlín Brandenburgo" },
  { city: "Roma, Italia", percentage: 33, airport: "Aeropuerto de Fiumicino" },
  { city: "Múnich, Alemania", percentage: 33, airport: "Aeropuerto de Múnich-Franz Josef Strauss" },
  { city: "Los Ángeles, Estados Unidos", percentage: 33, airport: "Aeropuerto Internacional de Los Ángeles" }
];

const getColor = (percentage: number): string => {
  if (percentage >= 80) return '#ff4d4d'; // Rojo para alta capacidad
  if (percentage >= 50) return '#ffd700'; // Amarillo para medio
  return '#90ee90'; // Verde para bajo
};

const CapacityUsageStats = () => {
  return (
    <div className="bg-white shadow rounded-lg p-4">
      <h2 className="font-bold text-lg mb-4">Promedio de capacidad usada por almacén</h2>
      <ul>
        {capacityData.map((data, index) => (
          <li key={index} style={{
            marginBottom: '6px',
          }}>
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              fontWeight: 'bold' // Aplica negrita a todo el texto dentro del div

            }}>
              <span>{data.city}</span>
              <span style={{
                backgroundColor: getColor(data.percentage),
                color: 'white',
                padding: '3px 6px',
                borderRadius: '4px'
              }}>{data.percentage}%</span>
            </div>
            <div style={{ fontSize: 'small', opacity: 0.8 }}>{data.airport}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};


export default CapacityUsageStats;