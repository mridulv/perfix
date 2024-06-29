import React, { useEffect, useState } from "react";
import { MdClose } from "react-icons/md";
import toast from "react-hot-toast";
import { IoIosArrowForward } from "react-icons/io";
import AddDatabase from "../AddDatabase/AddDatabase";
import axios from "axios";
import { handleAddDatasetApi } from "../../api/handleAddDatasetApi";
import ChooseDatasetComponent from "../Common/ChooseDatasetComponent";
import AddExperiment from "../AddExperiment/AddExperiment";
import { useStatesForAddModals } from "../../hooks/useStatesForAddModals";
const AddExperimentModal = ({
  open,
  onClose,
  experiments,
  datasets,
  databases,
  databaseRefetch,
}) => {
  const [creationState, setCreationState] = useState("setUpDataset");
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
    resetState,
  } = useStatesForAddModals("new");

  const handleClose = () => {
    onClose();

    setCreationState("setUpDataset");
    resetState();
  };

  const handleSubmitDataset = async (event) => {
    event.preventDefault();

    if (
      activeDataset === "existing" &&
      selectedDatasetOption.value === "choose"
    ) {
      return toast.error("Please select a dataset");
    }

    if (activeDataset === "new" && creationState === "setUpDataset") {
      //for creating new dataset
      const successFunctions = (response) => {
        selectedDatasetId(response.data.id);
        toast.success("New Dataset Created!");
        setCreationState("setUpDatabase");
        setColumns([{ columnName: "", columnType: "" }]);
      };
      await handleAddDatasetApi(event, datasets, columns, successFunctions);
    } else {
      //for selecting existing dataset
      setSelectedDatasetId(selectedDatasetOption.value);
      toast.success("Dataset selected successfully!");
      setCreationState("setUpDatabase");
      setColumns([{ columnName: "", columnType: "" }]);
    }
  };

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

  const successFunctionForAddDatabase = (response) => {
    toast.success("New Database Added Successfully!");
    setCreationState("setUpDatabase");
    databaseRefetch();
  };
  return (
    <div
      onClick={handleClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50 
      `}
    >
      {/* modal */}
      <div
        onClick={(e) => e.stopPropagation()}
        className={`
          bg-white rounded-xl shadow transition-all
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{ maxHeight: "80vh", overflow: "auto" }}
      >
        <button
          onClick={onClose}
          className="absolute top-2 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
          <MdClose size={20} />
        </button>
        <div className="w-[95%] md:w-[600px] py-6">
          <div className="ps-6 mb-5 flex items-center gap-3">
            <h2 className="text-[#8e8e8e] text-xl font-semibold">
              Create new Experiment /
            </h2>
            <h2 className="text-xl font-semibold">
              Experiment {experiments?.length + 1}
            </h2>
          </div>
          <div className="w-[95%] h-[1px] bg-accent my-6"></div>

          {/* try to make this thing common */}
          <div className="w-full bg-secondary flex items-center gap-10">
            <div className="ps-7 py-3 ">
              <div
                className={`h-[45px] w-[180px] ps-3 ${
                  creationState === "setUpDataset" && "bg-white"
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
              className={`h-[45px] w-[180px] ps-3  ${
                creationState === "setUpDatabase" && "bg-white"
              } flex items-center gap-3 rounded-xl`}
            >
              <div className="h-6 w-6 rounded-full bg-black text-white flex justify-center items-center">
                <p className="text-[14px]">2</p>
              </div>
              <p className="font-bold text-[14px]">Setup Database</p>
            </div>
          </div>
          <div>
            {creationState === "setUpDataset" && (
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
            )}

            {creationState === "setUpDatabase" && selectedDatasetId && (
              <AddExperiment
                databases={databases}
                datasetId={selectedDatasetData}
                setCreationState={setCreationState}
              />
            )}

            {creationState === "setUpAddDatabase" &&
              selectedDatasetId &&
              selectedDatasetData && (
                <AddDatabase
                  dataset={selectedDatasetData}
                  databases={databases}
                  successFunction={successFunctionForAddDatabase}
                  cancelFunction={handleClose}
                />
              )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddExperimentModal;
