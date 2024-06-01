import axios from "axios";
import React, { useState } from "react";
import { useQuery } from "react-query";
import { FaArrowLeft } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";
import { IoIosArrowForward } from "react-icons/io";
import Loading from "../../../components/Loading";
import { handleAddDatasetApi } from "../../../utilities/api";
import ChooseDatasetComponent from "../../../components/ChooseDatasetComponent";
import toast from "react-hot-toast";

const AddExperiment = () => {
  const [activeDataset, setActiveDataset] = useState("new");
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);
  const [selectedDataset, setSelectedDataset] = useState({
    option: "Choose Type",
    value: "Choose",
  });

  const navigate = useNavigate();
  const { data: experiments, isLoading } = useQuery({
    queryKey: ["experiments"],
    queryFn: async () => {
      const values = [];
      const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/experiment`, values, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      const data = await res.data;
      return data;
    },
  });

  const { data: datasets, isLoading: datasetsLoading } = useQuery({
    queryKey: ["datasets"],
    queryFn: async () => {
      const values = [];
      const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/dataset`, values, {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      });
      const data = await res.data;
      return data;
    },
  });

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (selectedDataset.value === "Choose") {
      return toast.error("Please select a dataset");
    }
    const datasetId = selectedDataset.value;

    if (activeDataset === "new") {
      const navigateFor = "experimentPage";
      handleAddDatasetApi(
        event,
        datasets,
        columns,
        setColumns,
        navigate,
        navigateFor
      );
    } else {
      navigate(`/add-experiment-database/${datasetId}`);
    }
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
      <div className="w-full bg-secondary flex items-center gap-10">
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
      <ChooseDatasetComponent
        activeDataset={activeDataset}
        setActiveDataset={setActiveDataset}
        handleSubmit={handleSubmit}
        columns={columns}
        handleAddColumn={handleAddColumn}
        datasets={datasets}
        selectedDataset={selectedDataset}
        setSelectedDataset={setSelectedDataset}
      />
    </div>
  );
};

export default AddExperiment;
