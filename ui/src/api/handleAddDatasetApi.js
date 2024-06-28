import toast from "react-hot-toast";
import axiosApi from "./axios";

export const baseUrl = process.env.REACT_APP_BASE_URL;

export const handleAddDatasetApi = async (
  event,
  datasets,
  columns,
  successFunctions,
  apiFor,
) => {
  const datasetName = event.target.datasetName.value;
  const description = event.target.description.value;
  const isDuplicateName = datasets.some(
    (dataset) => dataset.name.toLowerCase() === datasetName.toLowerCase()
  );

  if (isDuplicateName) {
    toast.error(`A dataset with the name "${datasetName}" already exists.`);
    return;
  }

  const formData = new FormData(event.target);
  const columnValues = [];

  columns.forEach((column, index) => {
    const columnName = formData.get(`columnName${index}`);
    const columnType = formData.get(`columnType${index}`);

    columnValues.push({ columnName, columnType });
  });

  try {
    const columnData = {
      rows: 10000,
      name: datasetName,
      description,
      columns: columnValues.map((columnValue) => ({
        columnName: columnValue.columnName,
        columnType: {
          type: columnValue.columnType,
          isUnique: true,
        },
      })),
    };

    const response = await axiosApi.post("/dataset/create", columnData);
    if (response.status === 200) {
      if(apiFor === "dataset"){
        successFunctions();
      }else{
        successFunctions(response)
      }
    }
  } catch (err) {
    console.log(err);
  }
};