import React from 'react';

const data = [
  { name: 'En almacén origen', value: 26.9, color: '#0088FE' },
  { name: 'Volando', value: 10.8, color: '#00C49F' },
  { name: 'En espera', value: 19.4, color: '#FFBB28' },
  { name: 'En almacén destino', value: 22.2, color: '#FF8042' },
  { name: 'Entregado', value: 11.8, color: '#9900FF' },
  { name: 'Atrasado', value: 10.2, color: '#FF4050' }
];

const PieChart = () => {
  const radius = 100; // Radio del gráfico
  let cumulativePercentage = 0;

  function getCoordinatesForPercent(percent: number) {
    const x = Math.cos(2 * Math.PI * percent);
    const y = Math.sin(2 * Math.PI * percent);
    return [x, y];
  }

  return (
    <div>
      <h2 className="text-lg font-semibold mb-2">Gráfica de estado de envíos</h2>
      <svg width="700" height="620" viewBox="-1 -1 2 2" style={{ transform: "rotate(-90deg)" }}>
        {data.map(slice => {
          // Calcula el porcentaje del segmento
          const [startX, startY] = getCoordinatesForPercent(cumulativePercentage);
          cumulativePercentage += slice.value / 100;
          const [endX, endY] = getCoordinatesForPercent(cumulativePercentage);
          const largeArcFlag = slice.value / 100 > 0.5 ? 1 : 0;
          const pathData = [
            `M ${startX} ${startY}`, // Mover
            `A 1 1 0 ${largeArcFlag} 1 ${endX} ${endY}`, // Arco
            `L 0 0` // Línea al centro
          ].join(' ');

          return (
            <path d={pathData} fill={slice.color} key={slice.name} />
          );
        })}
      </svg>
    </div>
  );
};

export default PieChart;