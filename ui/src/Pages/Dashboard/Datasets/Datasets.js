import React, { useState } from "react";
import axios from "axios";
import AddDatasetModal from "../../../components/AddDatasetModal";
import { FaPlus } from "react-icons/fa6";
import { useQuery } from "react-query";
import toast from "react-hot-toast";
import DatasetCard from "../../../components/DatasetCard";

const Datasets = () => {
  
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);
  const [open, setOpen] = useState(false);


  const {
    data: datasets,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/dataset");
      const data = await res.data;
      console.log(data);
      return data;
    },
  });

  console.log(datasets);
  

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };

  const handleAddDataset = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.target);
    const columnValues = [];

    columns.forEach((column, index) => {
      const columnName = formData.get(`columnName${index}`);
      const columnType = formData.get(`columnType${index}`);
      
      columnValues.push({ columnName, columnType });
    });

    console.log(columnValues);
    try {
      const url = "http://localhost:9000/dataset";
      const columnData = {
        rows: 10000,
        columns: columnValues.map(columnValue => ({
          columnName: columnValue.columnName,
          columnType: {
            type: columnValue.columnType,
            isUnique: true // Assuming isUnique is always true for all columns
          }
        }))
      };
      console.log(columnData);
    
      const response = await axios.post(url, columnData, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log(response.data);
      if (response.status === 200) {
        setOpen(false);
        event.target.reset();
        toast.success("Datased Added Successfully");
        refetch();
      }
    } catch (err) {
      console.log(err);
    }
  };

  if (isLoading)
    return (
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
        }}
      >
        <span className="loading loading-spinner loading-lg"></span>
      </div>
    );
  return (
    <div className="px-3 py-4">
      <h2 className="text-2xl text-center font-bold text-orange-600 my-3">
        Datasets
      </h2>
      <div className="flex justify-end my-4 mx-8">
        <button
          className="btn btn-error text-white"
          onClick={() => setOpen(true)}
        >
          <FaPlus /> Add
        </button>
      </div>
      <AddDatasetModal open={open} onClose={() => setOpen(false)}>
        <div className=" w-[280px] md:w-[400px] ">
          <div className="mx-auto my-4 w-[80%] md:w-[300px]">
            <h3 className="text-lg my-4 font-bold text-center">Add Dataset</h3>
            <form onSubmit={handleAddDataset}>
              {columns.map((column, i) => (
                <div key={i}>
                  <p className="text-sm">{i + 1}.</p>
                  <div className="flex flex-col mb-4">
                    <label htmlFor="">Column Name: </label>
                    <input
                      className="border-2 border-gray-500 px-2 py-2 rounded mt-2"
                      placeholder="Name"
                      type="text"
                      name={`columnName${i}`}
                      id={`columnName${i}`}
                      required
                    />
                  </div>
                  <div className="flex flex-col my-4">
                    <label htmlFor="">Column Type:</label>
                    <select
                      name={`columnType${i}`}
                      id={`columnType${i}`}
                      className="block w-full mt-2 px-4 py-2 pr-8 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                    >
                      <option value="NameType">NameType</option>
                      <option value="AddressType">AddressType</option>
                    </select>
                  </div>
                </div>
              ))}
              <div className="flex justify-end">
                <button
                  onClick={handleAddColumn}
                  className="btn btn-xs  btn-error text-white"
                >
                  Add Column
                </button>
              </div>
              <div className="flex justify-center mt-4">
                <input
                  type="submit"
                  className="btn btn-primary text-center"
                  value={"Add Dataset"}
                />
              </div>
            </form>
          </div>
        </div>
      </AddDatasetModal>

      <div className="w-[90%] mx-auto grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {
          datasets.map((dataset, i) => (
            <DatasetCard key={dataset.id.id} dataset={dataset} index={i}></DatasetCard>
          ))
        }
      </div>
    </div>
  );
};

export default Datasets;
