import React, { useEffect, useState } from "react";
import { MdClose } from "react-icons/md";
import { IoIosArrowForward } from "react-icons/io";
import toast from "react-hot-toast";
import axios from "axios";
import AddDatabase from "../AddDatabase/AddDatabase";
import ChooseDatasetComponent from "../Common/ChooseDatasetComponent";
import { handleAddDatasetApi } from "../../api/handleAddDatasetApi";
import { useStatesForAddModals } from "../../hooks/useStatesForAddModals";

const AddDatabaseModal = ({ open, onClose, datasets, refetch, databases }) => {
  const [currentAddStep, setCurrentAddStep] = useState(1);
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

  const handleCloseModal = () => {
    onClose();
    setCurrentAddStep(1);
    resetState();
  };

  // choosing dataset or creating new dataset
  const handleSubmitDataset = async (event) => {
    event.preventDefault();

    if (
      activeDataset === "existing" &&
      selectedDatasetOption.value === "choose"
    ) {
      return toast.error("Please select a dataset.");
    }

    //successfunction for choosing dataset
    if (
      activeDataset === "existing" &&
      currentAddStep === 1 &&
      selectedDatasetOption.value !== "choose"
    ) {
      setSelectedDatasetId(selectedDatasetOption.value);
      toast.success("Dataset Selected!");
      setCurrentAddStep(2);
      setColumns([{ columnName: "", columnType: "" }]);
    }
    //success function for creating new dataset
    else {
      const successFunctions = (response) => {
        setSelectedDatasetId(response.data.id);
        toast.success("New Dataset Created!");
        setCurrentAddStep(2);
        setColumns([{ columnName: "", columnType: "" }]);
      };

      await handleAddDatasetApi(event, datasets, columns, successFunctions);
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

  //when database added successfully
  const successFunctionsForDatabase = () => {
    toast.success("Database Added Successfully");
    refetch();
    handleCloseModal();
  };

  return (
    <div
      onClick={onClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50
      `}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className={`
          w-[35%] bg-white rounded-lg shadow py-6 transition-all
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{ maxHeight: "80vh", overflow: "auto" }}
      >
        <button
          onClick={handleCloseModal}
          className="absolute top-5 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
          <MdClose size={25} />
        </button>
        <div className="min-h-[500px]">
          <div className="mb-5 ms-6">
            <p className="text-xl font-semibold">Create new database</p>
          </div>
          <div className="w-full bg-secondary mb-9 ps-6 py-3 flex items-center gap-8">
            <div
              className={`w-11 ${currentAddStep === 1 && "bg-white"} p-[10px] rounded-xl`}
            >
              <p className="w-6 h-6 bg-black rounded-full grid place-content-center text-sm text-white">
                1
              </p>
            </div>
            <div>
              <IoIosArrowForward size={20} />
            </div>
            <div
              className={`w-11 ${currentAddStep === 2 && "bg-white"} p-[10px] rounded-xl`}
            >
              <p className="w-6 h-6 bg-black rounded-full grid place-content-center text-sm text-white">
                2
              </p>
            </div>
          </div>
          <div>
            {currentAddStep === 1 && selectedDatasetId === null && (
              <ChooseDatasetComponent
                activeDataset={activeDataset}
                setActiveDataset={setActiveDataset}
                columns={columns}
                handleAddColumn={handleAddColumn}
                handleSubmit={handleSubmitDataset}
                datasets={datasets}
                selectedDataset={selectedDatasetOption}
                setSelectedDataset={setSelectedDatasetOption}
              ></ChooseDatasetComponent>
            )}
          </div>
          {currentAddStep === 2 && selectedDatasetId !== null && selectedDatasetData !== null && (
            <div>
              <AddDatabase
                dataset={selectedDatasetData}
                cancelFunction={handleCloseModal}
                successFunction={successFunctionsForDatabase}
                databases={databases}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AddDatabaseModal;
