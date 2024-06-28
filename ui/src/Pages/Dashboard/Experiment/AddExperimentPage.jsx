import React, { useEffect, useState } from "react";
import useExperiments from "../../../api/useExperiment";
import Loading from "../../../components/Common/Loading";
import { IoIosArrowForward } from "react-icons/io";
import { FaArrowLeft } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";
import ChooseDatasetComponent from "../../../components/Common/ChooseDatasetComponent";
import { useStatesForAddModals } from "../../../hooks/useStatesForAddModals";
import useDatasets from "../../../api/useDatasets";
import { handleAddDatasetApi } from "../../../api/handleAddDatasetApi";
import toast from "react-hot-toast";
import useDatabases from "../../../api/useDatabases";
import AddExperiment from "../../../components/AddExperiment/AddExperiment";
import axios from "axios";

const AddExperimentPage= () => {
  const [creatingStep, setCreatingStep] = useState("dataset");

  const {
    columns,
    setColumns,
    selectedDatasetOption,
    setSelectedDatasetOption,
    selectedDatasetId,
    setSelectedDatasetId,
    selectedDatasetData,
    setSelectedDatasetData,
    activeDataset,
    setActiveDataset,
    handleAddColumn,
  } = useStatesForAddModals("new");

  const navigate = useNavigate();

  const { data: experiments, isLoading } = useExperiments([]);
  const { data: datasets, isLoading: datasetsLoading } = useDatasets([]);
  const { data: databases, isLoading: databaseLoading, refetch: databaseRefetch } = useDatabases([]);

  const handleSubmitDataset = async (event) => {
    event.preventDefault();

    if (
      activeDataset === "existing" &&
      selectedDatasetOption.value === "choose"
    ) {
      return toast.error("Please select a dataset");
    }

    if (activeDataset === "new" && creatingStep === "dataset") {
      //for creating new dataset
      const successFunctions = (response) => {
        selectedDatasetId(response.data.id);
        toast.success("New Dataset Created!");
        setCreatingStep("database");
        setColumns([{ columnName: "", columnType: "" }]);
      };
      await handleAddDatasetApi(event, datasets, columns, successFunctions);
    } else {
      //for selecting existing dataset
      setSelectedDatasetId(selectedDatasetOption.value);
      toast.success("Dataset selected successfully!");
      setCreatingStep("database");
      setColumns([{ columnName: "", columnType: "" }]);
    }
  };

  //fetching the dataset based on the selected or created new dataset
  useEffect(() => {
    if (selectedDatasetId) {
      const fetchDataset = async () => {
        const res = await axios.get(
          `${process.env.REACT_APP_BASE_URL}/dataset/${selectedDatasetId}`,
          {
            withCredentials: true,
          }
        );
        const data = await res.data;
        setSelectedDatasetData(data);
      };
      fetchDataset();
    }
  }, [selectedDatasetId, setSelectedDatasetData]);

  if (isLoading && datasetsLoading && databaseLoading) return <Loading />;
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
            className={`h-[45px] w-[180px] ps-3  ${
              creatingStep === "dataset" && "bg-white"
            }  flex items-center gap-3 rounded-xl`}
          >
            <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
              <p className="text-[14px]">1</p>
            </div>
            <p className="font-bold text-[14px]">Setup Datasets</p>
          </div>
        </div>
        <IoIosArrowForward size={20} />
        <div
          className={`h-[45px] w-[180px] ps-3 ${
            creatingStep === "database" && "bg-white"
          } flex items-center gap-3 rounded-xl`}
        >
          <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
            <p className="text-[14px]">2</p>
          </div>
          <p className="font-bold text-[14px]">Setup Database</p>
        </div>
      </div>

      {creatingStep === "dataset" && (
        <div>
          <ChooseDatasetComponent
            activeDataset={activeDataset}
            setActiveDataset={setActiveDataset}
            handleAddColumn={handleAddColumn}
            columns={columns}
            datasets={datasets}
            selectedDataset={selectedDatasetOption}
            setSelectedDataset={setSelectedDatasetOption}
            handleSubmit={handleSubmitDataset}
          />
        </div>
      )}

      {creatingStep === "database" && selectedDatasetId && (
        <div>
          <AddExperiment
            databases={databases}
            dataset={selectedDatasetData}
            databaseRefetch={databaseRefetch}
          />
        </div>
      )}
    </div>
  );
};

export default AddExperimentPage;
