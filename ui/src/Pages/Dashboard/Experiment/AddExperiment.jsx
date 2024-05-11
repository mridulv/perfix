import axios from "axios";
import React, { useState } from "react";
import { useQuery } from "react-query";
import { FaArrowLeft } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { IoIosArrowForward } from "react-icons/io";
import Loading from "../../../components/Loading";
import AddDataset from "../../../components/AddDataset";

const AddExperiment = () => {
  const [activeDataset, setActiveDataset] = useState("new");
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);

  const navigate = useNavigate();
  const { data: experiments, isLoading } = useQuery({
    queryKey: ["experiments"],
    queryFn: async () => {
      const res = await axios.get(
        `${process.env.REACT_APP_BASE_URL}/experiment`
      );
      const data = await res.data;
      return data;
    },
  });

  const { data: datasets, isLoading: datasetsLoading } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/dataset`);
      const data = await res.data;
      return data;
    },
  });

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    navigate("/add-experiment-database");
  };

  if (isLoading && datasetsLoading) return <Loading />;
  return (
    <div className="pt-8">
      <div className="ps-7 mb-5 flex items-center gap-3">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/experiment")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xl font-semibold">
          Create new Experiment /
        </h2>
        <h2 className="text-xl font-semibold">
          Experiment {experiments?.length + 1}
        </h2>
      </div>
      <div className="w-full bg-[#fbeaee] flex items-center gap-10">
        <div className="ps-7 py-3 ">
          <div
            className={`h-[45px] w-[180px] ps-3 bg-white  flex items-center gap-3 rounded-xl`}
          >
            <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
              <p className="text-[14px]">1</p>
            </div>
            <p className="font-bold text-[14px]">Setup Datasets</p>
          </div>
        </div>
        <IoIosArrowForward size={20} />
        <div
          className={`h-[45px] w-[180px] flex items-center gap-3 rounded-xl`}
        >
          <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
            <p className="text-[14px]">2</p>
          </div>
          <p className="font-bold text-[14px]">Setup Database</p>
        </div>
      </div>
      <div className="mt-7 ms-7 w-[320px]">
        <div className="bg-[#fbeaee] py-1 ps-3  flex items-center gap-3 rounded">
          <button
            onClick={() => setActiveDataset("existing")}
            className={`py-1 px-2 text-[12px] ${
              activeDataset === "existing" && "bg-white"
            } font-semibold rounded transition ease-in-out delay-150`}
          >
            Choose existing dataset
          </button>
          <button
            onClick={() => setActiveDataset("new")}
            className={`py-1 px-2 text-[12px] ${
              activeDataset === "new" && "bg-white"
            } font-semibold rounded transition ease-in-out delay-100`}
          >
            Create new dataset
          </button>
        </div>

        <div className="mt-6">
          <form onSubmit={handleSubmit}>
            {activeDataset === "new" ? (
              <AddDataset columns={columns} handleAddColumn={handleAddColumn} />
            ) : (
              <label className="form-control w-full max-w-xs">
                <div className="label">
                  <span className="label-text">Select Dataset</span>
                </div>
                <select
                  style={{ outline: "none" }}
                  name="datasetId"
                  className="select select-bordered block max-w-[250px] px-2 py-2 border-2 border-gray-300 rounded-md shadow-sm focus:outline-none focus:border-gray-500"
                  required
                >
                  {datasets && datasets.length > 0 ? (
                    datasets.map((dataset) => (
                      <option value={dataset.id.id} key={dataset.id.id}>
                        {dataset.name}
                      </option>
                    ))
                  ) : (
                    <option value="" disabled>
                      You haven't added any dataset
                    </option>
                  )}
                </select>
              </label>
            )}

            <div className="mt-[50px] flex gap-3 pb-4">
              <button
                className="btn bg-[#E5227A] btn-sm border border-[#E5227A] rounded text-white hover:bg-[#6b3b51d2]"
                type="submit"
              >
                Next
              </button>
              <Link
                to="/experiment"
                className="px-3 py-1 text-[14px] font-bold border-2 border-gray-300 rounded"
              >
                Cancel
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AddExperiment;
