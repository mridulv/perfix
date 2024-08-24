export const generateSeriesData = (valuesArray, databaseInfo, colors) => {

  const result = valuesArray.map((values, index) => ({
    name: databaseInfo[index],
    type: "line",
    data: values,
    smooth: false,
    itemStyle: {
      color: colors[index % colors.length],
    },
  }));
  
  return result;
};

export const generateStatBlockData = (results, name, maxValue, valueKey) => {
  const values = results.map((result) => result.experimentResult[valueKey]);
  const result = [{ name, maxValue, values }];

  return result;
};
