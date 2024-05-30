import React, { useState } from "react";
import AddDataset from "../../../components/AddDataset";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Loading";
import { handleAddDatasetApi } from "../../../utilities/api";

const AddDatasetPage = () => {
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);

  const navigate = useNavigate();

  const { data: datasets, isLoading } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const values = [];
      const res = await axios.post(
        `${process.env.REACT_APP_BASE_URL}/dataset`,
        values,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
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
    const navigateFor = "datasetPage";
    handleAddDatasetApi(event, datasets, columns, setColumns, navigate, navigateFor);
  };

  if (isLoading) return <Loading />;
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
              to="/"
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
