export const generateSeriesData = (valuesArray, databaseInfo, colors) => {
    return valuesArray.map((values, index) => ({
      name: databaseInfo[index],
      type: "line",
      stack: "Total",
      data: values,
      itemStyle: {
        color: colors[index % colors.length],
      },
    }));
  };
  
  export const generateStatBlockData = (results, name, maxValue, valueKey) => {
    const values = results.map((result) => result.experimentResult[valueKey]);
    return [{ name, maxValue, values }];
  };