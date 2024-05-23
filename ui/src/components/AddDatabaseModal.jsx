import React from "react";
import { MdClose } from "react-icons/md";
import AddDatabase from "./AddDatabase";

const AddDatabaseModal = ({
  open,
  onClose,
  dataset,
  setSelectedDatabase,
  refetch,
}) => {
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
          bg-white rounded-lg shadow p-6 transition-all
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
        <div className="w-[300px] md:w-[460px] flex flex-col">
          <AddDatabase
            dataset={dataset}
            valueFor={"modal"}
            onClose={onClose}
            setSelectedDatabase={setSelectedDatabase}
            refetch={refetch}
          />
        </div>
      </div>
    </div>
  );
};

export default AddDatabaseModal;
