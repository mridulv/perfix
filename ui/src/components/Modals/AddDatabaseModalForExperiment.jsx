import React from "react";
import toast from "react-hot-toast";
import { MdClose } from "react-icons/md";
import AddDatabase from "../AddDatabase/AddDatabase";

const AddDatabaseModalForExperiment = ({
  open,
  onClose,
  seletedDataset,
  databases,
  refetch,
}) => {

  const handleClose = () => {
    onClose();
  }
  const successFunctionForAddDatabase = (response) => {
    toast.success("New Database Added Successfully!");
    refetch();
    onClose();
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
          bg-white rounded-lg shadow py-6 transition-all
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{ maxHeight: "80vh", overflow: "auto" }}
      >
        <button
          onClick={onClose}
          className="absolute top-5 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
          <MdClose size={25} />
        </button>
        <div className="w-[90%] md:w-[500px] flex flex-col">
          <div className="ps-4">
            <h3 className="text-[20px] font-bold">Create new database</h3>
          </div>
          <div className="w-[95%] h-[1px] bg-accent my-6"></div>
          <AddDatabase 
          dataset={seletedDataset}
          databases={databases}
          successFunction={successFunctionForAddDatabase}
          cancelFunction={handleClose}
          />
        </div>
      </div>
    </div>
  );
};

export default AddDatabaseModalForExperiment;
