import React from "react";
import { FaArrowLeft } from "react-icons/fa6";
import { MdClose } from "react-icons/md";

const AddExperimentModal = ({ open, onClose, experiments }) => {


 

  return (
    <div
      onClick={onClose}
      className={`
        fixed inset-0 flex justify-center items-center transition-colors
        ${open ? "visible bg-black/20" : "invisible"} z-50 
      `}
    >
      {/* modal */}
      <div
        onClick={(e) => e.stopPropagation()}
        className={`
          bg-white rounded-xl shadow p-6 transition-all
          ${open ? "scale-100 opacity-100" : "scale-125 opacity-0"}
        `}
        style={{maxHeight: "80vh", overflow: "auto"}}
      >
        <button
          onClick={onClose}
          className="absolute top-2 right-2 p-1 rounded-lg text-gray-400 bg-white hover:bg-gray-50 hover:text-gray-600"
        >
         <MdClose size={20}/>
        </button>
        <div>
        <div className="ps-7 mb-5 flex items-center gap-3">
        <h2 className="text-[#8e8e8e] text-xl font-semibold">
          Create new Experiment /
        </h2>
        <h2 className="text-xl font-semibold">
          Experiment {experiments?.length + 1}
        </h2>
      </div>
        </div>
      </div>
    </div>
  );
};

export default AddExperimentModal;
