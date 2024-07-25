import { useEffect, useState } from "react";
import useExperiments from "../../../api/useExperiment";
import Loading from "../../../components/Common/Loading";
import { IoIosArrowForward } from "react-icons/io";
import { FaArrowLeft } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { useStatesForAddModals } from "../../../hooks/useStatesForAddModals";
import toast from "react-hot-toast";
import useDatabases from "../../../api/useDatabases";
import AddExperiment from "../../../components/AddExperiment/AddExperiment";
import axios from "axios";
import fetchDatabaseCategory from "../../../api/fetchDatabaseCategory";
import CustomSelect from "../../../components/CustomSelect/CustomSelect";
import fetchRelevantDatasets from "../../../api/fetchRelevantDatasets";

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

  // const { data: experiments, isLoading } = useExperiments([]);
  const {
    data: databases,
    isLoading: databaseLoading,
    refetch: databaseRefetch,
  } = useDatabases([]);

  const databasesOptions =
    databases &&
    databases.map((database) => ({
      option: database.name,
      value: database.databaseConfigId.id,
    }));

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
    Object.keys(databaseCategories).map((category) => ({
      option: category,
      value: category,
    }));

  // const handleSubmitDataset = async (event) => {
  //   event.preventDefault();

  //   if (
  //     activeDataset === "existing" &&
  //     selectedDatasetOption.value === "choose"
  //   ) {
  //     return toast.error("Please select a dataset");
  //   }

  //   if (activeDataset === "new" && creatingStep === "dataset") {
  //     //for creating new dataset
  //     const successFunctions = (response) => {
  //       selectedDatasetId(response.data.id);
  //       toast.success("New Dataset Created!");
  //       setCreatingStep("database");
  //       setColumns([{ columnName: "", columnType: "" }]);
  //     };
  //     await handleAddDatasetApi(event, datasets, columns, successFunctions);
  //   } else {
  //     //for selecting existing dataset
  //     setSelectedDatasetId(selectedDatasetOption.value);
  //     toast.success("Dataset selected successfully!");
  //     setCreatingStep("database");
  //     setColumns([{ columnName: "", columnType: "" }]);
  //   }
  // };

  //fetching the relevant datasets
  useEffect(() => {
    if (selectedDatabaseCategory.value !== "Choose") {
      fetchRelevantDatasets(
        selectedDatabaseCategory.value,
        setRelevantDatasets
      );
    }
  }, [selectedDatabaseCategory]);
  console.log(relevantDatasets);

  //making the options for selecting relevant dataset
  const datasetsOptions =
    relevantDatasets &&
    relevantDatasets?.map((dataset) => ({
      option: dataset.databaseConfigName,
      value: dataset?.databaseConfigId?.id,
    }));

  const handleSelectDataset = () => {
    if (selectedDataset.value === "Choose") {
      return toast.error("Please select a dataset.");
    }

    setCreatingStep("database");
    setSelectedDatasetId(selectedDataset.value);
  };
  //fetching the dataset based on the selected or created new dataset
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

  if ( databaseLoading) return <Loading />;
  return (
    <div className="pt-8 flex flex-col min-h-screen">
      <div className="ps-7 mb-5 flex items-center gap-3 -tracking-tighter">
        <FaArrowLeft
          className="cursor-pointer"
          onClick={() => navigate("/experiment")}
          size={20}
        />
        <h2 className="text-[#8e8e8e] text-xl font-semibold">
          Create new Experiment /
        </h2>
        <h2 className="text-xl font-semibold">
          {/* Experiment {experiments?.length + 1} */}
        </h2>
      </div>
      <div className="w-full bg-secondary flex items-center gap-10">
        <div className="ps-7 py-3 ">
          <div
            className={`h-[45px] w-[220px] ps-3  ${
              creatingStep === "category" && "bg-white"
            }  flex items-center gap-3 rounded-xl`}
          >
            <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
              <p className="text-[14px]">1</p>
            </div>
            <p className="font-bold text-[12px]">Select Database Category</p>
          </div>
        </div>
        <IoIosArrowForward size={20} />
        <div className="ps-7 py-3 ">
          <div
            className={`h-[45px] w-[180px] ps-3  ${
              creatingStep === "dataset" && "bg-white"
            }  flex items-center gap-3 rounded-xl`}
          >
            <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
              <p className="text-[14px]">1</p>
            </div>
            <p className="font-bold text-[12px]">Setup Datasets</p>
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
          <p className="font-bold text-[12px]">Setup Database</p>
        </div>
      </div>

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
              width="w-[200px]"
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

      {/* second step select the dataset */}
      {/* {creatingStep === "dataset" && (
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
            reduceHeight={"200px"}
          />
        </div>
      )} */}
      {creatingStep === "dataset" && relevantDatasets && (
        <div className="mt-7 ms-7 flex-grow relative">
          <p className="text-[12px] font-bold mb-1 block">
            Choose Dataset
          </p>
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

      {creatingStep === "database" && selectedDatasetId && (
        <div>
          <AddExperiment
            databases={databases}
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
