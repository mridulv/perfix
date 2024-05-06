import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);



const labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
// const dataset1Data = [200, 350, 500, 600, 450, 300, 400];
// const dataset2Data = [150, 300, 450, 550, 400, 250, 350];
// const dataset3Data = [100, 250, 400, 500, 350, 200, 300];

const dataset1Data = Array.from({ length: labels.length }).map((_, index) =>
  labels[index] === 'June' ? 300 : null
);
const dataset2Data = Array.from({ length: labels.length }).map((_, index) =>
  labels[index] === 'June' ? 250 : null
);
const dataset3Data = Array.from({ length: labels.length }).map((_, index) =>
  labels[index] === 'June' ? 200 : null
);

const data = {
  labels,
  datasets: [
    {
      label: 'Dataset 1',
      data: dataset1Data,
      backgroundColor: '#5b84b0',
      legend: {
        position: "right",
      }
    },
    {
      label: 'Dataset 2',
      data: dataset2Data,
      backgroundColor: '#fc766a',
    },
    {
      label: 'Dataset 3',
      data: dataset3Data,
      backgroundColor: '#d197c7',
    },
  ],
};


const options = {
    plugins: {
      title: {
        display: true,
        text: 'Chart.js Bar Chart - Stacked',
      },
      legend: {
        position: 'right', // This will position the legend on the right side
        display: true,
        labels: {
          usePointStyle: true,
          pointStyle: "circle"
        }
      },
    },
    responsive: true,
    scales: {
      x: {
        stacked: true,
      },
      y: {
        stacked: true,
        grid:{
          lineWidth: 0,
        }
      },
    },
    
  };

// const function DashboardChart() {

//   return 
// }

const DashboardChart = () => {
    return <Bar options={options} data={data} />;
     
};

export default DashboardChart;