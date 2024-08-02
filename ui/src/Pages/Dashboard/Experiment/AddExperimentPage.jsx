/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import useExperiments from "../../../api/useExperiment";
import Loading from "../../../components/Common/Loading";
import { FaArrowLeft } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { useStatesForAddModals } from "../../../hooks/useStatesForAddModals";
import toast from "react-hot-toast";
import AddExperiment from "../../../components/AddExperiment/AddExperiment";
import axios from "axios";
import fetchDatabaseCategory from "../../../api/fetchDatabaseCategory";
import CustomSelect from "../../../components/CustomSelect/CustomSelect";
import fetchRelevantDatasets from "../../../api/fetchRelevantDatasets";
import StepHeader from "../../../components/AddExperiment/StepHeader";
import useRelevantDatabases from "../../../api/fetchRelevantDatabases";

const AddExperimentPage = () => {
  const [creatingStep, setCreatingStep] = useState("category");
  const [databaseCategories, setDatabaseCategories] = useState([]);
  const [selectedDatabaseCategory, setSelectedDatabaseCategory] = useState({
    option: "Choose Category",
    value: "Choose",
  });
  const [relevantDatasets, setRelevantDatasets] = useState(null);
  const [selectedDataset, setSelectedDataset] = useState({
    option: "Choose Dataset",
    value: "Choose",
  });

  const {
    selectedDatasetId,
    setSelectedDatasetId,
    selectedDatasetData,
    setSelectedDatasetData,
  } = useStatesForAddModals("new");

  const navigate = useNavigate();

  const { data: experiments, isLoading } = useExperiments([]);

  // handle the select database category
  const handleSelectCategory = () => {
    if (selectedDatabaseCategory.value === "Choose") {
      return toast.error("Please select a database category");
    }

    setCreatingStep("dataset");
  };
  // making the options for select database category
  useEffect(() => {
    fetchDatabaseCategory(setDatabaseCategories);
  }, []);

  const databaseCategoriesOptions =
    databaseCategories &&
    Object.keys(databaseCategories)?.map((category) => ({
      option: category,
      value: category,
    }));

  //fetching the relevant databases
  const {
    data: relevantDatabases,
    isLoading: databasesLoading,
    refetch: databaseRefetch,
  } = useRelevantDatabases(
    { category: selectedDatabaseCategory.value, datasetId: selectedDatasetId },
    {
      enabled: creatingStep === "database" && selectedDatasetId !== null && selectedDatabaseCategory.value !== "Choose"
    }
  );
  //making the options for selecting relevant databases
  const databasesOptions =
    relevantDatabases &&
    relevantDatabases?.map((database) => ({
      option: database.databaseConfigName,
      value: database?.databaseConfigId?.id,
    }));

  //fetching the relevant datasets
  useEffect(() => {
    if (selectedDatabaseCategory.value !== "Choose") {
      fetchRelevantDatasets(
        selectedDatabaseCategory.value,
        setRelevantDatasets
      );
    }
  }, [selectedDatabaseCategory]);

  //making the options for selecting relevant dataset
  const datasetsOptions =
    relevantDatasets &&
    relevantDatasets?.map((dataset) => ({
      option: dataset.datasetName,
      value: dataset?.datasetId?.id,
    }));

  const handleSelectDataset = () => {
    if (selectedDataset.value === "Choose") {
      return toast.error("Please select a dataset.");
    }

    setCreatingStep("database");
    setSelectedDatasetId(selectedDataset.value);
  };

  //fetching the dataset data based on the selected  dataset
  useEffect(() => {
    if (selectedDatasetId) {
      const fetchDataset = async () => {
        const res = await axios.get(
          `${import.meta.env.VITE_BASE_URL}/dataset/${selectedDatasetId}`,
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

  if (isLoading && databasesLoading) return <Loading />;
  return (
    <div className="pt-8 flex flex-col min-h-screen">
      <div className="ps-1 md:ps-7 mb-5 flex items-center gap-3 md:gap-0 -tracking-tighter">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/experiment")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xs  md:text-xl font-semibold">
          Create new Experiment /
        </h2>
        <h2 className="text-xs md:text-xl font-semibold">
          Experiment {experiments?.length + 1}
        </h2>
      </div>
      {/* step headers texts */}
      <StepHeader creatingStep={creatingStep} />

      {/* first step select the database category */}
      {creatingStep === "category" && (
        <div className="mt-7 ms-7 flex-grow relative">
          <p className="text-[12px] font-bold mb-1 block">
            Choose Database Category
          </p>
          <div>
            <CustomSelect
              selected={selectedDatabaseCategory}
              setSelected={setSelectedDatabaseCategory}
              options={databaseCategoriesOptions}
              width="w-[250px]"
            />
          </div>
          <div className="mt-auto pt-4 pb-6 flex gap-3 absolute bottom-0 left-0">
            <button
              className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
              onClick={handleSelectCategory}
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
        </div>
      )}

      {/* selecting relevant dataset step */}
      {creatingStep === "dataset" && relevantDatasets && (
        <div className="mt-7 ms-7 flex-grow relative">
          <p className="text-[12px] font-bold mb-1 block">Choose Dataset</p>
          <div>
            <CustomSelect
              selected={selectedDataset}
              setSelected={setSelectedDataset}
              options={datasetsOptions}
              width="w-[250px]"
            />
          </div>
          <div className="mt-auto pt-4 pb-6 flex gap-3 absolute bottom-0 left-0">
            <button
              className="btn bg-primary btn-sm border border-primary rounded text-white hover:bg-[#57B1FF]"
              onClick={handleSelectDataset}
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
        </div>
      )}

      {/* final step */}
      {creatingStep === "database" && selectedDatasetId && (
        <div>
          <AddExperiment
            databases={relevantDatabases}
            dataset={selectedDatasetData}
            databaseRefetch={databaseRefetch}
            databasesOptions={databasesOptions}
            selectedDatabaseCategory={selectedDatabaseCategory}
          />
        </div>
      )}
    </div>
  );
};

export default AddExperimentPage;
