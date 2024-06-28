const getChartOptionsForMultiDatabase = (
  title,
  series,
  yAxisValues,
  selected
) => {
  return {
    title: {
      text: title,
    },
    legend: {
      show: false,
      selected: selected,
    },
    tooltip: {
      trigger: "axis",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    toolbox: {
      feature: {
        saveAsImage: {},
      },
    },
    xAxis: {
      type: "category",
      data: yAxisValues,
    },
    yAxis: {
      type: "value",
    },
    series: series,
  };
};

export const chartOptions = {
  getChartOptionsForMultiDatabase,
};
