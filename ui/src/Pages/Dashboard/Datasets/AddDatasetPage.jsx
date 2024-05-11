import React, { useState } from "react";
import AddDataset from "../../../components/AddDataset";
import { Link, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import axios from "axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Loading";

const AddDatasetPage = () => {
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);

  const navigate = useNavigate();

  const {data: datasets, isLoading} = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/dataset`);
      const data = await res.data;
      console.log(data);
      return data;
    },
  });

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };

  const handleAddDataset = async (event) => {
    event.preventDefault();
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
      const url = `${process.env.REACT_APP_BASE_URL}/dataset`;
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
      console.log(columnData);

      const response = await axios.post(url, columnData, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log(response.data);
      if (response.status === 200) {
        toast.success("Datased Added Successfully");
        navigate("/datasets")
        event.target.reset();
        setColumns([{ columnName: "", columnType: "" }]);

      }
    } catch (err) {
      console.log(err);
    }
  };

  if(isLoading) return <Loading/>
  return (
    <div className="ps-7 pt-7">
      <h3 className="text-xl font-semibold">Create Dataset</h3>
      <div className="flex  justify-center">
        <form onSubmit={handleAddDataset} className="my-6 px-6 py-2 shadow-lg">
          <AddDataset columns={columns} handleAddColumn={handleAddColumn} />
          <div className="mt-[50px] flex gap-3 pb-4">
              <button
                className="btn bg-[#E5227A] btn-sm border border-[#E5227A] rounded text-white hover:bg-[#6b3b51d2]"
                type="submit"
              >
                Add
              </button>
              <Link
                to="/datasets"
                className="px-3 py-1 text-[14px] font-bold border-2 border-gray-300 rounded"
              >
                Cancel
              </Link>
            </div>
        </form>
      </div>
    </div>
  );
};

export default AddDatasetPage;
