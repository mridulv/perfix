import React, { useEffect, useState } from "react";
import axios from "axios";
import AddDatasetModal from "../../../components/AddDatasetModal";
import { BiTrash } from "react-icons/bi";
import { FaPlus } from "react-icons/fa6";
import { useQuery } from "react-query";


const Datasets = () => {
  const [datasets, setDatasets] = useState({});
  const [open, setOpen] = useState(false);

  // useEffect(() => {
  //   const fetchDatasets = async () => {
  //     const res = await axios.get("http://localhost:9000/dataset");
  //     const data = await res.data;
  //     console.log(data);
  //   };

  //   fetchDatasets();
  // }, []);

  const {data: datasets1, isLoading, refetch} = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const res = await axios.get("http://localhost:9000/dataset");
      const data = await res.data;
      console.log(data);
      return data;
    },
  });

  console.log(datasets1);

  const handleAddDataset = async (event) => {
    event.preventDefault();
    const form = event.target;
    const columnName = form.columnName.value;
    const columnType = form.columnType.value;
    console.log(columnName, columnType);

    try {
      const url = "http://localhost:9000/dataset";
      const data = {
        columnName,
        columnType,
      };
      const response = await axios.post(url, data, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      console.log(response.data);
      if(response.status === 200){
        refetch();
      }
    } catch (err) {
      console.log(err);
    }
  };

  if(isLoading) return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
            <span className="loading loading-spinner loading-lg"></span>
        </div>
  )
  return (
    <div className="px-3 py-4">
      <h2 className="text-2xl text-center font-bold text-orange-600 my-3">Datasets</h2>
      <div className="flex justify-end my-4 mx-8">
        <button className="btn btn-error text-white" onClick={() => setOpen(true)}>
          <FaPlus/> Add
        </button>
      </div>
      <AddDatasetModal open={open} onClose={() => setOpen(false)}>
        <div className=" w-[80%] md:w-[400px]">
          <div className="mx-auto my-4 w-[80%] md:w-[300px]">
            <h3 className="text-lg my-4 font-bold text-center">Add Dataset</h3>
            <form onSubmit={handleAddDataset}>
              <div className="flex flex-col my-4">
                <label htmlFor="">Column Name: </label>
                <input
                  className="border-2 border-gray-500 px-2 py-2 rounded mt-2"
                  placeholder="Name"
                  type="text"
                  name="columnName"
                  id=""
                  required
                />
              </div>
              <div className="flex flex-col my-4">
                <label htmlFor="">Column Type:</label>
                <select
                  id="column-type"
                  name="columnType"
                  className="block w-full mt-2 px-4 py-2 pr-8 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                >
                  <option value="NameType">NameType</option>
                  <option value="AddressType">AddressType</option>
                </select>
              </div>
              <div className="flex justify-center">
              <input type="submit" className="btn btn-primary text-center" value={"Submit"}/>
              </div>
            </form>
          </div>
        </div>
      </AddDatasetModal>
    </div>
  );
};

export default Datasets;
