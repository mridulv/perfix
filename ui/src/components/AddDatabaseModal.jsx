import React, { useEffect, useState } from "react";
import { MdClose } from "react-icons/md";
import ChooseDatasetComponent from "./ChooseDatasetComponent";
import { IoIosArrowForward } from "react-icons/io";
import toast from "react-hot-toast";
import axios from "axios";
import AddDatabase from "./AddDatabase/AddDatabase";
import { handleAddDatasetApi } from "../utilities/api";

const AddDatabaseModal = ({ open, onClose, datasets, refetch }) => {
  const [selectedDatasetValue, setSelectedDatasetValue] = useState(null);
  const [currentAddStep, setCurrentAddStep] = useState(1);
  const [activeDataset, setActiveDataset] = useState("new");
  const [columns, setColumns] = useState([{ columnName: "", columnType: "" }]);
  const [selectedDatasetOption, setSelectedDatasetOption] = useState({
    option: "Choose Dataset",
    value: "choose",
  });
  const [selectedDataset, setSelectedDataset] = useState(null);

  const handleAddColumn = () => {
    setColumns([...columns, { columnName: "", columnType: "" }]);
  };
  const handleCloseModal = () => {
    onClose();
    setColumns([{ columnName: "", columnType: "" }]);
    setSelectedDatasetOption({ option: "Choose Dataset", value: "choose" });
    setSelectedDatasetValue(null);
    setCurrentAddStep(1);
    setSelectedDataset(null);
  };

  // choosing dataset or creating new dataset
  const handleSubmitDataset = async (event) => {
    event.preventDefault();

    if(activeDataset === "existing" && selectedDatasetOption.value === "choose"){
      return toast.error("Please select a dataset.")
    }

    //successfunction for choosing dataset
    if (
      activeDataset === "existing" &&
      currentAddStep === 1 &&
      selectedDatasetOption.value !== "choose"
    ) {
      setSelectedDatasetValue(selectedDatasetOption.value);
      toast.success("Dataset Selected!");
      setCurrentAddStep(2);
      setColumns([{ columnName: "", columnType: "" }]);
    }
    //success function for creating new dataset
    else {
      const successFunctions = (response) => {
        setSelectedDatasetValue(response.data.id);
        toast.success("New Dataset Created!");
        setCurrentAddStep(2);
        setColumns([{ columnName: "", columnType: "" }]);
      };

      await handleAddDatasetApi(event, datasets, columns, successFunctions);
    }
  };

  //fetching the dataset based on the selected or created new dataset
  useEffect(() => {
    if (selectedDatasetValue) {
      const fetchDataset = async () => {
        const res = await axios.get(
          `${process.env.REACT_APP_BASE_URL}/dataset/${selectedDatasetValue}`,
          {
            withCredentials: true,
          }
        );
        const data = await res.data;
        setSelectedDataset(data);
      };
      fetchDataset();
    }
  }, [selectedDatasetValue]);


  //when database added successfully
  const successFunctionsForDatabase = () => {
    toast.success("Database Added Successfully");
    refetch();
    handleCloseModal();
  }

  //when click cancel button

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
        <div className="min-h-[500px] ">
          <div className="mb-5 ms-6">
            <p className="text-xl font-semibold">Create new database</p>
          </div>
          <div className="w-full bg-secondary mb-9 ps-6 py-3 flex items-center gap-8">
            <div
              className={`w-11 ${
                currentAddStep === 1 && "bg-white"
              }  p-[10px] rounded-xl`}
            >
              <p className="w-6 h-6 bg-black rounded-full grid place-content-center text-sm text-white ">
                1
              </p>
            </div>
            <div>
              <IoIosArrowForward size={20} />
            </div>
            <div
              className={`w-11 ${
                currentAddStep === 2 && "bg-white"
              }  p-[10px] rounded-xl`}
            >
              <p className="w-6 h-6 bg-black rounded-full grid place-content-center text-sm text-white ">
                2
              </p>
            </div>
          </div>
          {currentAddStep === 1 && selectedDatasetValue === null && (
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

          {currentAddStep === 2 &&
            selectedDatasetValue !== null &&
            selectedDataset !== null && (
              <div className="">
                <AddDatabase
                  dataset={selectedDataset}
                  onClose={handleCloseModal}
                  refetch={refetch}
                  successFunction={successFunctionsForDatabase}
                />
              </div>
            )}
        </div>
      </div>
    </div>
  );
};

export default AddDatabaseModal;
